package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Notification;
import club.xiaojiawei.controls.Time;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/1 21:29
 */
public class NotificationTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-padding: 20");
        Notification notification = new Notification();
        notification.setTitle("空指针异常");
        notification.setContent("顶顶顶顶的点点滴滴");
        hBox.getChildren().add(notification);
        hBox.getChildren().add(new Label("hello"));
        Scene scene = new Scene(hBox, 500, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(notification::show);
            }
        }, 5000);
    }
}
