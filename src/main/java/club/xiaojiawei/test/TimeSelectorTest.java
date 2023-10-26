package club.xiaojiawei.test;

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
        children.add(new TimeSelector());
        Scene scene = new Scene(hBox);
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.runLater(() -> {
            Popup popup = new Popup();
            TimeSelector timeSelector = new TimeSelector();
            popup.getContent().add(timeSelector);
            popup.show(primaryStage);
        });
    }
}
