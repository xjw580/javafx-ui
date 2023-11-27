package club.xiaojiawei.test;

import club.xiaojiawei.controls.Time;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TimeTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Time time = new Time();
//        time.setShowSelector(false);
        time.timeProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().add(time);
//        flowPane.getChildren().add(new Text("hello"));
//        time.setTranslateX(50);
//        time.setTranslateY(50);
        Scene scene = new Scene(flowPane, 200, 200);
        stage.setScene(scene);
        stage.show();
    }

}
