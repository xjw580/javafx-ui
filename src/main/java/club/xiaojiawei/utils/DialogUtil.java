package club.xiaojiawei.utils;

import club.xiaojiawei.JavaFXUI;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
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
import javafx.scene.layout.Pane;
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

    public static Stage showDialog(Parent parent, Node node){
        return showDialog(parent, new Stage(), node);
    }

    @SafeVarargs
    public static Stage showDialog(Parent parent, String headText, String bodyText, EventHandler<ActionEvent>... btnHandler){
        Stage stage = new Stage();
        VBox vBox = new VBox(){{setSpacing(20);setAlignment(Pos.CENTER);}};
        vBox.setStyle("-fx-padding: 20 15 15 20;-fx-spacing: 15;");
        vBox.setPrefWidth(350);
        Group group = new Group(vBox);
        vBox.getChildren().addAll(buildHead(headText), buildBody(bodyText), buildBtnGroup(stage, group, btnHandler));
        showDialog(parent, stage, vBox, btnHandler);
        return stage;
    }

    @SafeVarargs
    private static Stage showDialog(Parent parent, Stage stage, Node node, EventHandler<ActionEvent>... btnHandler){
        Window window = parent.getScene().getWindow();
        double height = parent.getScene().getHeight();
        double width = parent.getScene().getWidth();
        Group group = new Group(new StackPane(node){{setStyle("-fx-background-color: rgba(251, 251, 251);-fx-background-radius: 3;-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 3, 3)");}});
        StackPane stackPane = new StackPane(group);
        stackPane.setClip(new Rectangle(width, height){{setArcHeight(10);setArcWidth(10);}});
        stackPane.setStyle("-fx-background-color: rgba(0,0,0,0.05);");
        Scene scene = new Scene(stackPane, width, height);
        JavaFXUI.addjavafxUIStylesheet(scene);
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
        stage.showingProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1){
                stage.setX(window.getX() + 8);
                stage.setY(window.getY());
            }
        });
        stage.setResizable(false);
        stage.show();
        stage.setY(parent.getScene().getWindow().getY() + parent.getScene().getY());
        buildShowTransition(stackPane, group).play();
        return stage;
    }

    private static Transition buildShowTransition(Node root, Node content){
        double duration = 250D;
        ParallelTransition parallelTransition = new ParallelTransition();
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), root);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), content);
        scaleTransition.setFromX(0);
        scaleTransition.setToX(1);
        scaleTransition.setFromY(0);
        scaleTransition.setToY(1);
        parallelTransition.getChildren().addAll(fadeTransition, scaleTransition);
        return parallelTransition;
    }
    private static Transition buildHideTransition(Node root, Node content){
        double duration = 250D;
        ParallelTransition parallelTransition = new ParallelTransition();
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), root);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), content);
        scaleTransition.setFromX(1);
        scaleTransition.setToX(0);
        scaleTransition.setFromY(1);
        scaleTransition.setToY(0);
        parallelTransition.getChildren().addAll(fadeTransition, scaleTransition);
        return parallelTransition;
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
        Transition transition = buildHideTransition(stage.getScene().getRoot(), node);
        transition.setOnFinished(actionEvent -> stage.close());
        transition.play();
    }

}
