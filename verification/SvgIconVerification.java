import club.xiaojiawei.controls.Time;
import club.xiaojiawei.controls.ico.AbstractIco;
import club.xiaojiawei.controls.ico.AddIco;
import club.xiaojiawei.svg.SvgView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Real scene-graph regression checks; works with a desktop or test-only Monocle. */
public final class SvgIconVerification {
    private record Paints(javafx.scene.paint.Paint fill, javafx.scene.paint.Paint stroke, double strokeWidth) {}
    private static int assertions;
    private static Pane root;
    private static final List<String> initialPaintMismatches = new ArrayList<>();
    private static void check(boolean value, String message) {
        assertions++;
        if (!value) throw new AssertionError(message);
    }
    private static List<Shape> shapes(Node node) {
        List<Shape> result = new ArrayList<>();
        if (node instanceof Shape shape) result.add(shape);
        if (node instanceof Parent parent) for (Node child : parent.getChildrenUnmodifiable()) result.addAll(shapes(child));
        return result;
    }
    private static List<Paints> paints(Node node) {
        return shapes(node).stream().map(shape -> new Paints(shape.getFill(), shape.getStroke(), shape.getStrokeWidth())).toList();
    }
    private static void layout(AbstractIco icon) {
        root.applyCss();
        icon.resize(icon.prefWidth(-1), icon.prefHeight(-1));
        icon.layout();
    }
    private static long[] pixels(Node node) {
        SnapshotParameters p = new SnapshotParameters(); p.setFill(Color.TRANSPARENT);
        WritableImage image = node.snapshot(p, null);
        long opaque = 0, red = 0, blue = 0, black = 0;
        for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++) {
            Color color = image.getPixelReader().getColor(x, y);
            if (color.getOpacity() > .05) {
                opaque++;
                if (color.getRed() > .9 && color.getGreen() < .1 && color.getBlue() < .1) red++;
                if (color.getBlue() > .9 && color.getGreen() < .1 && color.getRed() < .1) blue++;
                if (color.getRed() < .1 && color.getGreen() < .1 && color.getBlue() < .1) black++;
            }
        }
        return new long[]{opaque, red, blue, black};
    }
    private static void verifyIcon(String name) throws Exception {
        AbstractIco icon = (AbstractIco) Class.forName("club.xiaojiawei.controls.ico." + name).getConstructor(String.class).newInstance((String) null);
        SvgView originalView = (SvgView) icon.getChildren().getFirst();
        var originalPaints = paints(originalView);
        root.getChildren().setAll(icon);
        layout(icon);
        if (!originalPaints.equals(paints(originalView))) initialPaintMismatches.add(name);
        check(icon.getChildren().size() == 1 && icon.getChildren().getFirst() instanceof SvgView, name + " uses one SvgView");
        SvgView view = (SvgView) icon.getChildren().getFirst();
        check(view.getDocument() != null, name + " document loaded");
        // Illustrator emits legacy background accumulation metadata. These icons
        // have no filters referencing BackgroundImage, so it is intentionally inert.
        check(view.getDiagnostics().stream().allMatch(d -> d.getMessage().equals("Unsupported SVG CSS property: enable-background")),
                name + " unexpected SVG diagnostics: " + view.getDiagnostics());
        double w = view.prefWidth(-1), h = view.prefHeight(-1);
        check(w > 0 && h > 0 && Double.isFinite(w) && Double.isFinite(h), name + " finite natural size");
        long[] original = pixels(icon);
        check(original[0] > 0, name + " renders visible pixels");
        icon.setColor("#ff0000"); layout(icon);
        long[] red = pixels(icon);
        check(red[1] > 0 && red[1] == red[0], name + " red recolors all painted pixels " + java.util.Arrays.toString(red));
        icon.setColor("#0000ff"); layout(icon);
        long[] blue = pixels(icon);
        check(blue[2] > 0 && blue[2] == blue[0], name + " blue recolors all painted pixels " + java.util.Arrays.toString(blue));
        icon.setColor(null); layout(icon);
        check(originalPaints.equals(paints(view)), name + " restores source fill/stroke/width after color reset: old="
                + originalPaints + " new=" + paints(view));
        check(pixels(icon)[0] > 0, name + " renders after color reset");
        if (List.of("LinkDirIco", "LockIco", "StartIco").contains(name)) {
            for (Shape shape : shapes(view)) {
                var bounds = view.sceneToLocal(shape.localToScene(shape.getBoundsInLocal()));
                check(bounds.getMinX() >= -0.01 && bounds.getMinY() >= -0.01
                        && bounds.getMaxX() <= view.getWidth() + 0.01 && bounds.getMaxY() <= view.getHeight() + 0.01,
                        name + " painted geometry inside viewport: " + bounds + " raw=" + shape.getBoundsInLocal() + " strokeWidth=" + shape.getStrokeWidth());
            }
        }
        icon.resize(1, 1); icon.layout();
        check(Math.abs(view.getWidth() - w) <= 1 && Math.abs(view.getHeight() - h) <= 1, name + " does not shrink in small container");
        icon.resize(w * 4, h * 4); icon.layout();
        check(Math.abs(view.getWidth() - w) <= 1 && Math.abs(view.getHeight() - h) <= 1, name + " does not expand in large container");
        AbstractIco second = (AbstractIco) icon.getClass().getConstructor().newInstance();
        check(second.getChildren().getFirst() != view, name + " independent scene graph");
        check(((SvgView) second.getChildren().getFirst()).getDocument() == view.getDocument(), name + " shared cached document");
        // Existing no-argument constructors may intentionally select black.
        // They must not be confused with the explicit null/source-paint mode.
        root.getChildren().setAll(second); layout(second);
        long[] defaults = pixels(second);
        check(defaults[0] > 0, name + " default constructor renders");
        boolean defaultBlack = List.of("BrainIco", "NextIco", "NextPageIco", "PinterIco", "ScreenshotIco", "TrendIco").contains(name);
        check(java.util.Objects.equals(defaultBlack ? "black" : null, second.getColor()), name + " retains original default color contract");
        if (defaultBlack) check(defaults[3] == defaults[0], name + " preserves default black theme");
    }
    private static String stylesheet(String css) {
        return "data:text/css;charset=utf-8;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.UTF_8));
    }
    private static void verifyStylesheets() {
        AddIco icon = new AddIco();
        String first = stylesheet(".unused-first { -fx-opacity: 0.9; }");
        String second = stylesheet(".unused-second { -fx-opacity: 0.8; }");
        icon.getStylesheets().setAll(first, second);
        icon.setColor("red");
        check(icon.getStylesheets().containsAll(List.of(first, second)), "preserve caller styles on first color");
        icon.getStylesheets().remove(first); icon.getStylesheets().addFirst(first);
        icon.setColor("blue");
        check(icon.getStylesheets().size() == 3 && icon.getStylesheets().containsAll(List.of(first, second)), "preserve caller styles after reorder");
        icon.setColor(" ");
        check(icon.getStylesheets().equals(List.of(first, second)), "remove only owned stylesheet");
    }
    private static void verifyFxml() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?import club.xiaojiawei.controls.ico.AddIco?><AddIco xmlns:fx=\"http://javafx.com/fxml\" color=\"icon-theme-color\"/>";
        AbstractIco icon = new FXMLLoader().load(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        root.getChildren().setAll(icon); root.setStyle("icon-theme-color: #ff0000;"); layout(icon);
        check(pixels(icon)[1] > 0, "FXML theme token resolves");
        root.setStyle("icon-theme-color: #0000ff;"); layout(icon);
        check(pixels(icon)[2] > 0, "FXML theme token follows change");
        Time time = new Time();
        root.getChildren().setAll(time); root.setStyle(""); root.applyCss(); root.layout();
        List<AbstractIco> icons = new ArrayList<>(); collectIcons(time, icons);
        AbstractIco timeIcon = icons.stream().filter(i -> i.getClass().getSimpleName().equals("TimeIco")).findFirst().orElseThrow();
        check("main-shallow-color".equals(timeIcon.getColor()), "actual Time.fxml preserves theme token");
        check(pixels(timeIcon)[0] > 0, "actual Time.fxml icon renders");
    }
    private static void collectIcons(Node node, List<AbstractIco> out) {
        if (node instanceof AbstractIco icon) out.add(icon);
        if (node instanceof Parent p) for (Node c : p.getChildrenUnmodifiable()) collectIcons(c, out);
    }
    private static void contactSheet(List<String> names, Path file) throws Exception {
        Pane sheet = new Pane(); sheet.setStyle("-fx-background-color: white;");
        root.getChildren().setAll(sheet);
        for (int i = 0; i < names.size(); i++) {
            AbstractIco icon = (AbstractIco) Class.forName("club.xiaojiawei.controls.ico." + names.get(i)).getConstructor().newInstance();
            icon.relocate((i % 10) * 90 + 35, (i / 10) * 56 + 5);
            javafx.scene.text.Text label = new javafx.scene.text.Text(names.get(i));
            label.setFont(javafx.scene.text.Font.font(8));
            label.relocate((i % 10) * 90 + 2, (i / 10) * 56 + 35);
            sheet.getChildren().addAll(icon, label);
        }
        sheet.setPrefSize(900, 448); sheet.resize(900, 448); root.applyCss(); sheet.layout();
        WritableImage image = sheet.snapshot(null, null);
        var buffered = new java.awt.image.BufferedImage((int) image.getWidth(), (int) image.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++) buffered.setRGB(x, y, image.getPixelReader().getArgb(x, y));
        javax.imageio.ImageIO.write(buffered, "png", file.toFile());
    }
    public static void main(String[] args) throws Exception {
        List<String> names;
        try (var files = Files.list(Path.of(args[0]))) {
            names = files.map(p -> p.getFileName().toString()).filter(n -> n.endsWith(".java") && !n.equals("AbstractIco.java"))
                    .map(n -> n.substring(0, n.length() - 5)).sorted().toList();
        }
        check(names.size() == 74, "expected 74 built-in icons, found " + names.size());
        CountDownLatch done = new CountDownLatch(1); AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.startup(() -> Platform.runLater(() -> {
            try {
                root = new Pane(); new Scene(root, 600, 300);
                for (String name : names) verifyIcon(name);
                verifyStylesheets(); verifyFxml();
                check(initialPaintMismatches.isEmpty(), "Source paints changed on first CSS application: " + initialPaintMismatches);
                if (args.length > 1) contactSheet(names, Path.of(args[1]));
                System.out.println("PASS icons=" + names.size() + " assertions=" + assertions);
            } catch (Throwable e) { failure.set(e); }
            finally { done.countDown(); }
        }));
        if (!done.await(90, TimeUnit.SECONDS)) throw new AssertionError("JavaFX verification timed out");
        Platform.exit();
        if (failure.get() != null) throw new AssertionError("SVG icon verification failed", failure.get());
    }
}
