package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 15:54
 */
public class DateTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FlowPane vBox = new FlowPane();
        Date date = new Date();
//        date.setDateInterceptor(localDate -> LocalDate.now().isAfter(localDate) || LocalDate.now().equals(localDate));
        System.out.println(date.getDate());
        date.dateProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        date.setTranslateX(50);
        date.setTranslateY(50);
        vBox.getChildren().add(date);
        Scene scene = new Scene(vBox, 200, 200);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    date.setDate("2021/12/05");
                });
            }
        }, 1500);
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(() -> {
//                    date.dateProperty().set(LocalDate.now());
//                });
//            }
//        }, 3000);
    }
}
