package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.stream.IntStream;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/14 17:12
 */
public class AdaptationTest extends Application {

    public ScrollPane bottomScrollPane;
    public StackPane bottomContent;
    public VBox bottomTagPane;
    public Label bottomTag1;
    public Label bottomTag2;
    public Label bottomTag3;

    public static void main(String[] args) {
        launch(args);
    }

    public void initialize(){
        System.out.println("bottomScrollPane prefWidth:" + bottomScrollPane.getPrefWidth());
        System.out.println("bottomScrollPane width:" + bottomScrollPane.getWidth());
        System.out.println("bottomContent prefWidth:" + bottomContent.getPrefWidth());
        System.out.println("bottomContent width:" + bottomContent.getWidth());
        bottomContent.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("bottomContent change prefWidth:" + newValue);
            calcItemH();
        });
        bottomContent.widthProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("bottomContent change width:" + newValue);
            Stage stage = (Stage) bottomScrollPane.getScene().getWindow();
            System.out.println(stage.isMaximized());
            if (stage.isMaximized() && newValue.doubleValue() > bottomContent.getPrefWidth()) {
                bottomContent.setPrefWidth(newValue.doubleValue());
                bottomContent.setMinWidth(newValue.doubleValue());
                bottomContent.setMaxWidth(newValue.doubleValue());
            }
        });
        bottomTagPane.getChildren().addListener((ListChangeListener<? super Node>) observable -> {
            calcItemH();
        });
    }

    private void calcItemH(){
        long count = bottomTagPane.getChildren().stream().filter(Node::isManaged).count();
        double itemH = bottomTagPane.getHeight() / count;
        for (Node child : bottomTagPane.getChildren()) {
            if (child instanceof Region region) {
                region.setPrefHeight(itemH);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("AdaptationTest.fxml")));
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);

        primaryStage.setOnShowing(event -> {
            System.out.println("setOnShowing");
        });
        primaryStage.show();
        primaryStage.setMaximized(true);
    }
}
