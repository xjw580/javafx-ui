package club.xiaojiawei.readme;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 8:57
 */
public class Readme extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("Readme.fxml")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
