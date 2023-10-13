package club.xiaojiawei.test;

import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.controls.Time;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TimeTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        VBox vBox = new VBox();
        Time time = new Time();
//        time.setShowSelector(false);
        Switch aSwitch = new Switch();
        vBox.getChildren().add(aSwitch);
        vBox.getChildren().add(new Switch());
        vBox.getChildren().add(new Switch());
        vBox.getChildren().add(new Switch());
        vBox.getChildren().add(time);
        time.timeProperty().addListener((observableValue, s, t1) -> System.out.println(t1));
        Scene scene = new Scene(vBox, 200, 200);
        stage.setScene(scene);
        stage.show();
    }

}
