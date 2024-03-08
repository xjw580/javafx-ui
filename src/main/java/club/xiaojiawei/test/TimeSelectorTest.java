package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.TimeSelector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/25 17:34
 */
public class TimeSelectorTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        ObservableList<Node> children = hBox.getChildren();
        TimeSelector timeSelector1 = new TimeSelector();
        timeSelector1.setShowSec(true);
        System.out.println(timeSelector1.getTime());
        timeSelector1.timeReadOnlyProperty().addListener((observableValue, localTime, t1) -> System.out.println(t1));
        children.add(timeSelector1);
        Scene scene = new Scene(hBox);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                timeSelector1.setTime("12:30");
//            }
//        }, 1000);
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                timeSelector1.setTime(null);
//            }
//        }, 3000);
    }
}
