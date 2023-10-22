package club.xiaojiawei.test;

import club.xiaojiawei.controls.Switch;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SwitchTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane titledPane = new AnchorPane();
        Switch aSwitch = new Switch();
        titledPane.getChildren().add(aSwitch);
        aSwitch.setTranslateX(50);
        aSwitch.setTranslateY(50);
        Scene scene = new Scene(titledPane, 200, 200);
        stage.setScene(scene);
        stage.show();
    }
}
