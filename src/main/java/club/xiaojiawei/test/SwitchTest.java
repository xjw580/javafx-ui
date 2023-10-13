package club.xiaojiawei.test;

import club.xiaojiawei.controls.Switch;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SwitchTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Group group = new Group();
        Switch aSwitch = new Switch();
        group.getChildren().add(aSwitch);
        Scene scene = new Scene(group, 200, 200);
        stage.setScene(scene);
        stage.show();
    }
}
