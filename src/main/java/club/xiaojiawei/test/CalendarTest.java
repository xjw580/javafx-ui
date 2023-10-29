package club.xiaojiawei.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

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
//        Calendar calendar = new Calendar();
//        calendar.setTranslateX(50);
//        calendar.setTranslateY(50);
//        Scene scene = new Scene(new AnchorPane(calendar), 200, 200);
//        primaryStage.setScene(scene);
//        primaryStage.show();

//        Scene scene = new Scene();
        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource(""))));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
