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
        FilterComboBox<Student> comboBox = new FilterComboBox<>();
//        comboBox.setEditable(true);
        comboBox.getStyleClass().addAll("combo-box-ui", "combo-box-ui-normal");
        comboBox.setPrefWidth(150);
        comboBox.getItems().add(new Student("dfs", 121));
        comboBox.getItems().add(new Student("dfs1", 122));
        comboBox.getItems().add(new Student("dfs2", 123));
        comboBox.getItems().add(new Student("dfs3", 124));
//        comboBox.getItems().add("122");
//        comboBox.getItems().add("2222");
//        comboBox.getItems().add("333");
//        comboBox.getItems().add("3244");
//        comboBox.getItems().add("4444444444");
        comboBox.getSelectionModel().select(1);
        StackPane stackPane = new StackPane(comboBox);
        Scene scene = new Scene(stackPane, 500, 500);
        primaryStage.setScene(scene);
        JavaFXUI.addjavafxUIStylesheet(scene);
        comboBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            System.out.println("index:" + t1);
            System.out.println(comboBox.getSelectionModel().getSelectedItem().age);
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
        }, 1000);
    }

    static class Student{
        String name;
        int age;
        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }
        public Student() {}

        @Override
        public String toString() {
            return name;
        }
    }
}
