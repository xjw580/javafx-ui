package club.xiaojiawei.test;

import club.xiaojiawei.controls.PasswordTextField;
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
        PasswordTextField passwordTextField = new PasswordTextField();
        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        AnchorPane anchorPane = new AnchorPane();
        passwordTextField.setTranslateX(50);
        passwordTextField.setTranslateY(50);
        anchorPane.getChildren().add(passwordTextField);
        Scene scene = new Scene(anchorPane, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
