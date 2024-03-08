package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.DateTime;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;

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
        System.out.println(dateTime.getDateTime());
        dateTime.dateTimeRealOnlyProperty().addListener((observableValue, localDateTime, t1) -> System.out.println(t1));
        dateTime.setInterceptor(localDateTime1 -> {
            LocalDateTime localDateTime = LocalDateTime.of(2024, 3, 8, 14, 40);
            return localDateTime1.isAfter(localDateTime);
        });
        dateTime.setTranslateX(50);
        dateTime.setTranslateY(50);
        Button refresh = new Button("refresh");
        refresh.setOnMouseClicked(mouseEvent -> dateTime.refresh());
        Scene scene = new Scene(new FlowPane(dateTime, refresh), 300, 200);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
