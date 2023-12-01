package club.xiaojiawei.readme.tab.style;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/27 16:15
 */
public class TabViewController implements Initializable {
    @FXML
    private TableView<Student> tableViewSmallDemo;
    @FXML
    private TableView<Student> tableViewDemo;
    @FXML
    private TableView<Student> tableViewBigDemo;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Student> students = FXCollections.observableArrayList(
                new Student(1, "张三", "男"),
                new Student(2, "李四", "女"),
                new Student(3, "王五", "二次元"),
                new Student(1, "张三", "男"),
                new Student(2, "李四", "女"),
                new Student(3, "王五", "二次元"),
                new Student(1, "张三", "男"),
                new Student(2, "李四", "女"),
                new Student(3, "王五", "二次元"),
                new Student(1, "张三", "男"),
                new Student(2, "李四", "女"),
                new Student(3, "王五", "二次元")
        );
        // 设置 TableView 列和数据
        tableViewSmallDemo.setItems(students);
        tableViewSmallDemo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewDemo.setItems(students);
        tableViewDemo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewBigDemo.setItems(students);
        tableViewBigDemo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    @Data
    @AllArgsConstructor
    public static class Student{
        private int id;
        private String name;
        private String sex;
    }
}
