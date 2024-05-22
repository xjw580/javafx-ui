package club.xiaojiawei.test;

import club.xiaojiawei.controls.ico.ImportIco;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/21 16:41
 */
public class ImportIcoTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ImportIco importIco = new ImportIco();
        importIco.setColor("red");
        Scene scene = new Scene(new StackPane(importIco), 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
