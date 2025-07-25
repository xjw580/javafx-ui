package club.xiaojiawei.controls;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.config.JavaFXUIThreadPoolConfig;
import club.xiaojiawei.enums.BaseTransitionEnum;
import club.xiaojiawei.func.MarkLogging;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 模态框
 *
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/31 10:38
 */
public class Modal implements MarkLogging {
    private static final Logger log = LoggerFactory.getLogger(Modal.class);

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 点击遮罩是否关闭Modal
     */
    @Getter
    @Setter
    private boolean maskClosable;

    /**
     * 按下esc键是否关闭Modal
     */
    @Getter
    @Setter
    private boolean escClosable = true;

    private final ReadOnlyBooleanProperty showing;

    private final Stage stage;

    private Pane rootPane;

    private Pane topPane;

    private Parent parent;

    private Node content;

    private Runnable cancelRunnable;

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        stage.setAlwaysOnTop(alwaysOnTop);
    }

    public boolean isAlwaysOnTop() {
        return stage.isAlwaysOnTop();
    }

    public boolean isShowing() {
        return showing.get();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    {
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        showing = stage.showingProperty();
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * 构建自定义模态框
     *
     * @param baseParent
     * @param content
     */
    public Modal(Parent baseParent, Node content) {
        if (baseParent == null) {
            throw new NullPointerException("parent不能为null");
        }
        buildRootPane(content);
        init(baseParent);
    }

    /**
     * 构建确认模态框
     *
     * @param baseParent
     * @param heading
     * @param content
     * @param btnHandler
     */
    public Modal(Parent baseParent, String heading, Object content, Runnable... btnHandler) {
        if (baseParent == null) {
            throw new NullPointerException("parent不能为null");
        }
        VBox vBox = buildConfirmBody(baseParent, heading, content);
        vBox.getChildren().add(createBtnGroup(btnHandler));
        buildRootPane(vBox);
        init(baseParent);
    }

    /**
     * 构建确认模态框
     *
     * @param baseParent
     * @param heading
     * @param content
     * @param btn
     */
    public Modal(Parent baseParent, String heading, Object content, Button... btn) {
        if (baseParent == null) {
            throw new NullPointerException("parent不能为null");
        }
        VBox vBox = buildConfirmBody(baseParent, heading, content);
        if (btn != null) {
            addBtnClickLogListen(btn);
            HBox hBox = buildBtnHBox();
            hBox.getChildren().addAll(Arrays.stream(btn).toList());
            vBox.getChildren().add(hBox);
        }
        buildRootPane(vBox);
        init(baseParent);
    }

    /**
     * 构建自定义确认模态框
     *
     * @param baseParent
     * @param heading
     * @param content
     * @param btn
     */
    public Modal(Parent baseParent, Node heading, Node content, Button... btn) {
        if (baseParent == null) {
            throw new NullPointerException("parent不能为null");
        }
        VBox vBox = new VBox(heading, content);
        if (btn != null) {
            addBtnClickLogListen(btn);
            HBox hBox = buildBtnHBox();
            hBox.getChildren().addAll(Arrays.stream(btn).toList());
            vBox.getChildren().add(hBox);
        }
        buildRootPane(vBox);
        init(baseParent);
    }

    /**
     * 构建自定义确认模态框
     *
     * @param baseParent
     * @param heading
     * @param content
     * @param style      The inline CSS style to use for this {@code Node}.
     *                   {@code null} is implicitly converted to an empty String.
     * @param btn
     */
    public Modal(Parent baseParent, Node heading, Node content, String style, Button... btn) {
        if (baseParent == null) {
            throw new NullPointerException("parent不能为null");
        }
        VBox vBox = new VBox(heading, content);
        vBox.setStyle(style);
        if (btn != null) {
            HBox hBox = buildBtnHBox();
            hBox.getChildren().addAll(Arrays.stream(btn).toList());
            vBox.getChildren().add(hBox);
        }
        buildRootPane(vBox);
        init(baseParent);
    }

    private String headingStr;

    private Object contentObj;

    public static final String CN_NAME = "弹窗";

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void addBtnClickLogListen(Button... btn) {
        if (JavaFXUI.getLogMark() != null) {
            for (Button button : btn) {
                button.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    recordBtnClickLog(button.getText());
                });
            }
        }
    }

    private void recordBtnClickLog(String text) {
        if (JavaFXUI.getLogMark() != null) {
            log.info(JavaFXUI.getLogMark(), "{}:点击了【{}】", CN_NAME, text);
        }
    }

    private HBox buildBtnHBox() {
        HBox hBox = new HBox();
        hBox.setStyle("-fx-alignment: CENTER_RIGHT;-fx-spacing: 15;-fx-padding: 0 15 15 0");
        return hBox;
    }

    private VBox buildConfirmBody(Parent baseParent, String heading, Object content) {
        VBox vBox = new VBox();
        vBox.setPrefWidth(Math.min(350, baseParent.getScene().getWidth() - 10));
        vBox.setMaxHeight(baseParent.getScene().getHeight() - 10);
        int headHeight = 0, btnHeight = 29, inset = 20;
        if (vBox.getMaxHeight() < 140) {
            inset = 15;
            vBox.setStyle("-fx-padding: 15;-fx-spacing: 15;");
        } else {
            vBox.setStyle("-fx-padding: 20;-fx-spacing: 20;");
        }
        headingStr = heading;
        contentObj = content;
        if (heading != null && !heading.isBlank()) {
            vBox.getChildren().add(createHeading(heading));
            headHeight = 14;
        }
        double maxWidth = vBox.getPrefWidth() - 40;
        double maxHeight = vBox.getMaxHeight() - btnHeight - headHeight - inset * 3;
        if (content instanceof String contentStr) {
            if (!contentStr.isBlank()) {
                vBox.getChildren().add(createContent(contentStr, maxWidth, maxHeight));
            }
        } else if (content instanceof Node node) {
            vBox.getChildren().add(node);
        }
        return vBox;
    }

    private void init(Parent baseParent) {
        this.parent = baseParent;
        this.stage.initOwner(baseParent.getScene().getWindow());
//        this.stage.initModality(Modality.APPLICATION_MODAL);
        initScene();
//        initSize();
//        addSizeListener();
        addClosingListener();
    }

    private void addClosingListener() {
        this.stage.getScene().addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (escClosable && keyEvent.getCode() == KeyCode.ESCAPE) {
                close();
                if (cancelRunnable != null) {
                    cancelRunnable.run();
                }
            }
        });
        this.stage.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (maskClosable && event.getTarget() == topPane) {
                close();
                if (cancelRunnable != null) {
                    cancelRunnable.run();
                }
                event.consume();
            }
        });
    }

    private void initScene() {
        Scene scene = new Scene(rootPane);
        scene.setFill(Color.TRANSPARENT);
        JavaFXUI.addjavafxUIStylesheet(scene);
        this.stage.setScene(scene);
    }

    private void addSizeListener() {
        this.parent.getScene().widthProperty().addListener((observableValue, number, t1) -> stage.setWidth(t1.doubleValue()));
        this.parent.getScene().heightProperty().addListener((observableValue, number, t1) -> stage.setHeight(t1.doubleValue()));
        this.parent.getScene().getWindow().yProperty().addListener((observableValue, number, t1) -> {
            if (this.parent.getScene() != null) {
                stage.setY(t1.doubleValue() + this.parent.getScene().getY());
            }
        });
        this.parent.getScene().getWindow().xProperty().addListener((observableValue, number, t1) -> {
            if (this.parent.getScene() != null) {
                stage.setX(t1.doubleValue() + this.parent.getScene().getX());
            }
        });
    }

    private void initSize() {
        Scene scene = parent.getScene();
        double width = scene.getWidth();
        double height = scene.getHeight();
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setY(scene.getWindow().getY() + scene.getY());
        stage.setX(scene.getWindow().getX() + scene.getX());
    }

    private void buildRootPane(Node content) {
        topPane = new StackPane(new Group(new StackPane(content) {{
            setStyle("-fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 0, 3);-fx-background-color: white;");
        }}));
        ScrollPane scrollPane = new ScrollPane(topPane) {{
            setFitToWidth(true);
            setFitToHeight(true);
            setStyle("""
                        -fx-background: transparent;
                        -fx-background-color: transparent;
                        -fx-background-insets: 0;
                        -fx-padding: 0;
                    """);
        }};
        rootPane = new StackPane(this.content = scrollPane);
        rootPane.setStyle("-fx-background-color: #00000011");
        rootPane.getStyleClass().add("fixed-label-color");
    }

    private Node createHeading(String heading) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(heading);
        label.setStyle("-fx-font-weight: bold;-fx-font-size: 14;-fx-wrap-text: true;-fx-text-fill: black");
        hBox.getChildren().add(label);
        return hBox;
    }

    private Node createContent(String content, double maxWidth, double maxHeight) {
        ScrollPane scrollPane = createContentPane(maxWidth, maxHeight);
        Text text = new Text(content);
        text.setWrappingWidth(maxWidth - 15);
        text.setStyle("-fx-font-size: 14;");
        scrollPane.setContent(text);
        return scrollPane;
    }

    private ScrollPane createContentPane(double maxWidth, double maxHeight) {
        ScrollPane scrollPane = new ScrollPane();
//        edge-to-edge样式类：去除默认的灰色边框和获得焦点时的蓝色边框
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background: white;-fx-hbar-policy: NEVER;-fx-padding: 0 0 0 5");
        scrollPane.setMaxWidth(maxWidth);
        if (maxHeight < 25) {
            scrollPane.setMaxHeight(maxHeight);
            scrollPane.setMinHeight(maxHeight);
            scrollPane.setPrefHeight(maxHeight);
        } else {
            scrollPane.setMaxHeight(Math.min(maxHeight, 200));
        }
        return scrollPane;
    }

    private Node createBtnGroup(Runnable... btnHandler) {
        HBox hBox = new HBox();
        Button ok = new Button("确认");
        ok.setOnAction(actionEvent -> {
            close();
            recordBtnClickLog(ok.getText());
            if (btnHandler != null && btnHandler.length > 0 && btnHandler[0] != null) {
                btnHandler[0].run();
            }
        });
        ok.getStyleClass().addAll("btn-ui", "btn-ui-success");
        hBox.getChildren().add(ok);
        if (btnHandler != null && btnHandler.length > 1) {
            this.cancelRunnable = btnHandler[1];
            Button cancel = new Button("取消");
            cancel.setOnAction(actionEvent -> {
                close();
                recordBtnClickLog(cancel.getText());
                if (btnHandler[1] != null) {
                    btnHandler[1].run();
                }
            });
            cancel.getStyleClass().addAll("btn-ui");
            hBox.getChildren().add(cancel);
        }
        hBox.setStyle("-fx-spacing: 15;-fx-alignment: CENTER_RIGHT");
        return hBox;
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * 关闭Modal
     */
    public void close() {
        close(null);
    }

    public void close(Runnable closedRunnable) {
        Duration duration = Duration.millis(150);
        ParallelTransition parallelTransition = new ParallelTransition(
                BaseTransitionEnum.FADE.get(this.rootPane, 1, 0, duration),
                BaseTransitionEnum.SLIDE_Y.get(this.content, 0, -25, duration)
        );
        parallelTransition.setOnFinished(actionEvent -> {
            stage.close();
            if (closedRunnable != null) {
                JavaFXUIThreadPoolConfig.SCHEDULED_POOL.submit(closedRunnable);
            }
        });
        parallelTransition.play();
    }

    /**
     * 显示Modal
     */
    public void show() {
        show(null);
    }

    public Window getWindow() {
        return stage;
    }

    public void show(Runnable shownRunnable) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(shownRunnable));
            return;
        }
        initSize();
        if (parent != null && parent.getScene().getWindow().isShowing()) {
            stage.show();
            if (JavaFXUI.getLogMark() != null) {
                if (contentObj instanceof String || (headingStr != null && !headingStr.isBlank())) {
                    log.info(JavaFXUI.getLogMark(), "{}:{ heading: {}, content: {} }", CN_NAME, headingStr, contentObj);
                }
            }
            Duration duration = Duration.millis(200);
            ParallelTransition parallelTransition = new ParallelTransition(
                    BaseTransitionEnum.FADE.get(this.rootPane, 0, 1, duration),
                    BaseTransitionEnum.SLIDE_Y.get(this.content, 25, 0, duration)
            );
            if (shownRunnable != null) {
                parallelTransition.setOnFinished(event -> {
                    JavaFXUIThreadPoolConfig.SCHEDULED_POOL.submit(shownRunnable);
                });
            }
            parallelTransition.play();
        }
    }

}
