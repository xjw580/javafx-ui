import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.component.FileUploaderItem;
import club.xiaojiawei.component.TableValueFilter;
import club.xiaojiawei.controls.Calendar;
import club.xiaojiawei.controls.Carousel;
import club.xiaojiawei.controls.Date;
import club.xiaojiawei.controls.DateSelector;
import club.xiaojiawei.controls.DateTime;
import club.xiaojiawei.controls.DurationSelector;
import club.xiaojiawei.controls.DurationTime;
import club.xiaojiawei.controls.FileUploader;
import club.xiaojiawei.controls.FilterComboBox;
import club.xiaojiawei.controls.FilterField;
import club.xiaojiawei.controls.LabelSeparator;
import club.xiaojiawei.controls.MessageBox;
import club.xiaojiawei.controls.Modal;
import club.xiaojiawei.controls.MultiFileChooser;
import club.xiaojiawei.controls.Notification;
import club.xiaojiawei.controls.NumberField;
import club.xiaojiawei.controls.NumberSelector;
import club.xiaojiawei.controls.PasswordTextField;
import club.xiaojiawei.controls.ProgressModal;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.controls.TableValueFilterManager;
import club.xiaojiawei.controls.TableFilterManagerGroup;
import club.xiaojiawei.controls.Time;
import club.xiaojiawei.controls.TimeSelector;
import club.xiaojiawei.controls.TipField;
import club.xiaojiawei.controls.VisibleTreeView;
import club.xiaojiawei.controls.WideComboBox;
import club.xiaojiawei.controls.WindowBar;
import club.xiaojiawei.enums.Theme;
import club.xiaojiawei.skin.IconTextFieldSkin;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/** Run with the built library and its dependencies on the classpath, on a desktop. */
public final class CustomThemeVerification {
    private record Sample(String styleClass, Supplier<Parent> create, List<String> selectors) {
    }

    private static Stage stage;
    private static int assertions;

