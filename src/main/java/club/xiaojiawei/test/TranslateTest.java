package club.xiaojiawei.test;

import club.xiaojiawei.controls.Carousel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 16:30
 */
public class TranslateTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("click");
        Text text = new Text("hhh");
        AnchorPane anchorPane = new AnchorPane(button);
        anchorPane.getChildren().add(text);
        button.setOnAction(actionEvent -> {
            anchorPane.setPrefWidth(150);
        });
        anchorPane.setPrefWidth(300);
        anchorPane.setRightAnchor(text, 5D);
        anchorPane.setTopAnchor(text, 250D);
        Scene scene = new Scene(new AnchorPane(anchorPane), 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
