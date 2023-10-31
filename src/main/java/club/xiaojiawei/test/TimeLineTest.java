package club.xiaojiawei.test;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/30 16:19
 */
public class TimeLineTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button play = new Button("play");
        ScrollPane scrollPane = new ScrollPane();
        VBox vBox = new VBox(play);
        vBox.setPrefWidth(200);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        for (int i = 0; i < 50; i++) {
            vBox.getChildren().add(new Label(String.valueOf(i)));
        }
        scrollPane.setContent(vBox);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0), "one", event -> {
            System.out.println("one");
        }, new KeyValue(scrollPane.vvalueProperty(), 0.5));
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(3), "two", event -> {
            System.out.println("two");
        }, new KeyValue(scrollPane.vvalueProperty(), 1));
        timeline.getKeyFrames().setAll(keyFrame, keyFrame2);
        play.setOnMouseClicked(event -> {
            timeline.play();
        });
        Scene scene = new Scene(scrollPane, 200, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    System.out.println(scrollPane.getHeight());
                    System.out.println(scrollPane.getPrefViewportHeight());
                    System.out.println(scrollPane.getPrefHeight());
                    System.out.println("-------------");
                    System.out.println(vBox.getHeight());
                    System.out.println(vBox.getPrefHeight());
                    System.out.println(((Region)scrollPane.getContent()).getHeight());
                });
            }
        }, 1000);

    }
}
