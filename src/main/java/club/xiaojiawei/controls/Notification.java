package club.xiaojiawei.controls;

import club.xiaojiawei.enums.BaseTransitionEnum;
import club.xiaojiawei.enums.NotificationTypeEnum;
import club.xiaojiawei.enums.SizeEnum;
import javafx.animation.ParallelTransition;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Iterator;

/**
 * 通知
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 22:54
 */
@Slf4j
public class Notification extends Group {

    /**
     * 内容最大宽度，超过此宽度将换行
     */
    private final DoubleProperty contentMaxWidth;
    /**
     * 通知标题
     */
    private final StringProperty title;
    /**
     * 通知内容
     */
    private final StringProperty content;
    /**
     * 通知类型
     */
    @Getter
    private NotificationTypeEnum type = NotificationTypeEnum.INFO;
    /**
     * 是否显示通知
     */
    private final BooleanProperty showing = new SimpleBooleanProperty(false);
    /**
     * 是否显示关闭通知按钮
     */
    @Getter
    private boolean showingCloseBtn = true;
    /**
     * 动画时间
     */
    @Getter
    @Setter
    private double transitionTime = 200D;
    @Getter
    private SizeEnum size;

    public double getContentMaxWidth() {
        return contentMaxWidth.get();
    }
    public DoubleProperty contentMaxWidthProperty() {
        return contentMaxWidth;
    }
    public void setContentMaxWidth(double contentMaxWidth) {
        this.contentMaxWidth.set(contentMaxWidth);
    }

    public String getTitle() {
        return title.get();
    }
    public StringProperty titleProperty() {
        return title;
    }
    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getContent() {
        return content.get();
    }
    public StringProperty contentProperty() {
        return content;
    }
    public void setContent(String content) {
        this.content.set(content);
    }

    public void setType(NotificationTypeEnum type) {
        this.type = type;
        tipIcoPane.getChildren().clear();
        tipIcoPane.getChildren().add(type.getBuilder().get());
    }

    public boolean getShowing() {
        return showing.get();
    }
    public BooleanProperty showingProperty() {
        return showing;
    }
    public void setShowing(boolean showing) {
        this.showing.set(showing);
    }

    public void setShowingCloseBtn(boolean showingCloseBtn) {
        this.showingCloseBtn = showingCloseBtn;
        closeIcoPane.setManaged(showingCloseBtn);
        closeIcoPane.setVisible(showingCloseBtn);
    }

    public void setSize(SizeEnum size) {
        this.size = size;
        switch (size){
            case BIG -> {
                removeSizeStyleClass();
                notificationVBox.getStyleClass().add("notificationVBox-big");
            }
            case MEDDLE, DEFAULT -> removeSizeStyleClass();
            case SMALL -> {
                removeSizeStyleClass();
                notificationVBox.getStyleClass().add("notificationVBox-small");
            }
            case TINY -> {
                removeSizeStyleClass();
                notificationVBox.getStyleClass().add("notificationVBox-tiny");
            }
        }
    }
    private void removeSizeStyleClass(){
        notificationVBox.getStyleClass().removeIf(s -> s.startsWith("notificationVBox-"));
    }

    @FXML
    private Label titleLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private HBox bottomHBox;
    @FXML
    private StackPane closeIcoPane;
    @FXML
    private StackPane tipIcoPane;
    @FXML
    private VBox notificationVBox;

    private ParallelTransition parallelTransition;
    private Runnable transitionFinishedRunnable;
    private Runnable closeRunnable;

    public Notification() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            contentMaxWidth = bottomHBox.maxWidthProperty();
            title = titleLabel.textProperty();
            content = contentLabel.textProperty();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Notification(String title){
        this();
        setTitle(title);
    }
    public Notification(NotificationTypeEnum type, String title){
        this(title);
        setType(type);
    }
    public Notification(String title, String content){
        this(title);
        setContent(content);
    }
    public Notification(NotificationTypeEnum type, String title, String content){
        this(title, content);
        setType(type);
    }
    private void afterFXMLLoaded(){
        this.setVisible(false);
        this.setManaged(false);
        this.setOpacity(0D);
        addListener();
    }
    private void addListener(){
        showing.addListener((observableValue, aBoolean, t1) -> {
            parallelTransition = new ParallelTransition();

            if (t1){
                parallelTransition.getChildren().add(BaseTransitionEnum.FADE.get(this, 1D, Duration.millis(transitionTime / 2)));
                this.setVisible(true);
                this.setManaged(true);
                parallelTransition.setOnFinished(actionEvent -> {
                    if (transitionFinishedRunnable != null){
                        transitionFinishedRunnable.run();
                        transitionFinishedRunnable = null;
                    }
                });
            }else {
                parallelTransition.getChildren().add(BaseTransitionEnum.FADE.get(this, 0D, Duration.millis(transitionTime)));
                parallelTransition.setOnFinished(actionEvent -> {
                    this.setVisible(false);
                    this.setManaged(false);
                    if (transitionFinishedRunnable != null){
                        transitionFinishedRunnable.run();
                        transitionFinishedRunnable = null;
                    }
                    if (closeRunnable != null){
                        closeRunnable.run();
                        closeRunnable = null;
                    }
                });
            }
            parallelTransition.play();
        });
        closeIcoPane.setOnMouseClicked(mouseEvent -> {
            showing.set(false);
        });
        content.addListener((observableValue, s, t1) -> {
            if (t1 == null || t1.isBlank()){
                bottomHBox.setManaged(false);
                bottomHBox.setVisible(false);
            }else {
                bottomHBox.setManaged(true);
                bottomHBox.setVisible(true);
            }
        });
    }

    public void show(){
        showing.set(true);
    }
    public void hide(){
        showing.set(false);
    }
    public void hide(Runnable transitionFinishedRunnable){
        this.transitionFinishedRunnable = transitionFinishedRunnable;
        hide();
    }

    public void setOnCloseEvent(Runnable closeRunnable){
        this.closeRunnable = closeRunnable;
    }
}
