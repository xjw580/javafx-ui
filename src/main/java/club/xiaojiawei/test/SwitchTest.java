package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SwitchTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FlowPane titledPane = new FlowPane();
        Switch aSwitch = new Switch();
        aSwitch.setStatus(false);
        aSwitch.setTransitionType(BaseTransitionEnum.SLIDE_X);
//        aSwitch.statusProperty().addListener((observableValue, aBoolean, t1) -> {
//            if (!t1) {
//                Platform.runLater(() -> {
//                    aSwitch.setStatus(true);
//                });
//            }
//        });
//        Thread.ofVirtual().start(()->{
//            try {
//                Thread.sleep(2000);
//                aSwitch.setStatus(false);
//                Thread.sleep(2000);
//                aSwitch.setStatus(true);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//
//            }
//        });
        aSwitch.setTranslateX(50);
        aSwitch.setTranslateY(50);
        titledPane.getChildren().add(aSwitch);
        Scene scene = new Scene(titledPane, 200, 200);
        JavaFXUI.addjavafxUIStylesheet(scene);
        stage.setScene(scene);
        stage.show();
    }
}
