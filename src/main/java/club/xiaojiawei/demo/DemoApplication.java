package club.xiaojiawei.demo;

import club.xiaojiawei.JavaFXUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 8:57
 */
public class DemoApplication extends Application{

    private boolean controlPressed = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DemoApplication.class.getResource("Demo.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(JavaFXUI.class.getResourceAsStream("/club/xiaojiawei/demo/demo.png"))));
        primaryStage.showingProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1){
                System.exit(0);
            }
        });

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.CONTROL){
                controlPressed = true;
            }else if (keyEvent.getCode() == KeyCode.W && controlPressed){
                DemoController controller = fxmlLoader.getController();
                controller.closeCurrentTab();
            }
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.CONTROL){
                controlPressed = false;
            }
        });
        primaryStage.setTitle("Demo");
        primaryStage.show();
    }
}
