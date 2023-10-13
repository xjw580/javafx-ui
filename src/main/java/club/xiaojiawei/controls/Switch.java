package club.xiaojiawei.controls;

import club.xiaojiawei.enums.TransitionTypeEnum;
import club.xiaojiawei.utils.TransitionUtil;
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
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static club.xiaojiawei.enums.TransitionTypeEnum.NONE;

/**
 * @author 肖嘉威
 * @date 2023/7/3 12:21
 * @msg 开关组件
 */
@Getter
@SuppressWarnings("unused")
public class Switch extends AnchorPane implements Initializable {
//    默认动画时间为200ms
    private Duration transitionDuration = Duration.valueOf("200ms");
//    默认动画效果为淡入淡出
    private TransitionTypeEnum transitionType = TransitionTypeEnum.FADE;
//    默认开关状态为开
    private final BooleanProperty initStatus = new SimpleBooleanProperty(true);
    private double size = 22D;
    @FXML
    private Circle switchCircle;
    @FXML
    private Rectangle switchRectangle;
    @FXML
    private Rectangle switchClipRectangle;
    @FXML
    private Rectangle bgRectangle;

    public void setTransitionDuration(Duration transitionDuration) {
        if (transitionType == NONE){
            this.transitionDuration = Duration.valueOf("1ms");
        }else {
            this.transitionDuration = transitionDuration;
        }
    }

    public void setTranslationType(TransitionTypeEnum transitionTypeEnum) {
        this.transitionType = transitionTypeEnum;
        if (transitionType == NONE){
            this.transitionDuration = Duration.valueOf("1ms");
        }
    }

    public boolean getInitStatus() {
        return this.initStatus.get();
    }
    public BooleanProperty initStatusProperty() {
        return this.initStatus;
    }
    public void setInitStatus(boolean initStatus) {
        if (initStatus != this.initStatus.get()){
            onMouseClicked(null);
        }
    }

    public void setSize(double size) {
        this.size = size;
        bgRectangle.setWidth(size * 2);
        bgRectangle.setHeight(size);
        bgRectangle.setArcHeight(size);
        bgRectangle.setArcWidth(size);

        switchRectangle.setWidth(size * 2);
        switchRectangle.setHeight(size);
        switchRectangle.setArcHeight(size);
        switchRectangle.setArcWidth(size);

        switchClipRectangle.setWidth(size * 2);
        switchClipRectangle.setHeight(size);

        switchCircle.setTranslateX(size);
        switchCircle.setLayoutX(size / 2);
        switchCircle.setLayoutY(size / 2);
        switchCircle.setRadius(size / 4);
    }

    public Switch() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("switch.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            AnchorPane ap = fxmlLoader.load();
            ap.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void onMouseClicked(MouseEvent event) {
        switch (transitionType) {
            case FADE -> fadeTranslation();
            case SLIDE -> translateTranslation();
            default -> noneTranslation();
        }
        circleTranslate();
        initStatus.set(!initStatus.get());
        if (event != null){
            event.consume();
        }
    }

    private void circleTranslate() {
        double translateFrom = 0.0D, translateTo = size;
        if (initStatus.get()) {
            final double temp = translateFrom;
            translateFrom = translateTo;
            translateTo = temp;
        }
        TransitionUtil.playTranslate(switchCircle, translateFrom, translateTo, transitionDuration);
    }
    private void fadeTranslation() {
        final double fadeFrom = 0.0D, fadeTo = 1.0D;
        if (initStatus.get()) {
            TransitionUtil.playFadeTransition(switchRectangle, fadeTo, fadeFrom, transitionDuration);
        } else {
            TransitionUtil.playFadeTransition(switchRectangle, fadeFrom, fadeTo, transitionDuration);
        }
    }
    private void translateTranslation() {
        switchRectangle.setOpacity(1.0D);
        final double clipTranslateFrom = size * 2, clipTranslateTo = 0.0D;
        if (initStatus.get()) {
            TransitionUtil.playTranslate(switchClipRectangle, clipTranslateTo, clipTranslateFrom, transitionDuration);
        } else {
            TransitionUtil.playTranslate(switchClipRectangle, clipTranslateFrom, clipTranslateTo, transitionDuration);
        }
    }
    private void noneTranslation() {
        if (initStatus.get()) {
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