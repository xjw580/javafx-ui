package club.xiaojiawei.demo.tab.style;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URL;
import java.util.ResourceBundle;

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
    @FXML
    private TableView<Student> tableViewUprightDemo;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Student> students = FXCollections.observableArrayList(
                new Student(1, "张三", "男", 20, "2024/10/22"),
                new Student(2, "张三", "男", 21, "2002/05/22"),
                new Student(3, "张三", "男", 20, "2024/05/03"),
                new Student(4, "李四", "男", 22, "2024/02/22"),
                new Student(5, "王五", "女", 19, "2024/04/22"),
                new Student(6, "赵六", "女", 21, "2024/05/11"),
                new Student(6, "赵六1", "女", 21, "2024/05/12"),
                new Student(6, "赵六2", "女", 21, "2003/02/22"),
                new Student(6, "赵六3", "女", 21, "2024/05/14"),
                new Student(6, "赵六4", "女", 21, "2024/05/14"),
                new Student(6, "赵六5", "女", 21, "2006/02/22"),
                new Student(6, "赵六6", "女", 21, "2024/04/16"),
                new Student(6, "赵六7", "女", 21, "2001/04/22"),
                new Student(6, "赵六8", "女", 21, "2024/03/26"),
                new Student(6, "赵六9", "女", 21, "2024/04/13"),
                new Student(6, "赵六10", "女", 21, "2024/04/14")
        );
        // 设置 TableView 列和数据
        tableViewSmallDemo.setItems(students);
        tableViewSmallDemo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewDemo.setItems(students);
        tableViewDemo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewBigDemo.setItems(students);
        tableViewBigDemo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewUprightDemo.setItems(students);
        tableViewUprightDemo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    @Data
    @AllArgsConstructor
    public static class Student{
        private int id;
        private String name;
        private String sex;
        private int age;
        private String date;
    }
}
