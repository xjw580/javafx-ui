import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Splits ordinary JavaFX CSS selector groups into default and explicit theme resources.
 * Run as a JDK 21 source-file program; no application or third-party dependencies are needed.
 */
public final class GenerateThemeCss {
    private static final String MARKER = "/* @theme-variants */";
    private static final Path RESOURCE_PATH = Path.of("club/xiaojiawei/controls/css/themes");
    private static final Pattern EXPLICIT_THEME = Pattern.compile(
            "(?:\\.[\\w-]+-ui-|:ui-)(base|windows)(?![\\w-])");
    private static final Pattern IMPORT = Pattern.compile("@import\\s+[\"']([^\"']+)[\"']\\s*;");

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java tools/GenerateThemeCss.java <source> <output>");
        }
        generate(Path.of(args[0]), Path.of(args[1]));
    }

    static void generate(Path source, Path output) throws IOException {
        source = source.toAbsolutePath().normalize();
        output = output.toAbsolutePath().normalize();
        if (!Files.isDirectory(source) || output.startsWith(source) || source.startsWith(output)) {
            throw new IllegalArgumentException("Source must exist and source/output trees must not overlap");
        }
        Map<Path, byte[]> generated = new LinkedHashMap<>();
        int variants = 0;
        try (var files = Files.walk(source)) {
            for (Path file : files.filter(Files::isRegularFile).sorted().toList()) {
                Path relative = source.relativize(file);
                byte[] bytes = Files.readAllBytes(file);
                String css = Files.readString(file, StandardCharsets.UTF_8);
                Path target = RESOURCE_PATH.resolve(relative);
                if (!css.startsWith(MARKER)) {
                    addOutput(generated, target, bytes);
                    continue;
                }
                if (relative.getNameCount() != 2
                    || !List.of("base", "windows").contains(relative.getName(0).toString())) {
                    throw new IllegalArgumentException(file + ": variants must be directly inside a theme directory");
                }
                Variants split = split(css, relative.getName(0).toString(), relative.toString());
                addOutput(generated, target, split.defaults().getBytes(StandardCharsets.UTF_8));
                addOutput(generated, target.getParent().resolve("explicit").resolve(target.getFileName()),
                        split.explicit().getBytes(StandardCharsets.UTF_8));
                variants++;
            }
        }
        if (variants == 0) {
            throw new IllegalArgumentException("No theme variant sources found in " + source);
        }
        validateImports(generated);

        // Validate the entire input before updating output, so bad sources fail the build.
        Files.createDirectories(output);
        Path manifest = output.resolveSibling(output.getFileName() + ".manifest");
        if (Files.exists(manifest)) {
            for (String old : Files.readAllLines(manifest, StandardCharsets.UTF_8)) {
                Path relative = Path.of(old);
                Path stale = output.resolve(relative).normalize();
                if (relative.isAbsolute() || !stale.startsWith(output) || stale.equals(output)) {
                    throw new IOException("Invalid generated resource in manifest: " + old);
                }
                if (!generated.containsKey(relative)) {
                    Files.deleteIfExists(stale);
                }
            }
        }
        for (var entry : generated.entrySet()) {
            Path file = output.resolve(entry.getKey());
            Files.createDirectories(file.getParent());
            Files.write(file, entry.getValue());
        }
        Files.write(manifest, generated.keySet().stream().map(Path::toString).toList(), StandardCharsets.UTF_8);
        System.out.printf("Generated %d theme resources from %d variant sources%n", generated.size(), variants);
    }

    private static void addOutput(Map<Path, byte[]> generated, Path path, byte[] bytes) {
        if (generated.putIfAbsent(path, bytes) != null) {
            throw new IllegalArgumentException("Multiple sources generate the same resource: " + path);
        }
    }

    private static void validateImports(Map<Path, byte[]> generated) {
        for (var entry : generated.entrySet()) {
            var matcher = IMPORT.matcher(stripComments(new String(entry.getValue(), StandardCharsets.UTF_8)));
            while (matcher.find()) {
                Path imported = entry.getKey().getParent().resolve(matcher.group(1)).normalize();
                if (!generated.containsKey(imported)) {
                    throw new IllegalArgumentException(entry.getKey() + ": missing import " + matcher.group(1));
                }
            }
        }
    }

    record Variants(String defaults, String explicit) {
    }

    static Variants split(String css, String theme, String name) {
        try {
            return new Parser(css, theme, name).parse();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(name + ": " + ex.getMessage(), ex);
        }
    }

    private static String stripComments(String text) {
        StringBuilder result = new StringBuilder();
        char quote = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quote != 0) {
                result.append(c);
                if (c == '\\' && i + 1 < text.length()) {
                    result.append(text.charAt(++i));
                } else if (c == quote) {
                    quote = 0;
                }
            } else if (c == '\'' || c == '"') {
                quote = c;
                result.append(c);
            } else if (c == '/' && i + 1 < text.length() && text.charAt(i + 1) == '*') {
                int end = text.indexOf("*/", i + 2);
                if (end < 0) {
                    throw new IllegalArgumentException("Unclosed CSS comment");
                }
                result.append(' ');
                i = end + 1;
            } else {
                result.append(c);
            }
        }
        if (quote != 0) {
            throw new IllegalArgumentException("Unclosed CSS string");
        }
        return result.toString();
    }

    private static final class Parser {
        private final String css;
        private final String theme;
        private final String header;
        private int position;

        Parser(String css, String theme, String name) {
            this.css = stripComments(css);
            this.theme = theme;
            this.header = "/* Generated from " + name.replace('\\', '/') + "; do not edit. */\n\n";
        }

        Variants parse() {
            StringBuilder defaults = new StringBuilder(header);
            StringBuilder explicit = new StringBuilder(header);
            while (position < css.length()) {
                skipWhitespace();
                if (position == css.length()) {
                    break;
                }
                if (css.charAt(position) == '@') {
                    String directive = readUntil(';') + ";";
                    if (!IMPORT.matcher(directive).matches()) {
                        throw new IllegalArgumentException("Only local @import directives are supported: " + directive);
                    }
                    // Imports in a combined source belong to its default stylesheet.
                    defaults.append(directive).append("\n\n");
                    continue;
                }
                String selectorText = readUntil('{');
                String declarations = readUntil('}').strip();
                List<String> normalSelectors = new ArrayList<>();
                List<String> explicitSelectors = new ArrayList<>();
                for (String selector : selectors(selectorText)) {
                    var matcher = EXPLICIT_THEME.matcher(selector);
                    boolean isExplicit = false;
                    while (matcher.find()) {
                        if (!matcher.group(1).equals(theme)) {
                            throw new IllegalArgumentException("Foreign theme selector: " + selector);
                        }
                        isExplicit = true;
                    }
                    (isExplicit ? explicitSelectors : normalSelectors).add(selector);
                }
                appendRule(defaults, normalSelectors, declarations);
                appendRule(explicit, explicitSelectors, declarations);
            }
            return new Variants(defaults.toString(), explicit.toString());
        }

        private static void appendRule(StringBuilder output, List<String> selectors, String declarations) {
            if (!selectors.isEmpty()) {
                output.append(String.join(",\n", selectors)).append(" {\n    ")
                        .append(declarations).append("\n}\n\n");
            }
        }

        private static List<String> selectors(String text) {
            Parser scanner = new Parser(text + ",", "", "");
            List<String> selectors = new ArrayList<>();
            while (scanner.position < scanner.css.length()) {
                String selector = scanner.readUntil(',');
                if (selector.isBlank()) {
                    throw new IllegalArgumentException("Empty CSS selector");
                }
                selectors.add(selector);
            }
            return selectors;
        }

        private void skipWhitespace() {
            while (position < css.length() && Character.isWhitespace(css.charAt(position))) {
                position++;
            }
        }

        /** Finds a delimiter outside strings, functions and attribute selectors. */
        private String readUntil(char delimiter) {
            int start = position;
            char quote = 0;
            List<Character> brackets = new ArrayList<>();
            while (position < css.length()) {
                char c = css.charAt(position++);
                if (quote != 0) {
                    if (c == '\\' && position < css.length()) {
                        position++;
                    } else if (c == quote) {
                        quote = 0;
                    }
                    continue;
                }
                if (c == '\'' || c == '"') {
                    quote = c;
                } else if (c == '(' || c == '[') {
                    brackets.add(c);
                } else if (c == ')' || c == ']') {
                    char expected = c == ')' ? '(' : '[';
                    if (brackets.isEmpty() || brackets.removeLast() != expected) {
                        throw new IllegalArgumentException("Mismatched CSS brackets near offset " + position);
                    }
                } else if (brackets.isEmpty()) {
                    if (c == delimiter) {
                        return css.substring(start, position - 1).strip();
                    }
                    if (c == '{' || c == '}') {
                        throw new IllegalArgumentException("Unexpected CSS brace near offset " + position);
                    }
                }
            }
            throw new IllegalArgumentException("Expected '" + delimiter + "' near offset " + position);
        }
    }
}
