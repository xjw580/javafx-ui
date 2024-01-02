package club.xiaojiawei.test;

import club.xiaojiawei.controls.Notification;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.JavaFXUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/1 21:29
 */
public class NotificationManagerTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane hBox = new BorderPane();
        hBox.setStyle("-fx-padding: 20");
        NotificationManager notificationManager = new NotificationManager();

        Button click = new Button("click");
        click.setOnMouseClicked(mouseEvent -> {
            System.out.println("click");
        });
        hBox.setCenter(new StackPane(click, notificationManager));
//        hBox.getChildren().add(click);
//        hBox.getChildren().add(notificationManager);
        Scene scene = new Scene(hBox, 500, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    notificationManager.showSuccess("保存成功", 20);
                    notificationManager.showSuccess("保存成功", 20);
                });
            }
        }, 500);
    }
}
