package club.xiaojiawei.controls;

import club.xiaojiawei.enums.NotificationTypeEnum;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 通知
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 22:54
 */
@Slf4j
public class Notification extends Group {

    private final DoubleProperty contentMaxWidth;
    private final StringProperty title;
    private final StringProperty content;
    @Getter
    private NotificationTypeEnum type = NotificationTypeEnum.INFO;
    private final BooleanProperty isShowAll = new SimpleBooleanProperty(false);
    private final BooleanProperty isShowCloseBtn = new SimpleBooleanProperty(true);
    @Getter
    @Setter
    private double transitionTime = 200D;

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
        Pane pane = type.getBuilder().get();
        pane.setScaleY(1.6);
        pane.setScaleX(1.6);
        tipIcoPane.getChildren().add(pane);
    }

    public boolean isIsShowAll() {
        return isShowAll.get();
    }
    public BooleanProperty isShowAllProperty() {
        return isShowAll;
    }
    public void setIsShowAll(boolean isShowAll) {
        this.isShowAll.set(isShowAll);
    }

    public boolean isIsShowCloseBtn() {
        return isShowCloseBtn.get();
    }
    public BooleanProperty isShowCloseBtnProperty() {
        return isShowCloseBtn;
    }
    public void setIsShowCloseBtn(boolean isShowCloseBtn) {
        this.isShowCloseBtn.set(isShowCloseBtn);
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
        isShowAll.addListener((observableValue, aBoolean, t1) -> {
            parallelTransition = new ParallelTransition();
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(transitionTime), this);
            parallelTransition.getChildren().add(fadeTransition);
            if (t1){
                this.setVisible(true);
                this.setManaged(true);
                fadeTransition.setToValue(1D);
                parallelTransition.setOnFinished(actionEvent -> {
                    if (transitionFinishedRunnable != null){
                        transitionFinishedRunnable.run();
                        transitionFinishedRunnable = null;
                    }
                });
            }else {
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
                fadeTransition.setToValue(0D);
            }
            parallelTransition.play();
        });
        closeIcoPane.setOnMouseClicked(mouseEvent -> {
            isShowAll.set(false);
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
        isShowCloseBtn.addListener((observableValue, aBoolean, t1) -> {
            closeIcoPane.setManaged(t1);
            closeIcoPane.setVisible(t1);
        });
    }

    public void show(){
        isShowAll.set(true);
    }
    public void hide(){
        isShowAll.set(false);
    }
    public void hide(Runnable transitionFinishedRunnable){
        this.transitionFinishedRunnable = transitionFinishedRunnable;
        hide();
    }

    public void setOnCloseEvent(Runnable closeRunnable){
        this.closeRunnable = closeRunnable;
    }
}
