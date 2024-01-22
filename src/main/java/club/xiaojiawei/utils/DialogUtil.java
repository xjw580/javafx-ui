package club.xiaojiawei.utils;

import club.xiaojiawei.JavaFXUI;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/19 15:17
 */
public class DialogUtil {

    @SafeVarargs
    public static void showDialog(Parent parent, String headText, String bodyText, EventHandler<ActionEvent>... btnHandler){
        Window window = parent.getScene().getWindow();
        Stage stage = new Stage();
        double height = window.getHeight() - 8;
        double width = parent.getScene().getWidth();
        VBox vBox = new VBox(){{setSpacing(20);setAlignment(Pos.CENTER);}};
        vBox.setStyle("-fx-background-color: white;-fx-padding: 18 13 13 18;-fx-background-radius: 3;-fx-spacing: 15");
        vBox.setPrefWidth(350);
        Group group = new Group(vBox);
        vBox.getChildren().addAll(buildHead(headText), buildBody(bodyText), buildBtnGroup(stage, group, btnHandler));
        StackPane stackPane = new StackPane(group);
        stackPane.setClip(new Rectangle(width, height){{setArcHeight(20);setArcWidth(20);}});
        stackPane.setStyle("-fx-background-color: rgba(0,0,0,0.05);");
        Scene scene = new Scene(stackPane, width, height);
        scene.getStylesheets().addAll(JavaFXUI.stylesheet("common.css"), JavaFXUI.stylesheet("button.css"));
        scene.setFill(null);
        stage.setScene(scene);
        stage.initOwner(window);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE){
                if (btnHandler != null && btnHandler.length > 0){
                    if (btnHandler[btnHandler.length - 1] != null){
                        btnHandler[btnHandler.length - 1].handle(null);
                    }
                }
                hideByTransition(stage, group);
            }
        });
        stage.show();
        buildShowTransition(group).play();
    }

    private static ScaleTransition buildShowTransition(Node node){
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), node);
        scaleTransition.setFromX(0);
        scaleTransition.setToX(1);
        scaleTransition.setFromY(0);
        scaleTransition.setToY(1);
        return scaleTransition;
    }
    private static ScaleTransition buildHideTransition(Node node){
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), node);
        scaleTransition.setFromX(1);
        scaleTransition.setToX(0);
        scaleTransition.setFromY(1);
        scaleTransition.setToY(0);
        return scaleTransition;
    }

    private static HBox buildHead(String headText){
        HBox hBox = new HBox(){{setAlignment(Pos.TOP_LEFT);}};
        if (headText != null && !headText.isBlank()){
            Label label = new Label(headText);
            label.setStyle("-fx-font-weight: bold;-fx-font-size: 12;-fx-wrap-text: true");
            hBox.getChildren().add(label);
        }
        return hBox;
    }

    private static HBox buildBody(String bodyText){
        HBox hBox = new HBox(){{setAlignment(Pos.TOP_LEFT);setStyle("-fx-padding: 0 0 0 10");}};
        if (bodyText != null && !bodyText.isBlank()){
            Label label = new Label(bodyText);
            label.setStyle("-fx-font-size: 14;-fx-wrap-text: true");
            hBox.getChildren().add(label);
        }
        return hBox;
    }

    @SafeVarargs
    private static HBox buildBtnGroup(Stage stage, Node node,EventHandler<ActionEvent>... btnHandler){
        HBox hBox = new HBox(){{setAlignment(Pos.CENTER_RIGHT);setStyle("-fx-padding: 10 0 0 0;-fx-spacing: 10");}};
        Button okBtn = new Button("确定");
        okBtn.getStyleClass().addAll("btn-ui", "btn-ui-success");
        okBtn.setOnAction(actionEvent -> {
            if (btnHandler != null && btnHandler.length > 0 && btnHandler[0] != null){
                btnHandler[0].handle(actionEvent);
            }
            hideByTransition(stage, node);
        });
        hBox.getChildren().add(okBtn);
        if (btnHandler != null && btnHandler.length > 1){
            Button cancelBtn = new Button("取消");
            cancelBtn.getStyleClass().addAll("btn-ui");
            cancelBtn.setOnAction(actionEvent -> {
                if (btnHandler[1] != null){
                    btnHandler[1].handle(actionEvent);
                }
                hideByTransition(stage, node);
            });
            hBox.getChildren().add(cancelBtn);
        }
        return hBox;
    }

    private static void hideByTransition(Stage stage, Node node){
        ScaleTransition scaleTransition = buildHideTransition(node);
        scaleTransition.setOnFinished(actionEvent -> stage.close());
        scaleTransition.play();
    }

}
