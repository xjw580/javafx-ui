package club.xiaojiawei.test;

import club.xiaojiawei.controls.DateSelector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/24 23:17
 */
public class DateSelectorTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        DateSelector dateSelector = new DateSelector();
        dateSelector.dateProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        Scene scene = new Scene(dateSelector);
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.runLater(() -> {
            Popup popup = new Popup();
            DateSelector dateSelector1 = new DateSelector();
            popup.getContent().add(dateSelector1);
            popup.show(primaryStage);
        });
    }
}
