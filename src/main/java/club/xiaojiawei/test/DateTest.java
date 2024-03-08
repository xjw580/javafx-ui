package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Date;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 15:54
 */
public class DateTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        HBox hBox = new HBox();
        VBox vBox = new VBox(hBox);
        vBox.setAlignment(Pos.CENTER);
        hBox.setPrefHeight(100);
        hBox.setAlignment(Pos.CENTER);
        Date date = new Date();
        date.setInterceptor(localDate -> LocalDate.now().isAfter(localDate) || LocalDate.now().equals(localDate));
        System.out.println(date.getDate());
        date.dateReadOnlyProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        Button refresh = new Button("refresh");
        refresh.setOnMouseClicked(mouseEvent -> date.refresh());
        hBox.getChildren().addAll(new Label("test"), date, refresh);
        Scene scene = new Scene(vBox, 400, 400);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle("hello");
        primaryStage.show();
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(() -> {
//                    date.setDate("2021/12/05");
//                });
//            }
//        }, 1500);
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(() -> {
//                    date.dateProperty().set(LocalDate.now());
//                });
//            }
//        }, 3000);
    }
}
