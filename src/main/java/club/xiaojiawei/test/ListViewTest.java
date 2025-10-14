package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Calendar;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class ListViewTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        ListView<String> listView = new ListView<>();
        listView.getStyleClass().addAll("list-view-ui", "list-view-ui-big");
        listView.setItems(FXCollections.observableArrayList(
                "苹果", "香蕉", "橙子", "葡萄", "草莓",
                "西瓜", "芒果", "樱桃", "桃子", "梨"
        ));

        VBox root = new VBox(listView);
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root, 400, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);

        stage.setTitle("ListView CSS 美化示例");
        stage.setScene(scene);
        stage.show();
    }
}
