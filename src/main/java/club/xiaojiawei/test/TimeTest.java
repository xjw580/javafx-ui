package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Time;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Timer;
import java.util.TimerTask;

public class TimeTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Time time = new Time();
//        time.setShowSelector(false);
        time.timeProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
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
