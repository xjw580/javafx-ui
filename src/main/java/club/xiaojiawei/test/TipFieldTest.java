package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Calendar;
import club.xiaojiawei.controls.NumberField;
import club.xiaojiawei.controls.TipField;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class TipFieldTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        TipField tipField = new TipField();
        tipField.getStyleClass().addAll("text-field-ui", "text-field-ui-small");
        tipField.setMaxWidth(200);
        tipField.setTip("回车确认");

//        NumberField numberField = new NumberField();
//        numberField.getStyleClass().addAll("text-field-ui");
//        numberField.setMaxWidth(220);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(30);
        vBox.getChildren().addAll(tipField, new Button("click"));
        Scene scene = new Scene(new StackPane(vBox), 400, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
