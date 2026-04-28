package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.NumberSelector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/05/23 10:10
 */
public class NumberSelectorTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        
        NumberSelector numberSelector = new NumberSelector();
        numberSelector.setMin(10);
        numberSelector.setMax(50);


        numberSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Selected value: " + newVal);
        });
        
        hBox.getChildren().add(numberSelector);
        
        Scene scene = new Scene(hBox, 300, 400);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle("NumberSelector Test");
        primaryStage.show();
        Platform.runLater(()->{
            numberSelector.setValue(35);
        });
    }
}