    public static void main(String[] args) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            stage = new Stage();
            Platform.runLater(() -> {
                try {
                    verifyApi();
                    verifyModalDemo();
                    verifyCheckBoxDemo();
                    verifyRadioButtonDemo();
                    for (Sample sample : samples()) {
                        for (Theme theme : Theme.values()) {
                            Map<String, Object> normal = appearance(sample, theme, "");
                            Theme opposite = theme == Theme.BASE ? Theme.WINDOWS : Theme.BASE;
                            String suffix = theme == Theme.BASE ? "-base" : "-windows";
                            Map<String, Object> forced = appearance(sample, opposite, suffix);
                            check(normal.equals(forced), sample.styleClass + " forced " + theme + "\nnormal=" + normal + "\nforced=" + forced);
                        }
                    }
                    verifyInputStyle();
                    verifyNestedThemes();
                    verifyWindows();
                    System.out.println("PASS customControls=" + samples().size() + " assertions=" + assertions);
                } catch (Throwable error) {
                    failure.set(error);
                } finally {
                    for (Window window : List.copyOf(Window.getWindows())) window.hide();
                    done.countDown();
                }
            });
        });
        done.await();
        Platform.exit();
        if (failure.get() != null) throw new AssertionError("Custom theme regression", failure.get());
    }

    private static List<Sample> samples() {
        return List.of(
                new Sample("switch-ui", Switch::new, List.of("#bgRectangle", "#switchRectangle", "#switchCircle")),
                new Sample("calendar-ui", Calendar::new, List.of("#calendar", ".bottomLabel")),
                new Sample("date-ui", Date::new, List.of("#dateTimeBg")),
                new Sample("time-ui", Time::new, List.of("#dateTimeBg")),
                new Sample("date-time-ui", DateTime::new, List.of("#dateTimeBg")),
                new Sample("duration-time-ui", () -> new DurationTime(23, 59, 59, 0, 0, 0), List.of("#dateTimeBg")),
                new Sample("date-selector-ui", DateSelector::new, List.of("#root", ".dateSelectorScrollPane")),
                new Sample("time-selector-ui", TimeSelector::new, List.of("#root", ".timeLabel")),
                new Sample("duration-selector-ui", () -> new DurationSelector(23, 59, 59, 0, 0, 0), List.of("#root", ".timeLabel")),
                new Sample("number-selector-ui", NumberSelector::new, List.of("#root", ".numberLabel")),
                new Sample("file-uploader-ui", FileUploader::new, List.of("#FileUploader")),
                new Sample("file-uploader-item-ui", FileUploaderItem::new, List.of("#fileUploaderItem")),
                new Sample("carousel-ui", Carousel::new, List.of("#left", "#right")),
                new Sample("message-box-ui", MessageBox::new, List.of("#msg", "#triangle")),
                new Sample("notification-ui", Notification::new, List.of("#notificationVBox")),
                new Sample("window-bar-ui", WindowBar::new, List.of("#topPane")),
                new Sample("progress-modal-ui", ProgressModal::new, List.of("#progressLabel", "#title")),
                new Sample("label-separator-ui", LabelSeparator::new, List.of(".label")),
                new Sample("table-filter-ui", TableValueFilterManager::new, List.of(".filterBtn")),
                new Sample("table-filter-panel-ui", () -> new TableValueFilter<>(new TableColumn<>("Value"), new TableFilterManagerGroup<>()), List.of("", ".window-bar-ui")),
                new Sample("filter-field-ui", FilterField::new, List.of("")),
                new Sample("number-field-ui", NumberField::new, List.of("")),
                new Sample("password-text-field-ui", PasswordTextField::new, List.of("")),
                new Sample("tip-field-ui", TipField::new, List.of("")),
                new Sample("filter-combo-box-ui", FilterComboBox::new, List.of("")),
                new Sample("wide-combo-box-ui", WideComboBox::new, List.of("")),
                new Sample("visible-tree-view-ui", VisibleTreeView::new, List.of("")));
    }

    private static Map<String, Object> appearance(Sample sample, Theme theme, String suffix) {
        Parent control = sample.create.get();
        if (!suffix.isEmpty()) control.getStyleClass().add(sample.styleClass + suffix);
        StackPane root = new StackPane(control);
        Scene scene = new Scene(root, 600, 450);
        JavaFXUI.setTheme(scene, theme);
        stage.setScene(scene);
        stage.show();
        root.applyCss();
        root.layout();
        Map<String, Object> result = new LinkedHashMap<>();
        for (String state : List.of("normal", "hover", "focused", "disabled")) {
            for (String pseudo : List.of("hover", "focused", "disabled")) {
                control.pseudoClassStateChanged(PseudoClass.getPseudoClass(pseudo), state.equals(pseudo));
            }
            root.applyCss();
            root.layout();
            for (String selector : sample.selectors) {
                Node node = selector.isEmpty() ? control : control.lookup(selector);
                check(node != null, sample.styleClass + " missing " + selector);
                String key = state + "/" + selector;
                if (node instanceof Region region) {
                    result.put(key + "/background", region.getBackground());
                    result.put(key + "/border", region.getBorder());
                    result.put(key + "/padding", region.getPadding());
                }
                if (node instanceof Labeled labeled) result.put(key + "/text", labeled.getTextFill());
                if (node instanceof Shape shape) result.put(key + "/fill", shape.getFill());
            }
        }
        if (!suffix.isEmpty()) {
            control.getStyleClass().remove(sample.styleClass + suffix);
            check(JavaFXUI.getTheme(control) == theme, "suffix removal returns to scene " + sample.styleClass);
            check(control.getStylesheets().stream().noneMatch(s -> s.equals(JavaFXUI.themeStylesheet(Theme.BASE))
                                                                   || s.equals(JavaFXUI.themeStylesheet(Theme.WINDOWS))), "no retained forced stylesheet");
        }
        return result;
    }

    private static void verifyApi() {
        JavaFXUI.setDefaultTheme(Theme.WINDOWS);
        Scene scene = new Scene(new StackPane());
        scene.getStylesheets().add("file:/business.css");
        JavaFXUI.setTheme(scene);
        check(JavaFXUI.getTheme(scene) == Theme.WINDOWS, "configured default");
        JavaFXUI.setTheme(scene, Theme.BASE);
        JavaFXUI.setTheme(scene, Theme.BASE);
        check(scene.getStylesheets().size() == 2, "idempotent switch preserves business stylesheet");
        check(scene.getStylesheets().getFirst().equals("file:/business.css"), "business stylesheet order");
        scene.getStylesheets().removeFirst();
        try {
            JavaFXUI.setDefaultTheme(null);
            throw new AssertionError("null theme accepted");
        } catch (NullPointerException expected) {
            check(JavaFXUI.getDefaultTheme() == Theme.WINDOWS, "null does not mutate default");
        }
        Switch control = new Switch();
        Scene automatic = new Scene(new StackPane(control));
        check(JavaFXUI.getTheme(automatic) == Theme.WINDOWS, "custom control loads selected default");
        JavaFXUI.setDefaultTheme(Theme.BASE);
    }

    private static void verifyInputStyle() {
        for (Theme theme : Theme.values()) {
            for (TextField input : List.of(new FilterField(), new NumberField(), new PasswordTextField(), new TipField())) {
                String style = "-fx-font-size: 24px; -fx-padding: 8 12;";
                input.setStyle(style);
                input.setText("12");
                StackPane root = new StackPane(input);
                Scene scene = new Scene(root, 350, 120);
                JavaFXUI.setTheme(scene, theme);
                stage.setScene(scene);
                root.applyCss();
                root.layout();
                IconTextFieldSkin skin = (IconTextFieldSkin) input.getSkin();
                skin.setHideIcon(true);
                root.layout();
                skin.setHideIcon(false);
                root.layout();
                check(input.getStyle().equals(style), "icon layout preserves caller CSS");
                check(input.getFont().getSize() == 24, "icon layout preserves font");
                check(input.getPadding().getRight() == 12, "icon layout preserves padding");
                check(input.lookup("#icon-pane").getBoundsInParent().getMaxX() <= input.getWidth(), "icon inside input");
            }
        }
    }

    private static void verifyNestedThemes() {
        StackPane composite = new StackPane();
        JavaFXUI.initializeTheme(composite, "test-ui");
        StackPane root = new StackPane(composite);
        Scene scene = new Scene(root, 350, 200);
        JavaFXUI.setTheme(scene, Theme.BASE);
        stage.setScene(scene);
        composite.getStyleClass().add("test-ui-windows");
        FilterField input = new FilterField();
        composite.getChildren().add(input);
        root.applyCss();
        check(input.getPseudoClassStates().contains(PseudoClass.getPseudoClass("ui-windows")), "late child inherits scope");
        List<String> original = List.copyOf(input.getStyleClass());
        input.getStyleClass().add("filter-field-ui-base");
        root.applyCss();
        check(JavaFXUI.getTheme(input) == Theme.BASE, "nested explicit theme wins");
        check(input.getPseudoClassStates().contains(PseudoClass.getPseudoClass("ui-base")), "nested scope owns pseudo-class");
        input.getStyleClass().remove("filter-field-ui-base");
        root.applyCss();
        check(JavaFXUI.getTheme(input) == Theme.WINDOWS, "nested removal restores ancestor");
        check(input.getPseudoClassStates().contains(PseudoClass.getPseudoClass("ui-windows")), "ancestor scope restored: " + input.getPseudoClassStates() + " classes=" + input.getStyleClass());
        composite.getChildren().remove(input);
        check(!input.getPseudoClassStates().contains(PseudoClass.getPseudoClass("ui-windows")), "removed child releases scope");
        composite.getChildren().add(input);
        composite.getStyleClass().remove("test-ui-windows");
        check(!input.getPseudoClassStates().contains(PseudoClass.getPseudoClass("ui-windows")), "scope removal clears child");
        check(input.getStyleClass().equals(original), "theme propagation preserves caller classes");
        composite.getStyleClass().add("test-ui-windows");
        composite.getStyleClass().setAll("test-ui", "test-ui-base");
        check(input.getPseudoClassStates().contains(PseudoClass.getPseudoClass("ui-base")), "atomic suffix replacement updates scope");
    }

    private static void verifyCheckBoxDemo() throws Exception {
        Parent demo = FXMLLoader.load(JavaFXUI.class.getResource("/club/xiaojiawei/demo/tab/style/CheckBoxTab.fxml"));
        Scene scene = new Scene(demo, 1300, 1000);
        stage.setScene(scene);
        stage.show();
        for (Theme theme : Theme.values()) {
            JavaFXUI.setTheme(scene, theme);
            demo.applyCss();
            demo.layout();
            Node section = demo.lookup("#sizePane");
            check(section != null, "CheckBox size section exists");
            for (Node current = section; current != null; current = current.getParent()) {
                check(current.isVisible(), "CheckBox size section and ancestors are visible");
            }
            check(section.isManaged(), "CheckBox size section participates in layout");
            Parent samples = (Parent) demo.lookup("#sizeShow");
            check(samples.isVisible() && samples.isManaged(), "CheckBox size examples are displayed");
            check(samples.getChildrenUnmodifiable().size() == 4, "CheckBox shows four size tiers");
            double previousHeight = 0;
            double previousBoxWidth = 0, previousMarkWidth = 0;
            double fontSize = ((CheckBox) samples.getChildrenUnmodifiable().getFirst()).getFont().getSize();
            String[] classes = {"check-box-ui-tiny", "check-box-ui-small", "check-box-ui", "check-box-ui-big"};
            for (int i = 0; i < classes.length; i++) {
                CheckBox box = (CheckBox) samples.getChildrenUnmodifiable().get(i);
                check(box.getStyleClass().contains(classes[i]), "CheckBox size class matches displayed tier");
                check(box.getHeight() > previousHeight, "CheckBox size tiers increase in height");
                previousHeight = box.getHeight();
                Region indicator = (Region) box.lookup(".box");
                Region mark = (Region) box.lookup(".mark");
                check(indicator.getWidth() > previousBoxWidth, "CheckBox indicator grows with size tier");
                check(mark.getWidth() > previousMarkWidth, "CheckBox check mark grows with size tier");
                check(box.getFont().getSize() == fontSize, "CheckBox size tier preserves label font");
                previousBoxWidth = indicator.getWidth();
                previousMarkWidth = mark.getWidth();
            }
            check(demo.lookupAll(".check-box-ui-windows").isEmpty(), "CheckBox page follows global theme");
        }
    }

    private static void verifyRadioButtonDemo() throws Exception {
        Parent demo = FXMLLoader.load(JavaFXUI.class.getResource("/club/xiaojiawei/demo/tab/style/RadioButtonTab.fxml"));
        Scene scene = new Scene(demo, 1300, 1000);
        stage.setScene(scene);
        stage.show();
        for (Theme theme : Theme.values()) {
            JavaFXUI.setTheme(scene, theme);
            demo.applyCss();
            demo.layout();
            Node section = demo.lookup("#sizePane");
            check(section != null, "RadioButton size section exists");
            for (Node current = section; current != null; current = current.getParent()) {
                check(current.isVisible(), "RadioButton size section and ancestors are visible");
            }
            check(section.isManaged(), "RadioButton size section participates in layout");
            Parent samples = (Parent) demo.lookup("#sizeShow");
            check(samples.isVisible() && samples.isManaged(), "RadioButton size examples are displayed");
            check(samples.getChildrenUnmodifiable().size() == 4, "RadioButton shows four size tiers");
            double previousHeight = 0;
            double previousBoxWidth = 0, previousMarkWidth = 0;
            double fontSize = ((RadioButton) samples.getChildrenUnmodifiable().getFirst()).getFont().getSize();
            String[] classes = {"radio-button-ui-tiny", "radio-button-ui-small", "radio-button-ui", "radio-button-ui-big"};
            for (int i = 0; i < classes.length; i++) {
                RadioButton box = (RadioButton) samples.getChildrenUnmodifiable().get(i);
                check(box.getStyleClass().contains(classes[i]), "RadioButton size class matches displayed tier");
                check(box.getHeight() > previousHeight, "RadioButton size tiers increase in height");
                previousHeight = box.getHeight();
                Region indicator = (Region) box.lookup(".radio");
                Region mark = (Region) box.lookup(".dot");
                check(indicator.getWidth() > previousBoxWidth, "RadioButton indicator grows with size tier");
                check(mark.getWidth() > previousMarkWidth, "RadioButton check mark grows with size tier");
                check(box.getFont().getSize() == fontSize, "RadioButton size tier preserves label font");
                previousBoxWidth = indicator.getWidth();
                previousMarkWidth = mark.getWidth();
            }
            check(demo.lookupAll(".radio-button-ui-windows").isEmpty(), "RadioButton page follows global theme");
        }
    }

    private static void verifyModalDemo() throws Exception {
        Parent demo = FXMLLoader.load(JavaFXUI.class.getResource("/club/xiaojiawei/demo/tab/controls/ModalTab.fxml"));
        Scene scene = new Scene(demo, 600, 400);
        stage.setScene(scene);
        stage.show();
        for (Theme theme : List.of(Theme.WINDOWS, Theme.BASE)) {
            JavaFXUI.setDefaultTheme(theme == Theme.BASE ? Theme.WINDOWS : Theme.BASE);
            JavaFXUI.setTheme(scene, theme);
            demo.applyCss();
            demo.layout();
            ((Button) demo.lookup(".btn-ui")).fire();
            Stage dialog = (Stage) Window.getWindows().stream()
                    .filter(window -> window instanceof Stage && window != stage).findFirst().orElseThrow();
            check(JavaFXUI.getTheme(dialog.getScene()) == theme, "Modal Demo follows selected scene theme, not global default");
            check(dialog.getOwner() == stage, "Modal Demo retains its actual owner");
            Parent root = dialog.getScene().getRoot();
            root.applyCss();
            root.layout();
            Region surface = (Region) root.lookup(".modal-surface");
            check(surface.getBackground().getFills().getFirst().getRadii().getTopLeftHorizontalRadius()
                  == (theme == Theme.WINDOWS ? 8 : 0), "Modal surface uses selected theme corners");
            Color textColor = theme == Theme.WINDOWS ? Color.web("#202020") : Color.BLACK;
            check(((Labeled) root.lookup(".modal-heading")).getTextFill().equals(textColor), "Modal heading uses selected theme text color");
            check(root.lookupAll(".label").stream().filter(Labeled.class::isInstance)
                    .map(Labeled.class::cast).allMatch(label -> label.getTextFill().equals(textColor)), "Modal labels use selected theme text color");
            dialog.hide();
        }
        JavaFXUI.setDefaultTheme(Theme.BASE);
    }

    private static void verifyWindows() throws Exception {
        StackPane owner = new StackPane(new Button("Owner"));
        Scene scene = new Scene(owner, 600, 400);
        JavaFXUI.setTheme(scene, Theme.WINDOWS);
        stage.setScene(scene);
        stage.show();
        Modal modal = new Modal(owner, new Button("Content"));
        modal.show();
        check(JavaFXUI.getTheme(modal.getWindow().getScene()) == Theme.WINDOWS, "Modal inherits Windows");
        modal.getWindow().hide();
        JavaFXUI.setTheme(scene, Theme.BASE);
        modal.show();
        check(JavaFXUI.getTheme(modal.getWindow().getScene()) == Theme.BASE, "reused Modal follows owner");
        modal.getWindow().hide();
        JavaFXUI.setDefaultTheme(Theme.WINDOWS);
        Modal independent = new Modal(null, new Button("Content"));
        independent.show();
        check(JavaFXUI.getTheme(independent.getWindow().getScene()) == Theme.WINDOWS, "ownerless Modal uses default");
        independent.getWindow().hide();
        JavaFXUI.setDefaultTheme(Theme.BASE);

        Date date = new Date();
        owner.getChildren().setAll(date);
        JavaFXUI.setTheme(scene, Theme.WINDOWS);
        owner.applyCss();
        owner.layout();
        date.showPopup(stage, 50, 50);
        Popup popup = (Popup) Window.getWindows().stream().filter(w -> w instanceof Popup).findFirst().orElseThrow();
        check(JavaFXUI.getTheme(popup.getScene()) == Theme.WINDOWS, "date popup inherits Windows");
        popup.hide();
        date.getStyleClass().add("date-ui-base");
        date.showPopup(stage, 50, 50);
        check(JavaFXUI.getTheme(popup.getScene()) == Theme.BASE, "date popup inherits forced Base");
        popup.hide();

        Path directory = Files.createDirectories(Path.of(".omo/evidence/custom-theme/chooser-fixture"));
        MultiFileChooser chooser = new MultiFileChooser();
        chooser.setInitialDirectory(directory.toFile());
        chooser.showMultiFileDialog(stage, files -> {
        });
        Window chooserWindow = Window.getWindows().stream().filter(w -> w instanceof Stage && w != stage).findFirst().orElseThrow();
        check(JavaFXUI.getTheme(chooserWindow.getScene()) == Theme.WINDOWS, "file chooser inherits Windows");
        chooser.hideDialog();
        JavaFXUI.setTheme(scene, Theme.BASE);
        chooser.showMultiFileDialog(stage, files -> {
        });
        check(JavaFXUI.getTheme(chooserWindow.getScene()) == Theme.BASE, "reused file chooser follows owner");
        chooser.hideDialog();
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }
}
