package club.xiaojiawei.test;

import club.xiaojiawei.controls.Date;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.function.Predicate;

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
        date.setDateInterceptor(new Predicate<LocalDate>() {
            @Override
            public boolean test(LocalDate localDate) {
                return LocalDate.now().isAfter(localDate) || LocalDate.now().equals(localDate);
            }
        });
        System.out.println(date.getDate());
        date.dateProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        date.setTranslateX(50);
        date.setTranslateY(50);
        vBox.getChildren().add(date);
        Scene scene = new Scene(vBox, 200, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
