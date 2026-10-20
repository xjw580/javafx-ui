import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runs against real JavaFX skins. Pass the directory containing base.css and windows.css.
 * Requires the built library and JavaFX 21 on the classpath and a desktop session.
 */
public final class ControlSizingVerification {
    private record Mode(String entry, String suffix) {
    }

    private record Sample(Control control, String styleClass) {
    }

    private static final String TEXT = "\u6d4b\u8bd5 Ag";
    private static final List<Mode> MODES = List.of(
            new Mode("base", ""), new Mode("base", "base"), new Mode("base", "windows"),
            new Mode("windows", ""), new Mode("windows", "windows"), new Mode("windows", "base"),
            new Mode("base", "base-scope"), new Mode("base", "windows-scope"),
            new Mode("windows", "base-scope"), new Mode("windows", "windows-scope"));
    private static final List<String> SIZES = List.of("tiny", "small", "default", "big");
    private static final List<String> STATES = List.of("normal", "hover", "pressed", "selected", "disabled", "focus");
    private static Stage stage;
    private static Pane host;
    private static HBox row;
    private static Button focusSink;
    private static int assertions;
    private static int groups;

    public static void main(String[] args) throws Exception {
        Path themes = Path.of(args[0]);
        CountDownLatch done = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.startup(() -> {
            focusSink = new Button("Focus sink");
            row = new HBox(8);
            row.setAlignment(Pos.TOP_LEFT);
            row.setManaged(false);
            host = new Pane(focusSink, row);
            stage = new Stage();
            stage.setScene(new Scene(host, 1200, 300));
            stage.setTitle("Control sizing verification");
            stage.show();
            stage.requestFocus();
            Platform.runLater(() -> {
                try {
                    for (Mode mode : MODES) {
                        stage.getScene().getStylesheets().setAll(themes.resolve(mode.entry + ".css").toUri().toString());
                        verifyRadioButtonSizes(mode);
                        verifyEditableComboBoxPaint(mode);
                        verifyDisabledComboBoxPaint(mode);
                        for (String size : SIZES) {
                            verifyCheckBoxFont(mode, size);
                            verifyLegacySizingContract(mode, size);
                            for (int font : List.of(12, 16, 24)) {
                                verifyLine(mode, size, font);
                            }
                            verifyContent(mode, size);
                        }
                    }
                    System.out.printf("PASS groups=%d assertions=%d outputScale=%.2f%n",
                            groups, assertions, stage.getOutputScaleY());
                } catch (Throwable error) {
                    failure.set(error);
                } finally {
                    stage.close();
                    done.countDown();
                }
            });
        });
        done.await();
        Platform.exit();
        if (failure.get() != null) {
            throw new AssertionError("Control sizing regression", failure.get());
        }
    }

    private static List<Sample> samples() {
        List<Sample> samples = new ArrayList<>();
        samples.add(new Sample(new Button(TEXT), "btn-ui"));
        samples.add(new Sample(new Label(TEXT), "label-ui"));
        samples.add(new Sample(new TextField(TEXT), "text-field-ui"));
        PasswordField password = new PasswordField();
        password.setText(TEXT);
        samples.add(new Sample(password, "text-field-ui"));
        for (boolean editable : List.of(false, true)) {
            ComboBox<String> combo = new ComboBox<>();
            combo.getItems().addAll(TEXT, "Second");
            combo.getSelectionModel().selectFirst();
            combo.setEditable(editable);
            samples.add(new Sample(combo, "combo-box-ui"));
        }
        samples.add(new Sample(new ToggleButton(TEXT), "toggle-btn-ui"));
        samples.add(new Sample(new CheckBox(TEXT), "check-box-ui"));
        samples.add(new Sample(new RadioButton(TEXT), "radio-button-ui"));
        return samples;
    }

    private static void style(Sample sample, Mode mode, String size, int font) {
        Control control = sample.control;
        control.getStyleClass().add(sample.styleClass);
        if (mode.suffix.endsWith("-scope")) {
            control.pseudoClassStateChanged(PseudoClass.getPseudoClass("ui-" + mode.suffix.replace("-scope", "")), true);
        } else if (!mode.suffix.isEmpty()) {
            control.getStyleClass().add(sample.styleClass + "-" + mode.suffix);
        }
        if (!size.equals("default")) {
            control.getStyleClass().add(sample.styleClass + "-" + size);
        }
        control.setStyle("-fx-font-size: " + font + "px;");
    }

