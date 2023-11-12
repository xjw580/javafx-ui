package club.xiaojiawei.test;

import club.xiaojiawei.controls.FileUploader;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/9 15:59
 */
public class FileUploaderTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox vBox = new VBox(){{setAlignment(Pos.CENTER);}};
        HBox hBox = new HBox(){{setAlignment(Pos.CENTER);}};
        vBox.getChildren().add(hBox);
        FileUploader fileUploader = new FileUploader();
        fileUploader.setColumns(4);
        fileUploader.setMaxFileSize(8);
        fileUploader.getFileURLs().addListener((ListChangeListener<String>) c -> {
            System.out.print("[");
            for (String fileURL : fileUploader.getFileURLs()) {
                System.out.print(new File(fileURL).getName() + ", ");
            }
            System.out.println("]");
        });
        hBox.getChildren().add(fileUploader);
        Scene scene = new Scene(vBox, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
