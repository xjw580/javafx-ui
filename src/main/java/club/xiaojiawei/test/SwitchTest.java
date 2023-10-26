package club.xiaojiawei.test;

import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.application.Application;
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
        aSwitch.setTranslateX(50);
        aSwitch.setTranslateY(50);
        titledPane.getChildren().add(aSwitch);
        Scene scene = new Scene(titledPane, 200, 200);
        stage.setScene(scene);
        stage.show();
    }
}
