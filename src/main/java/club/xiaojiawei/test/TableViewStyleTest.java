package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/5 10:45
 */
public class TableViewStyleTest extends Application {

    public static class Person {
        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;

        public Person(String firstName, String lastName) {
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
        }

        public SimpleStringProperty firstNameProperty() {
            return firstName;
        }

        public SimpleStringProperty lastNameProperty() {
            return lastName;
        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String firstName) {
            this.firstName.set(firstName);
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String lastName) {
            this.lastName.set(lastName);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        TableView<Person> tableView = new TableView<>();
        tableView.getStyleClass().add("table-view-ui");
        ObservableList<Person> data = FXCollections.observableArrayList(
                new Person("John", "Doe"),
                new Person("Jane", "Doe"),
                new Person("Bob", "Smith"),
                new Person("Bob", "Smith"),
                new Person("Bob", "Smith")
        );

        // 创建表格列，并映射到对象的属性
        TableColumn<Person, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Person, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        tableView.getColumns().addAll(firstNameColumn, lastNameColumn);
        tableView.setItems(data);

        StackPane root = new StackPane(tableView);
        root.setStyle("-fx-padding: 10");
        Scene scene = new Scene(root, 300, 200);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle("TableView Object Mapping");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
