package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.MultiFileChooser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class MultiFileChooserTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        MultiFileChooser multiFileChooser = new MultiFileChooser();
        multiFileChooser.setTitle("多选文件夹");
        Scene scene = new Scene(new AnchorPane(new Button("click"){{
            setOnAction(event -> {
                multiFileChooser.showMultiFileDialog(primaryStage, files -> {
                    System.out.println("files:" + files);
                });
            });
        }}), 600, 700);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
