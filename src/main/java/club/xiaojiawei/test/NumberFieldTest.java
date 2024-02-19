package club.xiaojiawei.test;

import club.xiaojiawei.controls.NumberField;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 15:19
 */
public class NumberFieldTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        NumberField numberField = new NumberField();
        Scene scene = new Scene(new StackPane(new Group(numberField)), 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
