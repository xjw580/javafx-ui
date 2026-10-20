import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/** Compile together with tools/GenerateThemeCss.java; requires only JDK 21. */
public final class ThemeCssGenerationVerification {
    private static int assertions;

    public static void main(String[] args) throws Exception {
        verifySelectorsAndValues();
        verifyRejectedSources();
        verifyResourceLifecycle();
        System.out.println("Theme CSS generation: " + assertions + " assertions passed");
    }

    private static void verifySelectorsAndValues() {
        var split = GenerateThemeCss.split("""
                /* @theme-variants */
                @import "../shared/common.css";
                .btn-ui,
                .btn-ui.btn-ui-windows,
                .btn-ui:ui-windows {
                    -fx-shape: "M0,0 /* literal */ {x};";
                    -fx-background-color: rgba(1, 2, 3, .5), derive(blue, -20%);
                }
                .btn-ui-windows-primary { -fx-padding: 5; }
                .btn-ui.btn-ui-windows:focused:hover { -fx-border-width: 1; }
                """, "windows", "windows/button.css");
        require(split.defaults().contains(".btn-ui {"), "default selector");
        require(!split.defaults().contains(".btn-ui.btn-ui-windows"), "explicit selector must not leak");
        require(!split.explicit().contains("\n.btn-ui {"), "default selector must not leak");
        require(split.explicit().contains(".btn-ui:ui-windows"), "composite control pseudo-class");
        require(split.explicit().contains(":focused:hover"), "combined state specificity");
        require(split.defaults().contains(".btn-ui-windows-primary"), "standalone variant is not a force suffix");
        require(!split.explicit().contains(".btn-ui-windows-primary"), "suffix boundary");
        require(split.defaults().contains("@import"), "default import");
        require(!split.explicit().contains("@import"), "import stays out of explicit stylesheet");
        for (String css : new String[]{split.defaults(), split.explicit()}) {
            require(css.contains("\"M0,0 /* literal */ {x};\""), "quoted content preserved");
            require(css.contains("rgba(1, 2, 3, .5), derive(blue, -20%)"), "functions preserved");
        }
        var base = GenerateThemeCss.split(
                ".btn-ui, .btn-ui.btn-ui-base { -fx-padding: 8 14; }", "base", "base/button.css");
        require(base.explicit().contains(".btn-ui.btn-ui-base"), "base selector");
    }

    private static void verifyRejectedSources() {
        for (String invalid : new String[]{
                "/* unfinished",
                ".btn-ui { -fx-shape: \"unfinished; }",
                ".btn-ui { -fx-padding: 1;",
                ".btn-ui { nested { -fx-padding: 1; } }",
                ".btn-ui { -fx-background-color: rgba(1, 2]; }",
                ".btn-ui, { -fx-padding: 1; }",
                ".btn-ui.btn-ui-base { -fx-padding: 1; }",
                "@media all { .btn-ui { -fx-padding: 1; } }"
        }) {
            expectFailure(() -> GenerateThemeCss.split(invalid, "windows", "windows/button.css"));
        }
    }

    private static void verifyResourceLifecycle() throws Exception {
        Files.createDirectories(Path.of("target"));
        Path workspace = Files.createTempDirectory(Path.of("target"), "theme-css-verification-");
        try {
            Path source = workspace.resolve("source");
            Path output = workspace.resolve("output");
            Path button = source.resolve("windows/button.css");
            Files.createDirectories(button.getParent());
            Files.writeString(button, """
                    /* @theme-variants */
                    .btn-ui, .btn-ui.btn-ui-windows { -fx-padding: 7 14; }
                    """);
            Files.writeString(source.resolve("windows/index.css"), """
                    @import "button.css";
                    @import "explicit/button.css";
                    """);
            Files.writeString(source.resolve("extra.css"), ".extra { -fx-padding: 1; }");
            GenerateThemeCss.generate(source, output);
            Path resources = output.resolve("club/xiaojiawei/controls/css/themes");
            require(Files.exists(resources.resolve("windows/button.css")), "default file generated");
            require(Files.exists(resources.resolve("windows/explicit/button.css")), "explicit file generated");
            require(Files.readString(resources.resolve("windows/index.css"))
                    .equals(Files.readString(source.resolve("windows/index.css"))), "import order preserved");
            String before = Files.readString(resources.resolve("windows/button.css"));
            GenerateThemeCss.generate(source, output);
            require(before.equals(Files.readString(resources.resolve("windows/button.css"))), "deterministic output");
            Path secondSource = workspace.resolve("second-source");
            Files.createDirectories(secondSource.resolve("windows"));
            Files.copy(button, secondSource.resolve("windows/button.css"));
            Path secondOutput = workspace.resolve("second-output");
            GenerateThemeCss.generate(secondSource, secondOutput);
            require(before.equals(Files.readString(secondOutput.resolve(
                    "club/xiaojiawei/controls/css/themes/windows/button.css"))), "build location independent output");

            Path duplicate = source.resolve("windows/explicit/button.css");
            Files.createDirectories(duplicate.getParent());
            Files.writeString(duplicate, ".duplicate { -fx-padding: 1; }");
            expectFailure(() -> GenerateThemeCss.generate(source, output));
            Files.delete(duplicate);
            Files.delete(source.resolve("extra.css"));
            GenerateThemeCss.generate(source, output);
            require(!Files.exists(resources.resolve("extra.css")), "obsolete generated file removed");

            Files.writeString(source.resolve("windows/index.css"), "@import \"missing.css\";");
            expectFailure(() -> GenerateThemeCss.generate(source, output));
            require(before.equals(Files.readString(resources.resolve("windows/button.css"))),
                    "broken input must fail before overwriting output");

            expectFailure(() -> GenerateThemeCss.generate(source, source.resolve("nested-output")));
            Path manifest = output.resolveSibling("output.manifest");
            Files.writeString(source.resolve("windows/index.css"), "@import \"button.css\";");
            Files.writeString(manifest, "../outside.css\n");
            expectFailure(() -> GenerateThemeCss.generate(source, output));
        } finally {
            try (var files = Files.walk(workspace)) {
                for (Path file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(file);
                }
            }
        }
    }

    private static void require(boolean condition, String message) {
        assertions++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void expectFailure(CheckedAction action) {
        assertions++;
        try {
            action.run();
        } catch (IllegalArgumentException | IOException expected) {
            return;
        }
        throw new AssertionError("Invalid input should fail");
    }

    private interface CheckedAction {
        void run() throws IOException;
    }
}
