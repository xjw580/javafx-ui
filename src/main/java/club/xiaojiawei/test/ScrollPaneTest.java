package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.List;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class ScrollPaneTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Data
    @AllArgsConstructor
    public static class Student {
        private String name;
        private int age;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        TableView<Student> tableView = new TableView<>();
        tableView.getColumns().addAll(
                new TableColumn<>("姓名") {{
                    setCellValueFactory(new PropertyValueFactory<>("name"));
                }},
                new TableColumn<>("年龄") {{
                    setCellValueFactory(new PropertyValueFactory<>("age"));
                }}
        );
        tableView.getItems().addAll(List.of(
                new Student("zs", 12),
                new Student("ls", 53),
                new Student("ww", 51),
                new Student("xjw", 18),
                new Student("fsdf", 43)
        ));
        Scene scene = new Scene(new StackPane(tableView), 400, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
