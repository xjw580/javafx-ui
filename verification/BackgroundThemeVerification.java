import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.WideComboBox;
import club.xiaojiawei.enums.Theme;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/** Run with the built library and JavaFX on the classpath in a desktop session. */
public final class BackgroundThemeVerification {
    private static final PseudoClass HOVER = PseudoClass.getPseudoClass("hover");
    private static int assertions;

    public static void main(String[] args) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        Platform.startup(() -> {
            Stage stage = new Stage();
            try {
                Label selectedText = new Label("Selected");
                StackPane selected = new StackPane(selectedText);
                selected.getStyleClass().add("bg-ui");
                StackPane hover = new StackPane(new Label("Hover"));
                hover.getStyleClass().add("bg-hover-ui");
                WideComboBox<String> combo = new WideComboBox<>();
                combo.getItems().addAll("First", "Second", "Third");
                combo.getSelectionModel().selectFirst();
                VBox root = new VBox(10, selected, hover, combo);
                Scene scene = new Scene(root, 600, 400);
                stage.setScene(scene);
                stage.show();
                for (Theme theme : List.of(Theme.BASE, Theme.WINDOWS, Theme.BASE)) {
                    JavaFXUI.setTheme(scene, theme);
                    hover.pseudoClassStateChanged(HOVER, true);
                    root.applyCss();
                    root.layout();
                    background(selected, selection(theme));
                    background(hover, hover(theme));
                    color(selectedText.getTextFill(), text(theme));
                    for (String forced : List.of("", "base", "windows")) {
                        combo.getStyleClass().removeAll("wide-combo-box-ui-base", "wide-combo-box-ui-windows");
                        if (!forced.isEmpty()) combo.getStyleClass().add("wide-combo-box-ui-" + forced);
                        Theme effective = forced.isEmpty() ? theme : forced.equals("base") ? Theme.BASE : Theme.WINDOWS;
                        root.applyCss();
                        root.layout();
                        combo.show();
                        PopupWindow popup = (PopupWindow) Window.getWindows().stream()
                                .filter(w -> w instanceof PopupWindow && w.isShowing()).findFirst().orElseThrow();
                        Parent popupRoot = popup.getScene().getRoot();
                        popupRoot.applyCss();
                        popupRoot.layout();
                        Region grid = (Region) popupRoot.lookup(".grid-pane");
                        Region selectedItem = (Region) grid.getChildrenUnmodifiable().stream()
                                .filter(n -> "First".equals(n.getUserData())).findFirst().orElseThrow();
                        Region hoveredItem = (Region) grid.getChildrenUnmodifiable().stream()
                                .filter(n -> "Second".equals(n.getUserData())).findFirst().orElseThrow();
                        hoveredItem.pseudoClassStateChanged(HOVER, true);
                        popupRoot.applyCss();
                        background(selectedItem, selection(effective));
                        background(hoveredItem, hover(effective));
                        color(((Label) selectedItem.lookup(".label")).getTextFill(), text(effective));
                        double width = selectedItem.getWidth(), height = selectedItem.getHeight();
                        selectedItem.pseudoClassStateChanged(HOVER, true);
                        popupRoot.applyCss();
                        popupRoot.layout();
                        background(selectedItem, selection(effective));
                        check(selectedItem.getWidth() == width && selectedItem.getHeight() == height, "hover changes item size");
                        if (effective == Theme.WINDOWS) {
                            check(grid.getEffect() instanceof DropShadow shadow && shadow.getRadius() == 12,
                                    "Windows popup shadow not applied");
                        }
                        combo.getSelectionModel().select(1);
                        popupRoot.applyCss();
                        background(hoveredItem, selection(effective));
                        color(((Label) hoveredItem.lookup(".label")).getTextFill(), text(effective));
                        combo.hide();
                        combo.getSelectionModel().selectFirst();
                    }
                }
                System.out.println("PASS background utility classes and WideComboBox popup themes: assertions=" + assertions);
            } catch (Throwable failure) {
                error.set(failure);
            } finally {
                stage.close();
                done.countDown();
            }
        });
        done.await();
        Platform.exit();
        if (error.get() != null) throw new AssertionError("Background theme regression", error.get());
    }

    private static String selection(Theme theme) {
        return theme == Theme.WINDOWS ? "#E0EFFF" : "#0075FF";
    }

    private static String hover(Theme theme) {
        return theme == Theme.WINDOWS ? "#EDF5FD" : "#b3d5ff";
    }

    private static String text(Theme theme) {
        return theme == Theme.WINDOWS ? "#202020" : "#ffffff";
    }

    private static void background(Region node, String expected) {
        check(node.getBackground() != null && !node.getBackground().getFills().isEmpty(), "missing background");
        color(node.getBackground().getFills().getLast().getFill(), expected);
    }

    private static void color(Object actual, String expected) {
        check(Color.web(expected).equals(actual), "expected " + expected + ", actual " + actual);
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }
}
