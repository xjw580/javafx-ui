package club.xiaojiawei.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.ColorPickerSkin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/17 16:17
 */
public class
ColorPickerTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ColorPicker colorPicker = new ColorPicker(Color.BLACK) {
            @Override
            protected Skin<?> createDefaultSkin() {
                return new ColorPickerSkin(this) {
                    private boolean isModify;
                    @Override
                    public void show() {
                        if (!isModify) {
                            ((Region) getPopupContent().lookup(".color-palette")).getChildrenUnmodifiable().forEach(node -> {
                                if (node instanceof GridPane gridPane && gridPane.getChildren().size() == 12) {
                                    Class<?> aClass;
                                    try {
                                        aClass = Class.forName("javafx.scene.control.skin.ColorPalette$ColorSquare");
                                        Field rectangle = aClass.getDeclaredField("rectangle");
                                        rectangle.setAccessible(true);
                                        Object o1 = rectangle.get(gridPane.getChildren().get(3));
                                        if (o1 instanceof Rectangle rectangle1) {
                                            rectangle1.setFill(Color.BLACK);
                                            Tooltip.install(gridPane.getChildren().get(3), new Tooltip("黑色 #000000 (css:black)"));
                                            isModify = true;
                                        }
                                    } catch (ClassNotFoundException |
                                             IllegalAccessException | NoSuchFieldException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                List<String> styles = List.of("label", "separator", "hyperlink");
                                if ("ColorCustomizerColorGrid".equals(node.getId()) || node.getStyleClass().stream().anyMatch(styles::contains)) {
                                    node.setVisible(false);
                                    node.setManaged(false);
                                }
                            });
                        }
                        super.show();
                    }
                };
            }
        };
        HBox hBox = new HBox(colorPicker);
        Scene scene = new Scene(hBox, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
