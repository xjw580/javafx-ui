import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/** Checks real TabPane headers at natural size and under genuine overflow. */
public final class TabPaneThemeVerification {
    private static int assertions;

    public static void main(String[] args) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.startup(() -> {
            Stage stage = new Stage();
            Pane root = new Pane();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.show();
            try {
                for (String entry : List.of("windows", "base")) {
                    scene.getStylesheets().setAll(Path.of(args[0], entry + ".css").toUri().toString());
                    for (String variant : List.of("", "windows", "pseudo", "base", "base-pseudo")) {
                        boolean windows = !(variant.equals("base") || variant.equals("base-pseudo") || entry.equals("base") && variant.isEmpty());
                        for (Side side : Side.values()) {
                            for (boolean small : List.of(false, true)) {
                                for (int font : List.of(12, 16, 24)) {
                                    TabPane pane = new TabPane();
                                    pane.setManaged(false);
                                    pane.setSide(side);
                                    pane.getStyleClass().add("tab-pane-ui");
                                    if (small) pane.getStyleClass().add("tab-pane-ui-small");
                                    if (variant.equals("windows")) pane.getStyleClass().add("tab-pane-ui-windows");
                                    if (variant.equals("base")) pane.getStyleClass().add("tab-pane-ui-base");
                                    if (variant.equals("base-pseudo"))
                                        pane.pseudoClassStateChanged(PseudoClass.getPseudoClass("ui-base"), true);
                                    if (variant.equals("pseudo")) pane.pseudoClassStateChanged(
                                            PseudoClass.getPseudoClass("ui-windows"), true);
                                    pane.setStyle("-fx-font-size: " + font + "px; -fx-open-tab-animation: NONE; -fx-close-tab-animation: NONE;");
                                    pane.getTabs().addAll(new Tab("工作", new Label("工作中")), new Tab("摸鱼", new Label("摸鱼中")));
                                    root.getChildren().setAll(pane);
                                    root.applyCss();
                                    String key = entry + "/" + variant + "/" + side + "/" + small + "/" + font;
                                    for (int selected = 0; selected < 2; selected++) {
                                        pane.getSelectionModel().select(selected);
                                        root.applyCss();
                                        // Windows exercises natural sizing; Base checks appearance compatibility in a roomy host.
                                        if (windows) pane.resize(pane.prefWidth(-1), pane.prefHeight(-1));
                                        else pane.resize(600, 500);
                                        pane.layout();
                                        Region buttons = (Region) pane.lookup(".control-buttons-tab");
                                        check(!buttons.isVisible(), key + " false overflow at preferred size");
                                        Region headers = (Region) pane.lookup(".headers-region");
                                        Bounds clip = headers.getClip().getBoundsInLocal();
                                        for (var header : headers.getChildrenUnmodifiable()) {
                                            Bounds bounds = header.getBoundsInParent();
                                            check(bounds.getMinX() >= clip.getMinX() - .01 &&
                                                  bounds.getMaxX() <= clip.getMaxX() + .01, key + " clipped header");
                                        }
                                        Region current = (Region) headers.getChildrenUnmodifiable().stream()
                                                .filter(node -> node.getPseudoClassStates().contains(PseudoClass.getPseudoClass("selected")))
                                                .findFirst().orElseThrow();
                                        var fills = current.getBackground().getFills();
                                        if (windows) {
                                            check(fills.size() == 2 && fills.get(1).getInsets().getBottom() -
                                                                       fills.get(0).getInsets().getBottom() == 2, key + " missing selection indicator");
                                        } else {
                                            check(fills.size() == 1 && fills.getFirst().getInsets().getBottom() == (small ? 3 : 5),
                                                    key + " forced Base selected geometry");
                                        }
                                    }
                                    if (side == Side.TOP || side == Side.BOTTOM) pane.resize(70, 160);
                                    else pane.resize(160, 70);
                                    pane.layout();
                                    check(pane.lookup(".control-buttons-tab").isVisible(), key + " missing real overflow menu");
                                }
                            }
                        }
                    }
                }
                System.out.println("PASS TabPane preferred sizing, header visibility, small indicator and overflow: assertions=" + assertions);
            } catch (Throwable error) {
                failure.set(error);
            } finally {
                stage.close();
                done.countDown();
            }
        });
        done.await();
        Platform.exit();
        if (failure.get() != null) throw new AssertionError(failure.get());
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }
}
