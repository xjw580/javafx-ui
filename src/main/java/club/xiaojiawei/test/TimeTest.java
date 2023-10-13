package club.xiaojiawei.test;

import club.xiaojiawei.controls.Time;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TimeTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Group group = new Group();
        Time time = new Time();
        time.setShowSelector(false);
//        group.getChildren().add(new Switch());
        time.timeProperty().addListener((observableValue, s, t1) -> System.out.println(t1));
        group.getChildren().add(time);
        Scene scene = new Scene(group, 200, 200);
        stage.setScene(scene);
        stage.show();
    }

}
