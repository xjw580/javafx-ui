package club.xiaojiawei.readme.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
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
    private TableView<Student> tableViewDemo;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableViewDemo.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        // 设置 TableView 列和数据
        tableViewDemo.setItems(FXCollections.observableArrayList(
                new Student(1, "张三", "男"),
                new Student(2, "李四", "女"),
                new Student(3, "王五", "二次元")
        ));
    }
    @Data
    @AllArgsConstructor
    public static class Student{
        private int id;
        private String name;
        private String sex;
    }
}
