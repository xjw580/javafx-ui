package club.xiaojiawei.test;

import club.xiaojiawei.controls.Modal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/19 14:33
 */
public class DialogPaneTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        HBox hBox = new HBox() {{
            setStyle("-fx-padding: 20;-fx-background-color: rgba(251, 251, 251)");
        }};
        Button click = new Button("click");
        hBox.getChildren().add(click);
        Scene scene = new Scene(hBox, 400, 400);
        primaryStage.setScene(scene);

        click.setOnMouseClicked(event -> {
        });
        primaryStage.show();
    }

}

