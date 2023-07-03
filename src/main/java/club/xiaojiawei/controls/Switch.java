package club.xiaojiawei.controls;

import club.xiaojiawei.enums.TransitionType;
import club.xiaojiawei.utils.TransitionUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

/**
 * @author 肖嘉威
 * @date 2023/7/3 12:21
 * @msg 开关组件
 */

public class Switch extends AnchorPane {
//    默认动画时间为中等
    private double showDuration = TransitionUtil.MODERATION_DURATION;
//    默认动画效果为淡入淡出
    private TransitionType transitionType = TransitionType.FADE;
//    默认开关状态为开
    private final BooleanProperty status = new SimpleBooleanProperty(true);
    @FXML
    private Circle switchCircle;
    @FXML
    private Rectangle switchRectangle;
    @FXML
    private Rectangle switchClipRectangle;

    public double getShowDuration() {
        return this.showDuration;
    }
    public void setShowDuration(double showDuration) {
        this.showDuration = showDuration;
    }

    public TransitionType getTranslationType() {
        return this.transitionType;
    }
    public void setTranslationType(TransitionType transitionType) {
        this.transitionType = transitionType;
    }

    public boolean isStatus() {
        return this.status.get();
    }

    public BooleanProperty statusProperty() {
        return this.status;
    }

    public void setStatus(boolean status) {
        if (status != this.status.get())
            onMouseClicked(null);
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
        status.set(!status.get());
        if (event != null){
            event.consume();
        }
    }

    private void circleTranslate() {
        double translateFrom = 0.0D, translateTo = 22.0D;
        if (status.get()) {
            final double temp = translateFrom;
            translateFrom = translateTo;
            translateTo = temp;
        }
        TransitionUtil.playTranslate(switchCircle, translateFrom, translateTo, showDuration);
    }
    private void fadeTranslation() {
        final double fadeFrom = 0.0D, fadeTo = 1.0D;
        if (status.get()) {
            TransitionUtil.playFadeTransition(switchRectangle, fadeTo, fadeFrom, showDuration);
        } else {
            TransitionUtil.playFadeTransition(switchRectangle, fadeFrom, fadeTo, showDuration);
        }
    }
    private void translateTranslation() {
        switchRectangle.setOpacity(1.0D);
        final double clipTranslateFrom = 44.0D, clipTranslateTo = 0.0D;
        if (status.get()) {
            TransitionUtil.playTranslate(switchClipRectangle, clipTranslateTo, clipTranslateFrom, showDuration);
        } else {
            TransitionUtil.playTranslate(switchClipRectangle, clipTranslateFrom, clipTranslateTo, showDuration);
        }
    }
    private void noneTranslation() {
        if (status.get()) {
            switchRectangle.setOpacity(0.0D);
        } else {
            switchRectangle.setOpacity(1.0D);
        }
    }
}