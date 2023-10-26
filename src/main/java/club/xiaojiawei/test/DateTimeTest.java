package club.xiaojiawei.test;

import club.xiaojiawei.controls.DateTime;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/26 10:04
 */
public class DateTimeTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DateTime dateTime = new DateTime();
        dateTime.setTranslateX(50);
        dateTime.setTranslateY(50);
        Scene scene = new Scene(new FlowPane(dateTime), 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