    private static void layout() {
        host.applyCss();
        row.resizeRelocate(10, 45, 1180, 220);
        row.layout();
    }

    private static void verifyLine(Mode mode, String size, int font) {
        List<Sample> samples = samples();
        row.getChildren().clear();
        for (Sample sample : samples) {
            style(sample, mode, size, font);
            sample.control.setPrefWidth(100);
            row.getChildren().add(sample.control);
        }
        focusSink.requestFocus();
        layout();
        double expected = samples.getFirst().control.getHeight();
        for (String state : STATES) {
            focusSink.requestFocus();
            for (Sample sample : samples) {
                Control control = sample.control;
                control.setDisable(state.equals("disabled"));
                for (String pseudo : List.of("hover", "pressed", "selected")) {
                    control.pseudoClassStateChanged(PseudoClass.getPseudoClass(pseudo), state.equals(pseudo));
                }
            }
            layout();
            for (Sample sample : samples) {
                Control control = sample.control;
                if (state.equals("focus") && !(control instanceof Label)) {
                    control.requestFocus();
                    layout();
                    check(control.isFocused(), "real focus " + sample.styleClass);
                }
                String key = mode + "/" + size + "/" + font + "/" + state + "/" + control.getClass().getSimpleName();
                if (control instanceof CheckBox || control instanceof RadioButton) {
                    check(control.getPadding().equals(Insets.EMPTY), key + " indicator size does not add outer padding");
                } else if (control instanceof ComboBox) {
                    if (font == 12) {
                        check(Math.abs(control.getHeight() - expected) <= 1.0 / stage.getOutputScaleY() + 0.01,
                                key + " default-font height within one device pixel: expected=" + expected + " actual=" + control.getHeight());
                    }
                    check(control.getPadding().equals(Insets.EMPTY), key + " ComboBox owner keeps zero padding");
                } else {
                    equal(control.getHeight(), expected, key + " equal height");
                }
                equal(control.getLayoutY(), 0, key + " aligned");
                equal(control.getMinHeight(), Region.USE_COMPUTED_SIZE, key + " no height floor");
                equal(control.getPrefHeight(), Region.USE_COMPUTED_SIZE, key + " content-driven preference");
                verifyText(control, control, key);
            }
            groups++;
        }
    }


    private static void verifyLegacySizingContract(Mode mode, String size) {
        boolean windows = mode.suffix.startsWith("windows") || mode.entry.equals("windows") && mode.suffix.isEmpty();
        double tierHeight = switch (size) {
            case "tiny" -> 21;
            case "small" -> 25;
            case "big" -> 37;
            default -> 31;
        };
        for (boolean editable : List.of(false, true)) {
            ComboBox<String> combo = new ComboBox<>();
            combo.getItems().addAll(TEXT, "Second");
            combo.getSelectionModel().selectFirst();
            combo.setEditable(editable);
            style(new Sample(combo, "combo-box-ui"), mode, size, 12);
            row.getChildren().setAll(combo);
            layout();
            String key = mode + "/" + size + "/editable=" + editable;
            check(combo.getPadding().equals(Insets.EMPTY), key + " zero outer padding");
            Region content = editable ? combo.getEditor() : (Region) combo.lookupAll(".list-cell").stream()
                    .filter(node -> node.getParent() == combo).findFirst().orElseThrow();
            equal(content.getPrefHeight(), tierHeight - (windows || editable ? 2 : 0),
                    key + " cell/editor owns preferred height");
            check(content.getPadding().getTop() > 0, key + " native inner padding is retained");
            content.setStyle("-fx-pref-height: 55px !important;");
            content.applyCss();
            combo.requestLayout();
            layout();
            equal(content.getPrefHeight(), 55, key + " business CSS controls cell/editor preferred height");
            combo.setMinHeight(0);
            combo.setPrefHeight(18);
            layout();
            equal(combo.getHeight(), 18, key + " caller can set outer height");
        }
        Button button = new Button(TEXT);
        style(new Sample(button, "btn-ui"), mode, size, 12);
        row.getChildren().setAll(button);
        layout();
        if (!windows) {
            equal(button.getInsets().getTop(), button.getPadding().getTop(),
                    mode + "/" + size + " no synthetic Base button border");
        }
        if (mode.suffix.isEmpty()) {
            double expected = button.getHeight();
            for (String variant : List.of("btn-ui-windows-primary", "btn-ui-windows-default")) {
                Button themed = new Button(TEXT);
                style(new Sample(themed, "btn-ui"), mode, size, 12);
                themed.getStyleClass().add(variant);
                row.getChildren().setAll(themed);
                layout();
                equal(themed.getHeight(), expected, mode + "/" + size + "/" + variant + " equal height");
            }
        }
        TextField field = new TextField(TEXT);
        style(new Sample(field, "text-field-ui"), mode, size, 12);
        row.getChildren().setAll(field);
        layout();
        equal(field.getPadding().getTop(), field.getPadding().getLeft(),
                mode + "/" + size + " TextField keeps uniform inner padding");
    }

