package club.xiaojiawei.test;

import club.xiaojiawei.controls.Calendar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
//        calendar.setLocalDate(LocalDate.of(9999, 12, 1));
//        calendar.dateProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
//        System.out.println(calendar.getDate());
        LocalDate localDate = LocalDate.of(9999, 12, 1);
        System.out.println(localDate);
        LocalDate localDate1 = localDate.plusMonths(1);
        System.out.println(localDate1);
        System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate1));
        calendar.setTranslateX(50);
        calendar.setTranslateY(50);
        Scene scene = new Scene(new AnchorPane(calendar), 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
