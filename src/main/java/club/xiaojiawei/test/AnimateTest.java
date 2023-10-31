package club.xiaojiawei.test;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 1:03
 */
public class AnimateTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root = new AnchorPane();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setTranslateX(50);
        anchorPane.setTranslateY(50);
        root.getChildren().add(anchorPane);
        int height = 32;
        int width = 60;
        Rectangle bgBottom = new Rectangle(){{
            setHeight(height);
            setWidth(width);
            setFill(Paint.valueOf("0075FF33"));
            setArcHeight(5D);
            setArcWidth(5D);
        }};
        Rectangle bgTop = new Rectangle(){{
            setHeight(height);
            setWidth(width);
            setFill(Paint.valueOf("0075FF"));
            setArcHeight(5D);
            setArcWidth(5D);
        }};
        Label label = new Label(){{
            setText("Hello");
            setPrefHeight(height);
            setPrefWidth(width);
            setAlignment(Pos.CENTER);
            setBackground(new Background(new BackgroundFill(Color.web("transparent"), new CornerRadii(5D), Insets.EMPTY)));
        }};
        Circle circle = new Circle();
        circle.setRadius(85D);
        circle.setScaleX(0D);
        circle.setScaleY(0D);
        bgTop.setClip(circle);
        anchorPane.getChildren().add(bgBottom);
        anchorPane.getChildren().add(bgTop);
        anchorPane.getChildren().add(label);
        label.setOnMouseClicked(event -> {
            circle.setTranslateX(event.getX());
            circle.setTranslateY(event.getY());
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(800D), circle);
            scaleTransition.setFromX(0D);
            scaleTransition.setFromY(0D);
            scaleTransition.setToX(1D);
            scaleTransition.setToY(1D);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(800D), bgTop);
            fadeTransition.setFromValue(1D);
            fadeTransition.setToValue(0.1D);
            ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
            parallelTransition.play();
        });
        stage.setScene(new Scene(root, 300, 300));
        stage.show();
    }
}
