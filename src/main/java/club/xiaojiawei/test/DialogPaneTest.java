package club.xiaojiawei.test;

import club.xiaojiawei.utils.DialogUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
            setStyle("-fx-padding: 20");
        }};
        Button click = new Button("click");
        hBox.getChildren().add(click);
        Scene scene = new Scene(hBox, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        click.setOnMouseClicked(event -> {
            DialogUtil.showDialog(hBox, "你好", "你也好你也好你也好你也好你也好你也好你也好", actionEvent -> {
                System.out.println("ok");
            }, actionEvent -> {
                System.out.println("cancel");
            });
        });
    }
}
