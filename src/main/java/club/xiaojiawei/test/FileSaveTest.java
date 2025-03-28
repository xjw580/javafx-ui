package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.MultiFileChooser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class FileSaveTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        MultiFileChooser multiFileChooser = new MultiFileChooser();
        multiFileChooser.setTitle("保存图片");
        Scene scene = new Scene(new AnchorPane(new Button("click") {{
            setOnAction(event -> {
//                multiFileChooser.showMultiFileDialog(primaryStage, files -> {
//                    System.out.println("files:" + files);
//                });
                multiFileChooser.showSaveFileDialog(primaryStage, files -> {
                    System.out.println("files:" + files);
                }, List.of(
                        new FileChooser.ExtensionFilter("PNG", "png")
                ));
            });
        }}), 600, 700);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
