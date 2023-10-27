package club.xiaojiawei.enums;

import club.xiaojiawei.func.BaseTransitionFunc;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author 肖嘉威
 * @date 2023/7/3 15:51
 * @msg 动画效果
 */
public enum BaseTransitionEnum {

    /**
     * 淡入淡出
     */
    FADE((node, from, to, duration, cycleCount, autoReverse) -> {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(from);
        fade.setToValue(to);
        setAndPlayTransition(fade, cycleCount, autoReverse);
    }),
    /**
     * 滑动
     */
    SLIDE_X((node, from, to, duration, cycleCount, autoReverse) -> {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromX(from);
        slide.setToX(to);
        setAndPlayTransition(slide, cycleCount, autoReverse);
    }),
    SLIDE_Y((node, from, to, duration, cycleCount, autoReverse) -> {
        TranslateTransition slide = new TranslateTransition(duration, node);
        slide.setFromY(from);
        slide.setToY(to);
        setAndPlayTransition(slide, cycleCount, autoReverse);
    }),
    /**
     * Y轴缩放
     */
    SCALE_Y((node, from, to, duration, cycleCount, autoReverse) -> {
        ScaleTransition scaleY = new ScaleTransition(duration, node);
        scaleY.setFromY(from);
        scaleY.setToY(to);
        setAndPlayTransition(scaleY, cycleCount, autoReverse);
    }),
    NONE((node, from, to, duration, cycleCount, autoReverse) -> {
    }),
    ;
    private final BaseTransitionFunc transition;

    BaseTransitionEnum(BaseTransitionFunc transition) {
        this.transition = transition;
    }
    public void play(Node node, double from, double to, Duration duration, int cycleCount, boolean autoReverse){
        this.transition.play(node, from, to, duration, cycleCount, autoReverse);
    }
    public void play(Node node, double from, double to, Duration duration){
        play(node, from, to, duration, 0, false);
    }
    private static void setAndPlayTransition(Transition transition, int cycleCount, boolean autoReverse) {
        transition.setCycleCount(cycleCount);
        transition.setAutoReverse(autoReverse);
        transition.play();
    }
}
