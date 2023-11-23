package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.LabelUI;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/23 17:23
 */
public class LabelUITest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LabelUI labelUI = new LabelUI("HELLO");
        labelUI.setPrefHeight(40);
        labelUI.setPrefWidth(220);
        Scene scene = new Scene(new Group(labelUI), 400, 400);
        scene.getStylesheets().add(JavaFXUI.javafxUIStylesheet());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
