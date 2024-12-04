package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.LoadingIco;
import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
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
public class Switch extends StackPane{

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 加载中
     */
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    /**
     * 默认动画时间为200ms
     */
    @Getter
    private Duration transitionDuration = Duration.valueOf("200ms");
    /**
     * 默认动画效果为淡入淡出
     */
    @Getter
    @Setter
    private BaseTransitionEnum transitionType = BaseTransitionEnum.FADE;
    /**
     * 默认开关状态为开
     */
    private final BooleanProperty status = new SimpleBooleanProperty(true);
    /**
     * 默认开关尺寸为20
     */
    @Getter
    private double size = 20D;

    public boolean isLoading() {
        return loading.get();
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading.set(loading);
    }

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

    public boolean getStatus() {
        return status.get();
    }

    public boolean getInitStatus() {
        return this.status.get();
    }
    public BooleanProperty statusProperty() {
        return this.status;
    }
    public void setStatus(boolean status) {
        if (status != this.status.get()){
            noneTranslation();
            circleTranslate(false);
            this.status.set(status);
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
        switchCircle.setTranslateX(size / 2);
//        switchCircle.setLayoutY(size / 2);
        switchCircle.setRadius(size / 4);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

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
        this.disabledProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                contentPane.setOpacity(0.4);
            }else {
                contentPane.setOpacity(1);
            }
        });
        loading.addListener((observableValue, aBoolean, t1) -> {
            loadingIco.setVisible(t1);
            setDisable(t1);
        });
    }

    @FXML
    private Circle switchCircle;
    @FXML
    private Rectangle switchRectangle;
    @FXML
    private Rectangle switchClipRectangle;
    @FXML
    private Rectangle bgRectangle;
    @FXML
    private LoadingIco loadingIco;
    @FXML
    private StackPane contentPane;

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * 开关被点击后触发事件
     * @param event
     */
    private void onMouseClicked(MouseEvent event) {
        switch (transitionType) {
            case FADE -> {
                fadeTranslation();
                circleTranslate(true);
            }
            case SLIDE_X -> {
                slideTranslation();
                circleTranslate(true);
            }
            default -> {
                noneTranslation();
                circleTranslate(false);
            }
        }
        status.set(!status.get());
        if (event != null){
            event.consume();
        }
    }

    /**
     * 开关中的圆平移
     */
    private void circleTranslate(boolean playTransition) {
        double translateFrom = - size / 2, translateTo = -translateFrom;
        if (status.get()) {
            final double temp = translateFrom;
            translateFrom = translateTo;
            translateTo = temp;
        }
        if (playTransition){
            SLIDE_X.play(switchCircle, translateFrom, translateTo, transitionDuration);
        }else {
            switchCircle.setTranslateX(translateTo);
        }
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
        final double clipTranslateFrom = size * 2, clipTranslateTo = 0;
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

    @FXML void initialize() {
        setSize(size);
    }

}