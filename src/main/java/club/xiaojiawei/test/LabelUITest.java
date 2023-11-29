package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Title;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
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
        Title title = new Title("HELLO");
        title.setPrefHeight(40);
        title.setPrefWidth(220);
        Scene scene = new Scene(new Group(title), 400, 400);
        scene.getStylesheets().add(JavaFXUI.javafxUIStylesheet());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
