package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class PaginationTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pagination pagination = new Pagination();
//        pagination.getStyleClass().addAll("pagination-ui");
//        pagination.getStyleClass().addAll("pagination-ui", "pagination-ui-normal", "pagination-ui-small");
//        pagination.getStyleClass().addAll("pagination-ui", "pagination-ui-normal", "pagination-ui-big");
        pagination.getStyleClass().addAll("pagination-ui", "pagination-ui-normal");
//        pagination.getStyleClass().addAll("pagination-ui", "pagination-ui-success");
//        pagination.getStyleClass().addAll("pagination-ui", "pagination-ui-warn");
//        pagination.getStyleClass().addAll("pagination-ui", "pagination-ui-error");
        pagination.setCurrentPageIndex(10);
        pagination.setPageCount(20);
        pagination.setMaxPageIndicatorCount(3);
        pagination.setPageFactory(pageIndex -> {
            Label label = new Label("这是第 " + (pageIndex + 1) + " 页");
            label.setStyle("-fx-font-size: 16px;");
            return new StackPane(label); // 返回一个布局容器
        });
        Scene scene = new Scene(new StackPane(pagination), 400, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