    private static void verifyContent(Mode mode, String size) {
        for (boolean nested : List.of(false, true)) {
            Rectangle graphic = new Rectangle(54, 54, Color.CORNFLOWERBLUE);
            Button button = new Button(nested ? "" : TEXT);
            if (nested) {
                VBox content = new VBox(4, graphic, new Label(TEXT));
                content.setAlignment(Pos.CENTER);
                button.setGraphic(content);
            } else {
                button.setGraphic(graphic);
                button.setContentDisplay(ContentDisplay.TOP);
            }
            style(new Sample(button, "btn-ui"), mode, size, 16);
            row.getChildren().setAll(button);
            focusSink.requestFocus();
            layout();
            double natural = button.getHeight();
            check(natural > 54, "graphic and text expand " + mode + "/" + size);
            Bounds graphicBounds = graphic.localToScene(graphic.getBoundsInLocal());
            Bounds owner = button.localToScene(button.getLayoutBounds());
            check(graphicBounds.getMinY() >= owner.getMinY() && graphicBounds.getMaxY() <= owner.getMaxY(),
                    "graphic stays inside button");
            verifyText(button, button, "graphic button");
            button.requestFocus();
            layout();
            equal(button.getHeight(), natural, "focus retains natural height");
            button.setPrefHeight(natural + 30);
            layout();
            equal(button.prefHeight(-1), natural + 30, "caller preference retained");
        }
        Button multiline = new Button(TEXT + "\n" + TEXT);
        style(new Sample(multiline, "btn-ui"), mode, size, 16);
        Button single = new Button(TEXT);
        style(new Sample(single, "btn-ui"), mode, size, 16);
        row.getChildren().setAll(single, multiline);
        layout();
        check(multiline.getHeight() > single.getHeight(), "multiline expands");
        verifyText(multiline, multiline, "multiline button");
        single.setMinHeight(0);
        single.setPrefHeight(18);
        layout();
        equal(single.prefHeight(-1), 18, "small caller preference retained");
        equal(single.getHeight(), 18, "no CSS minimum overrides caller");
    }

    private static void verifyCheckBoxFont(Mode mode, String size) {
        for (String semantic : List.of("", "main", "normal", "success", "warn", "error", "circle")) {
            CheckBox checkBox = new CheckBox(TEXT);
            style(new Sample(checkBox, "check-box-ui"), mode, size, 12);
            if (!semantic.isEmpty()) {
                checkBox.getStyleClass().add("check-box-ui-" + semantic);
            }
            checkBox.setSelected(true);
            row.getChildren().setAll(checkBox);
            layout();
            Region box = (Region) checkBox.lookup(".box");
            Region mark = (Region) checkBox.lookup(".mark");
            for (int fontSize : List.of(12, 24, 48, 16, 12)) {
                checkBox.setStyle("-fx-font-size: " + fontSize + "px;");
                layout();
                String key = mode + "/" + size + "/" + fontSize + "/" + semantic;
                equal(checkBox.getFont().getSize(), fontSize, key + " font retained");
                equal(box.getPrefWidth(), Region.USE_COMPUTED_SIZE, key + " natural indicator width");
                equal(box.getPrefHeight(), Region.USE_COMPUTED_SIZE, key + " natural indicator height");
                if (size.equals("default")) {
                    check(box.getWidth() >= fontSize * 1.3, key + " default indicator not compressed");
                    check(box.getHeight() >= fontSize * 1.3, key + " default indicator not compressed vertically");
                }
                Bounds glyph = mark.getBoundsInParent();
                double ratio = switch (size) {
                    case "tiny" -> 0.55;
                    case "small" -> 0.75;
                    case "big" -> 1.3;
                    default -> 11.0 / 12;
                };
                if (size.equals("default")) {
                    check(glyph.getWidth() > fontSize * 0.75 && glyph.getWidth() < fontSize * 1.1, key + " native check width");
                    check(glyph.getHeight() > fontSize * 0.75 && glyph.getHeight() < fontSize * 1.1, key + " native check height");
                } else {
                    check(Math.abs(glyph.getWidth() - fontSize * ratio) <= 2, key + " sized check width");
                    check(Math.abs(glyph.getHeight() - fontSize * ratio) <= 2, key + " sized check height");
                }
                if (!size.equals("default")) {
                    double width = box.getWidth(), height = box.getHeight();
                    checkBox.setIndeterminate(true);
                    layout();
                    equal(box.getWidth(), width, key + " indeterminate retains indicator width");
                    equal(box.getHeight(), height, key + " indeterminate retains indicator height");
                    check(mark.isScaleShape(), key + " indeterminate bar scales with size");
                    checkBox.setIndeterminate(false);
                    layout();
                }
                check(glyph.getMinX() >= 0 && glyph.getMinY() >= 0
                      && glyph.getMaxX() <= box.getWidth() && glyph.getMaxY() <= box.getHeight(),
                        key + " check stays inside indicator");
                checkBox.fire();
                check(!checkBox.isSelected(), key + " deselect");
                checkBox.fire();
                check(checkBox.isSelected(), key + " select");
                groups++;
            }
        }
    }


