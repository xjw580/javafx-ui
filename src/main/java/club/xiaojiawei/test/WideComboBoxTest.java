package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Calendar;
import club.xiaojiawei.controls.WideComboBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class WideComboBoxTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        WideComboBox<String> wideComboBox = new WideComboBox<>();
//        wideComboBox.getStyleClass().addAll("combo-box-ui", "combo-box-ui-small", "combo-box-ui-normal");
        wideComboBox.getItems().addAll(
                "1",
                "2",
                "3",
                "4"
        );
        Scene scene = new Scene(new HBox(wideComboBox, new ComboBox<>(){{setValue("hh");}}){{setAlignment(Pos.CENTER);setSpacing(50);}}, 400, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.runLater(() -> wideComboBox.getSelectionModel().select("hh"));
    }
}
