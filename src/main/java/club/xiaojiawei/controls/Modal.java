package club.xiaojiawei.controls;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.animation.ParallelTransition;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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

    /**
     * 点击遮罩是否关闭
     */
    @Getter
    @Setter
    private boolean maskClosable;

    private final ReadOnlyBooleanProperty showing;

    private final Stage stage;

    private Pane rootPane;

    private Parent parent;

    private Node content;

    private Runnable cancelRunnable;

    public boolean isShowing() {
        return showing.get();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    {
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        showing = stage.showingProperty();
    }

    /**
     * 根据传入的content构建模态框
     * @param parent
     * @param content
     */
    public Modal(Parent parent, Node content) {
        if (parent == null){
            throw new NullPointerException("parent不能为null");
        }
        buildRootPane(content);
        init(parent);
    }

    /**
     * 构建确认模态框
     * @param parent
     * @param heading
     * @param content
     * @param btnHandler
     */
    public Modal(Parent parent, String heading, String content, Runnable... btnHandler) {
        if (parent == null){
            throw new NullPointerException("parent不能为null");
        }
        VBox vBox = new VBox(){{
            setStyle("-fx-padding: 20;-fx-spacing: 20;-fx-pref-width: 350");
        }};
        if (heading != null && !heading.isBlank()){
            vBox.getChildren().add(createHeading(heading));
        }
        if (content != null && !content.isBlank()){
            vBox.getChildren().add(createContent(content));
        }
        vBox.getChildren().add(createBtnGroup(btnHandler));
        buildRootPane(vBox);
        init(parent);
    }

    private void init(Parent parent){
        this.parent = parent;
        this.stage.initOwner(parent.getScene().getWindow());
        initScene();
        initSize();
        addSizeListener();
        addClosingListener();
    }

    private void addClosingListener(){
        this.stage.getScene().addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE){
                close();
                if (cancelRunnable != null){
                    cancelRunnable.run();
                }
            }
        });
        this.stage.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getTarget() instanceof StackPane stackPane && stackPane.getStyleClass().contains("root") && maskClosable){
                close();
                if (cancelRunnable != null){
                    cancelRunnable.run();
                }
                event.consume();
            }
        });
    }

    private void initScene(){
        Scene scene = new Scene(rootPane);
        scene.setFill(Color.TRANSPARENT);
        JavaFXUI.addjavafxUIStylesheet(scene);
        this.stage.setScene(scene);
    }

    private void addSizeListener(){
        this.parent.getScene().widthProperty().addListener((observableValue, number, t1) -> {
            stage.setWidth(t1.doubleValue());
        });
        this.parent.getScene().heightProperty().addListener((observableValue, number, t1) -> {
            stage.setHeight(t1.doubleValue());
        });
        this.parent.getScene().getWindow().yProperty().addListener((observableValue, number, t1) -> {
            stage.setY(t1.doubleValue() + this.parent.getScene().getY());
        });
        this.parent.getScene().getWindow().xProperty().addListener((observableValue, number, t1) -> {
            stage.setX(t1.doubleValue() + this.parent.getScene().getX());
        });
    }

    private void initSize(){
        double width = parent.getScene().getWidth();
        double height = parent.getScene().getHeight();
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setY(parent.getScene().getWindow().getY() + parent.getScene().getY());
        stage.setX(parent.getScene().getWindow().getX() + parent.getScene().getX());
    }

    public void buildRootPane(Node content){
        rootPane = new StackPane(this.content = new Group(new StackPane(content) {{
            setStyle("-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 0, 3);-fx-background-color: white;");
        }}));
        rootPane.setStyle("-fx-background-color: #00000011");
    }

    private Node createHeading(String heading){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(heading);
        label.setStyle("-fx-font-weight: bold;-fx-font-size: 12;-fx-wrap-text: true");
        hBox.getChildren().add(label);
        return hBox;
    }

    private Node createContent(String content){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(content);
        label.setStyle("-fx-font-size: 14;-fx-padding: 0 0 0 10;-fx-wrap-text: true");
        hBox.getChildren().add(label);
        return hBox;
    }

    private Node createBtnGroup(Runnable... btnHandler){
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
            this.cancelRunnable = btnHandler[1];
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
        Duration duration = Duration.millis(150);
        ParallelTransition parallelTransition = new ParallelTransition(
                BaseTransitionEnum.FADE.get(this.rootPane, 1, 0, duration),
                BaseTransitionEnum.SLIDE_Y.get(this.content, 0, -25, duration)
        );
        parallelTransition.play();
        parallelTransition.setOnFinished(actionEvent -> {
            stage.close();
        });
    }

    public void show(){
        stage.show();
        Duration duration = Duration.millis(200);
        ParallelTransition parallelTransition = new ParallelTransition(
                BaseTransitionEnum.FADE.get(this.rootPane, 0, 1, duration),
                BaseTransitionEnum.SLIDE_Y.get(this.content, 25, 0, duration)
        );
        parallelTransition.play();
    }

}
