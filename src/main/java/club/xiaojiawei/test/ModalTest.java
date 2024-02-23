package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Modal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/20 17:59
 */
public class ModalTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("click");
        button.setOnAction(actionEvent -> {
//            new Modal(primaryStage.getScene().getRoot(), new Label("head"), new HBox(new Label("body")){{setPrefWidth(200);}}, "-fx-padding: 15", new Button("click")).show();
            new Modal(primaryStage.getScene().getRoot(), "head", "body", new Button("cancel"), new Button("ok")).show();
        });
        StackPane stackPane = new StackPane(button);
        Scene scene = new Scene(stackPane, 300, 300);
        primaryStage.setScene(scene);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.show();
    }
}
