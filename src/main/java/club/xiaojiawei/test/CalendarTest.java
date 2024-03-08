package club.xiaojiawei.test;

import club.xiaojiawei.controls.Calendar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class CalendarTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Calendar calendar = new Calendar();
        calendar.setLocalDate(LocalDate.of(9999, 12, 1));
        calendar.dateReadOnlyProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        System.out.println(calendar.getDate());
        calendar.setTranslateX(50);
        calendar.setTranslateY(50);
        Scene scene = new Scene(new AnchorPane(calendar), 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
