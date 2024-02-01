package club.xiaojiawei.controls;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

/**
 * 模态框
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/31 10:38
 */
public class Modal {

    private Runnable cancelRunnable;

    /**
     * 点击遮罩是否关闭
     */
    @Getter
    @Setter
    private boolean maskClosable;


    public Modal(Parent parent) {
//        if (parent == null){
//            throw new NullPointerException("parent不能为null");
//        }
//        Stage stage = new Stage();
//
//        stage.initStyle(StageStyle.TRANSPARENT);
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.initOwner(parent.getScene().getWindow());
//        dialogPaneProperty().addListener((observableValue, dialogPane, t1) -> {
//            JavaFXUI.addjavafxUIStylesheet(this.getDialogPane().getScene());
//            double height = parent.getScene().getHeight();
//            double width = parent.getScene().getWidth();
//            t1.setPrefWidth(width);
//            t1.setPrefHeight(height);
//            t1.setStyle("-fx-background-color: #00000011;");
//            t1.getScene().addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
//                if (keyEvent.getCode() == KeyCode.ESCAPE){
//                    close();
//                    if (cancelRunnable != null){
//                        cancelRunnable.run();
//                    }
//                }
//            });
//            t1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//                if (event.getTarget() instanceof StackPane stackPane && stackPane.getStyleClass().contains("content")){
//                    if (maskClosable){
//                        close();
//                        if (cancelRunnable != null){
//                            cancelRunnable.run();
//                        }
//                        event.consume();
//                    }
//                }
//            });
//        });
//        showingProperty().addListener((observableValue, aBoolean, t1) -> {
//            if (t1){
//                getDialogPane().setOpacity(0);
//                this.setY(parent.getScene().getWindow().getY() + parent.getScene().getY());
//                this.setX(parent.getScene().getWindow().getX() + parent.getScene().getX());
//                this.getDialogPane().getScene().setFill(Color.TRANSPARENT);
//                int duration = 250;
//                BaseTransitionEnum.FADE.play(getDialogPane(), 0, 1, Duration.millis(duration));
//            }
//        });

    }

    public Node initRootPane(Node content){
        return new StackPane(new Group(new AnchorPane(content){{setStyle("-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 0, 3);-fx-background-color: white;");}}));
    }

    public void initDialog(String heading, String content, Runnable[] btnHandler){
        if (btnHandler != null && btnHandler.length > 1){
            cancelRunnable = btnHandler[1];
        }
        initRootPane(new StackPane(new VBox(createHeading(heading), createContent(content), createBtnGroup(btnHandler)){{setStyle("-fx-spacing: 15;-fx-alignment: CENTER");}}){{setStyle("-fx-pref-width: 300;-fx-padding: 15;");}});
    }

    private Node createHeading(String heading){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(heading);
        label.setStyle("-fx-font-weight: bold;-fx-font-size: 12");
        hBox.getChildren().add(label);
        return hBox;
    }

    private Node createContent(String content){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(content);
        label.setStyle("-fx-font-size: 14;-fx-padding: 0 0 0 10");
        hBox.getChildren().add(label);
        return hBox;
    }

    private Node createBtnGroup(Runnable[] btnHandler){
        HBox hBox = new HBox();
        Button ok = new Button("确认");
        ok.setOnAction(actionEvent -> {
            close();
            if (btnHandler != null && btnHandler.length > 0 && btnHandler[0] != null){
                btnHandler[0].run();
            }
        });
        ok.getStyleClass().addAll("btn-ui", "btn-ui-success");
        hBox.getChildren().add(ok);
        if (btnHandler != null && btnHandler.length > 1 && btnHandler[1] != null){
            Button cancel = new Button("取消");
            cancel.setOnAction(actionEvent -> {
                close();
                btnHandler[1].run();
            });
            cancel.getStyleClass().addAll("btn-ui");
            hBox.getChildren().add(cancel);
        }
        hBox.setStyle("-fx-spacing: 15;-fx-alignment: CENTER_RIGHT");
        return hBox;
    }

    public void close(){

    }

}