    private static void verifyDisabledComboBoxPaint(Mode mode) {
        if (!(mode.suffix.startsWith("windows") || mode.entry.equals("windows") && mode.suffix.isEmpty())) return;
        for (String size : SIZES) {
            for (String semantic : List.of("", "normal", "success", "warn", "error")) {
                for (boolean editable : List.of(false, true)) {
                    ComboBox<String> combo = new ComboBox<>();
                    combo.setEditable(editable);
                    combo.getItems().addAll(TEXT, "Second");
                    combo.getSelectionModel().selectFirst();
                    style(new Sample(combo, "combo-box-ui"), mode, size, 16);
                    if (!semantic.isEmpty()) combo.getStyleClass().add("combo-box-ui-" + semantic);
                    combo.setDisable(true);
                    row.getChildren().setAll(combo);
                    layout();
                    Region arrow = (Region) combo.lookup(".arrow-button");
                    double width = combo.getWidth(), height = combo.getHeight();
                    for (boolean hovered : List.of(false, true)) {
                        combo.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), hovered);
                        layout();
                        String key = mode + "/" + size + "/" + semantic + "/editable=" + editable + "/hover=" + hovered;
                        check(arrow.getBackground().getFills().stream()
                                        .allMatch(fill -> fill.getFill().equals(Color.TRANSPARENT)),
                                key + " disabled arrow uses owner background");
                        equal(combo.getWidth(), width, key + " disabled width stable");
                        equal(combo.getHeight(), height, key + " disabled height stable");
                        groups++;
                    }
                }
            }
        }
    }

    private static void verifyEditableComboBoxPaint(Mode mode) {
        if (!(mode.suffix.startsWith("windows") || mode.entry.equals("windows") && mode.suffix.isEmpty())) return;
        for (String size : SIZES) {
            for (String semantic : List.of("", "normal", "success", "warn", "error")) {
                ComboBox<String> combo = new ComboBox<>();
                combo.setEditable(true);
                combo.getItems().addAll(TEXT, "Second");
                combo.getSelectionModel().selectFirst();
                style(new Sample(combo, "combo-box-ui"), mode, size, 16);
                if (!semantic.isEmpty()) combo.getStyleClass().add("combo-box-ui-" + semantic);
                row.getChildren().setAll(combo);
                layout();
                Region arrow = (Region) combo.lookup(".arrow-button");
                double height = combo.getHeight();
                for (String state : List.of("normal", "hover", "focus", "focus-hover", "disabled", "disabled-hover")) {
                    focusSink.requestFocus();
                    combo.setDisable(state.startsWith("disabled"));
                    combo.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), state.contains("hover"));
                    if (state.startsWith("focus")) combo.getEditor().requestFocus();
                    layout();
                    String key = mode + "/" + size + "/" + semantic + "/" + state;
                    check(arrow.getBackground().getFills().stream()
                            .allMatch(fill -> fill.getFill().equals(Color.TRANSPARENT)), key + " unified arrow background");
                    check(combo.getEditor().getBackground().getFills().stream()
                            .allMatch(fill -> fill.getFill().equals(Color.TRANSPARENT)), key + " unified editor background");
                    Color expected = Color.web(state.startsWith("disabled") ? "#F3F3F3"
                            : state.contains("hover") ? "#EDF5FD" : "#FFFFFF");
                    check(combo.getBackground().getFills().getLast().getFill().equals(expected),
                            key + " owner paints the entire surface");
                    equal(combo.getHeight(), height, key + " background states retain height");
                    groups++;
                }
            }
        }
    }

    private static void verifyRadioButtonSizes(Mode mode) {
        for (int font : List.of(12, 16, 24, 48)) {
            for (String semantic : List.of("", "main", "normal", "success", "warn", "error")) {
                double previousRing = 0, previousDot = 0;
                for (String size : SIZES) {
                    CheckBox checkbox = new CheckBox(TEXT);
                    style(new Sample(checkbox, "check-box-ui"), mode, size, font);
                    if (!semantic.isEmpty()) checkbox.getStyleClass().add("check-box-ui-" + semantic);
                    RadioButton radio = new RadioButton(TEXT);
                    style(new Sample(radio, "radio-button-ui"), mode, size, font);
                    if (!semantic.isEmpty()) radio.getStyleClass().add("radio-button-ui-" + semantic);
                    radio.setSelected(true);
                    row.getChildren().setAll(checkbox, radio);
                    layout();
                    Region box = (Region) checkbox.lookup(".box");
                    Region ring = (Region) radio.lookup(".radio");
                    Region dot = (Region) radio.lookup(".dot");
                    String key = mode + "/" + size + "/" + font + "/" + semantic;
                    equal(radio.getFont().getSize(), font, key + " radio label font retained");
                    check(ring.getWidth() > previousRing, key + " radio ring grows");
                    check(dot.getWidth() > previousDot, key + " radio dot grows");
                    previousRing = ring.getWidth();
                    previousDot = dot.getWidth();
                    double height = radio.getHeight();
                    for (String state : STATES) {
                        checkbox.setSelected(!state.equals("normal"));
                        checkbox.setDisable(state.equals("disabled"));
                        radio.setSelected(!state.equals("normal"));
                        radio.setDisable(state.equals("disabled"));
                        for (String pseudo : List.of("hover", "pressed", "focused")) {
                            checkbox.pseudoClassStateChanged(PseudoClass.getPseudoClass(pseudo),
                                    state.equals(pseudo) || state.equals("focus") && pseudo.equals("focused"));
                            radio.pseudoClassStateChanged(PseudoClass.getPseudoClass(pseudo),
                                    state.equals(pseudo) || state.equals("focus") && pseudo.equals("focused"));
                        }
                        layout();
                        equal(ring.getWidth(), box.getWidth(), key + " indicator widths aligned");
                        equal(ring.getHeight(), box.getHeight(), key + " indicator heights aligned");
                        equal(radio.getHeight(), checkbox.getHeight(), key + " control heights aligned");
                        equal(checkbox.getFont().getSize(), font, key + " checkbox label font retained");
                        equal(ring.getWidth(), previousRing, key + " ring width stable");
                        equal(ring.getHeight(), previousRing, key + " circular ring");
                        equal(dot.getWidth(), previousDot, key + " dot width stable");
                        equal(dot.getHeight(), previousDot, key + " circular dot");
                        equal(radio.getHeight(), height, key + " state retains layout");
                        Bounds bounds = dot.getBoundsInParent();
                        equal(bounds.getCenterX(), ring.getWidth() / 2, key + " dot centered horizontally");
                        equal(bounds.getCenterY(), ring.getHeight() / 2, key + " dot centered vertically");
                        check(bounds.getMinX() >= 0 && bounds.getMinY() >= 0
                              && bounds.getMaxX() <= ring.getWidth() && bounds.getMaxY() <= ring.getHeight(),
                                key + " dot stays inside ring");
                        groups++;
                    }
                }
            }
        }
    }

    private static void verifyText(Control owner, Node node, String key) {
        if (!node.isVisible() || node.getOpacity() == 0) {
            return;
        }
        if (node instanceof Text text && !text.getText().isEmpty()) {
            Bounds glyph = text.localToScene(text.getBoundsInLocal());
            Bounds bounds = owner.localToScene(owner.getLayoutBounds());
            check(glyph.getMinY() >= bounds.getMinY() - 0.75 && glyph.getMaxY() <= bounds.getMaxY() + 0.75,
                    key + " text inside control");
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                verifyText(owner, child, key);
            }
        }
    }

    private static void equal(double actual, double expected, String message) {
        check(Math.abs(actual - expected) < 0.01, message + " expected=" + expected + " actual=" + actual);
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
