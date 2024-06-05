package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.ProgressModal;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class ProgressTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        ProgressModal progressModal = new ProgressModal();
        Button click = new Button("click");
        click.setOnAction(event -> {
            DoubleProperty progressProperty = progressModal.show("加载中");
            new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                    progressProperty.set(progressProperty.get() + 0.01);
                }
                progressModal.hide(progressProperty);
            }).start();
        });
        Scene scene = new Scene(new VBox(click, progressModal), 400, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
