package club.xiaojiawei.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/30 12:39
 */
public class ToggleButtonTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ToggleButton play = new ToggleButton("play");
        ToggleButton pause = new ToggleButton("pause");
        ToggleGroup toggleGroup = new ToggleGroup();
        play.setToggleGroup(toggleGroup);
        play.selectedProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        pause.setToggleGroup(toggleGroup);
        HBox hBox = new HBox(play, pause);
        hBox.setSpacing(20);
        hBox.getChildren().add(new Button("next"));
        Scene scene = new Scene(hBox, 200, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
