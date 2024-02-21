package club.xiaojiawei.demo;

import club.xiaojiawei.JavaFXUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 8:57
 */
public class DemoApplication extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Demo.fxml"))));
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(JavaFXUI.class.getResourceAsStream("/club/xiaojiawei/demo/demo.png"))));
        primaryStage.showingProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1){
                System.exit(0);
            }
        });
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }
}
