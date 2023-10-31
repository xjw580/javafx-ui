package club.xiaojiawei.test;

import club.xiaojiawei.controls.PasswordShowField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 14:27
 */
public class PasswordShowFieldTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        PasswordShowField passwordShowField = new PasswordShowField();
        passwordShowField.textProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        AnchorPane anchorPane = new AnchorPane();
        passwordShowField.setTranslateX(50);
        passwordShowField.setTranslateY(50);
        anchorPane.getChildren().add(passwordShowField);
        Scene scene = new Scene(anchorPane, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
