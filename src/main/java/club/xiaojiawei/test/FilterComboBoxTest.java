package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.FilterComboBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:59
 */
public class FilterComboBoxTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FilterComboBox<Object> comboBox = new FilterComboBox<>();
//        comboBox.setEditable(true);
        comboBox.getStyleClass().addAll("combo-box-ui", "combo-box-ui-normal", "combo-box-ui-big");
        comboBox.getItems().add("111");
        comboBox.getItems().add("122");
        comboBox.getItems().add("2222");
        comboBox.getItems().add("333");
        comboBox.getItems().add("3244");
        comboBox.getItems().add("4444444444");
        StackPane stackPane = new StackPane(comboBox);
        Scene scene = new Scene(stackPane, 500, 500);
        primaryStage.setScene(scene);
        JavaFXUI.addjavafxUIStylesheet(scene);
        comboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            System.out.println("index:" + t1);
        });
        primaryStage.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
//                    comboBox.getItems().add("aaaaaaaaa");
//                    System.out.println(comboBox.getItems());
//                    comboBox.getSelectionModel().select(3);
//                    System.out.println(11);
                });
            }
        }, 3000);
    }
}
