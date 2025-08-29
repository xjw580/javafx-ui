package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.DurationTime;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

public class DurationTimeTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DurationTime time = new DurationTime(99, 59, 59, 0, 0, 0);
        time.setShowSec(true);
        time.focusedReadOnlyProperty().addListener((observableValue, aBoolean, t1) -> {
            System.out.println("focus:" + t1);
        });
//        time.setShowSelector(false);
        time.readOnlyTimeProperty().addListener((observable, oldValue, newValue) -> System.out.println("time:" + newValue));
        time.setInterceptor(localTime -> {
            return true;
        });
        FlowPane flowPane = new FlowPane();
//        flowPane.getChildren().add(new Text("hello"));
//        time.setTranslateX(50);
//        time.setTranslateY(50);
        Button refresh = new Button("refresh");
        refresh.setOnMouseClicked(mouseEvent -> time.refresh());
        flowPane.getChildren().addAll(time, refresh);
        Scene scene = new Scene(flowPane, 200, 200);
        JavaFXUI.addjavafxUIStylesheet(scene);
        stage.setScene(scene);

        stage.show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    time.setDuration(Duration.ofHours(22).plusMinutes(34).plusSeconds(59));
                });
            }
        }, 1500);
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                time.setTime("00:00");
//            }
//        }, 1500);
//
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                time.setTime(null);
//            }
//        }, 3000);
    }

}
