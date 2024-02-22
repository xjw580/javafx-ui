package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.FilterField;
import club.xiaojiawei.controls.NumberField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 15:19
 */
public class FilterFieldTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FilterField filterField = new FilterField();
        filterField.setOnFilterAction(System.out::println);
        filterField.getStyleClass().addAll("text-field-ui", "text-field-ui-small");
        Scene scene = new Scene(new StackPane(new Group(filterField)), 500, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
