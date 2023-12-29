package club.xiaojiawei.controls;

import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static club.xiaojiawei.enums.BaseTransitionEnum.*;

/**
 * 开关组件
 * @author 肖嘉威
 * @date 2023/7/3 12:21
 */
@SuppressWarnings("unused")
public class Switch extends AnchorPane implements Initializable {
    /**
     * 默认动画时间为200ms
     */
    private Duration transitionDuration = Duration.valueOf("200ms");
    /**
     * 默认动画效果为淡入淡出
     */
    private BaseTransitionEnum transitionType = BaseTransitionEnum.FADE;
    /**
     * 默认开关状态为开
     */
    private final BooleanProperty status = new SimpleBooleanProperty(true);
    /**
     * 默认开关尺寸为20
     */
    private double size = 20D;

    public void setTransitionDuration(Duration transitionDuration) {
//        动画效果为NONE时动画持续时间自然为0
        if (transitionType == NONE){
            this.transitionDuration = Duration.valueOf("1ms");
        }else {
            this.transitionDuration = transitionDuration;
        }
    }

    public void setTranslationType(BaseTransitionEnum baseTransitionEnum) {
        this.transitionType = baseTransitionEnum;
        if (transitionType == NONE){
            this.transitionDuration = Duration.valueOf("1ms");
        }
    }
    public Duration getTransitionDuration() {
        return transitionDuration;
    }

    public BaseTransitionEnum getTransitionType() {
        return transitionType;
    }

    public boolean getStatus() {
        return status.get();
    }

    public double getSize() {
        return size;
    }

    public boolean getInitStatus() {
        return this.status.get();
    }
    public BooleanProperty statusProperty() {
        return this.status;
    }
    public void setStatus(boolean status) {
        if (status != this.status.get()){
            onMouseClicked(null);
        }
    }

    public void setSize(double size) {
        this.size = size;
//        药丸形
        bgRectangle.setWidth(size * 2);
        bgRectangle.setHeight(size);
        bgRectangle.setArcHeight(size);
        bgRectangle.setArcWidth(size);
//        药丸形
        switchRectangle.setWidth(size * 2);
        switchRectangle.setHeight(size);
        switchRectangle.setArcHeight(size);
        switchRectangle.setArcWidth(size);
//        长方形
        switchClipRectangle.setWidth(size * 2);
        switchClipRectangle.setHeight(size);
//        圆形
        switchCircle.setTranslateX(size);
        switchCircle.setLayoutX(size / 2);
        switchCircle.setLayoutY(size / 2);
        switchCircle.setRadius(size / 4);
    }
    @FXML
    private Circle switchCircle;
    @FXML
    private Rectangle switchRectangle;
    @FXML
    private Rectangle switchClipRectangle;
    @FXML
    private Rectangle bgRectangle;
    public Switch() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.setOnMouseClicked(this::onMouseClicked);
    }

    /**
     * 开关被点击后触发事件
     * @param event
     */
    protected void onMouseClicked(MouseEvent event) {
        switch (transitionType) {
            case FADE -> fadeTranslation();
            case SLIDE_X -> slideTranslation();
            default -> noneTranslation();
        }
        circleTranslate();
        status.set(!status.get());
        if (event != null){
            event.consume();
        }
    }

    /**
     * 开关中的圆平移
     */
    private void circleTranslate() {
        double translateFrom = 0.0D, translateTo = size;
        if (status.get()) {
            final double temp = translateFrom;
            translateFrom = translateTo;
            translateTo = temp;
        }
        SLIDE_X.play(switchCircle, translateFrom, translateTo, transitionDuration);
    }

    /**
     * 开关中的背景动画：渐变
     */
    private void fadeTranslation() {
        final double fadeFrom = 0.0D, fadeTo = 1.0D;
        if (status.get()) {
            FADE.play(switchRectangle, fadeTo, fadeFrom, transitionDuration);
        } else {
            FADE.play(switchRectangle, fadeFrom, fadeTo, transitionDuration);
        }
    }
    /**
     * 开关中的背景动画：平移
     */
    private void slideTranslation() {
        switchRectangle.setOpacity(1.0D);
        final double clipTranslateFrom = size * 2, clipTranslateTo = 0.0D;
        if (status.get()) {
            SLIDE_X.play(switchClipRectangle, clipTranslateTo, clipTranslateFrom, transitionDuration);
        } else {
            SLIDE_X.play(switchClipRectangle, clipTranslateFrom, clipTranslateTo, transitionDuration);
        }
    }

    /**
     * 开关中的背景动画：缩放
     */
    private void scaleTranslation(){

    }
    /**
     * 开关中的背景动画：无
     */
    private void noneTranslation() {
        if (status.get()) {
            switchRectangle.setOpacity(0.0D);
        } else {
            switchRectangle.setOpacity(1.0D);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setSize(size);
    }
}