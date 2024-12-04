package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Calendar;
import club.xiaojiawei.controls.MultiDirectoryChooser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class MultiDirectoryChooserTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        MultiDirectoryChooser multiDirectoryChooser = new MultiDirectoryChooser();
        multiDirectoryChooser.setTitle("多选文件夹");
        Scene scene = new Scene(new AnchorPane(new Button("click"){{
            setOnAction(event -> {
                multiDirectoryChooser.showDialog(primaryStage, files -> {
                    System.out.println("files:" + files);
                });
            });
        }}), 600, 700);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
