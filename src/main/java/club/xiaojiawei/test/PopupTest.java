package club.xiaojiawei.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/26 0:08
 */
public class PopupTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(buildFlowPane());
        primaryStage.setScene(scene);
        primaryStage.show();
        Platform.runLater(() -> {
            Popup popup = new Popup();
            popup.getContent().add(buildFlowPane());
            popup.setAnchorX(600);
            popup.setAnchorY(1500);
            popup.show(primaryStage);
        });
    }
    private FlowPane buildFlowPane(){
        FlowPane flowPane = new FlowPane(){{setStyle("""
                    -fx-padding: 2;
                    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 0, 0);
                    -fx-background-radius: 5;
                    -fx-background-color: white;
                    """);}};
        flowPane.setMinWidth(86);
        flowPane.setMaxWidth(86);
        flowPane.setHgap(1.5);
        flowPane.getChildren().add(buildScrollPane());
        flowPane.getChildren().add(buildScrollPane());
        return flowPane;
    }
    private ScrollPane buildScrollPane(){
        ScrollPane scrollPane = new ScrollPane(){{setStyle("""
                -fx-padding: 0;
                -fx-background-color: transparent;
                -fx-border-color: transparent;
                -fx-background-insets: 0;
                -fx-hbar-policy: never;
                -fx-vbar-policy: never;
                """);}};
        scrollPane.setMaxHeight(180);
        VBox vBox = new VBox(){{setStyle("-fx-background-color: white;");}};
        ObservableList<Node> children = vBox.getChildren();
        for (int i = 0; i < 24; i++) {
            children.add(buildLabel(String.valueOf(i)));
        }
        scrollPane.setContent(vBox);
        return scrollPane;
    }
    private Label buildLabel(String text){
        return new Label(text){{setStyle("-fx-pref-width: 38;-fx-pref-height: 29;-fx-alignment: CENTER;-fx-background-radius: 5;-fx-background-color: white");}};
    }
}
