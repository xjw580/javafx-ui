package club.xiaojiawei.test;

import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.controls.Time;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class TimeTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Time time = new Time();
        FlowPane group = new FlowPane();
        group.getChildren().add(time);
        time.setTranslateX(50);
        time.setTranslateY(50);
        Scene scene = new Scene(group, 200, 200);
        stage.setScene(scene);
        stage.show();
    }

}
