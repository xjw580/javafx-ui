package club.xiaojiawei.enums;

import club.xiaojiawei.func.BaseTransition;
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
        FadeTransition transition = new FadeTransition(duration, node);
        if (from != -1){
            transition.setFromValue(from);
        }
        transition.setToValue(to);
        commonBuild(transition, cycleCount, autoReverse);
        return transition;
    }),

    /**
     * 滑动
     */
    SLIDE_X((node, from, to, duration, cycleCount, autoReverse) -> {
        TranslateTransition transition = new TranslateTransition(duration, node);
        if (from != -1){
            transition.setFromX(from);
        }
        transition.setToX(to);
        commonBuild(transition, cycleCount, autoReverse);
        return transition;
    }),

    SLIDE_Y((node, from, to, duration, cycleCount, autoReverse) -> {
        TranslateTransition transition = new TranslateTransition(duration, node);
        if (from != -1){
            transition.setFromY(from);
        }
        transition.setToY(to);
        commonBuild(transition, cycleCount, autoReverse);
        return transition;
    }),

    SCALE((node, from, to, duration, cycleCount, autoReverse) -> {
        ScaleTransition transition = new ScaleTransition(duration, node);
        if (from != -1){
            transition.setFromX(from);
            transition.setFromY(from);
        }
        transition.setToX(to);
        transition.setToY(to);
        commonBuild(transition, cycleCount, autoReverse);
        return transition;
    }),

    /**
     * Y轴缩放
     */
    SCALE_Y((node, from, to, duration, cycleCount, autoReverse) -> {
        ScaleTransition transition = new ScaleTransition(duration, node);
        if (from != -1){
            transition.setFromY(from);
        }
        transition.setToY(to);
        commonBuild(transition, cycleCount, autoReverse);
        return transition;
    }),

    NONE((node, from, to, duration, cycleCount, autoReverse) -> null),
    ;

    private final BaseTransition transition;

    BaseTransitionEnum(BaseTransition transition) {
        this.transition = transition;
    }

    public void play(Node node, double from, double to, Duration duration, int cycleCount, boolean autoReverse){
        get(node, from, to, duration, cycleCount, autoReverse).play();
    }
    public void play(Node node, double to, Duration duration){
        get(node, -1, to, duration).play();
    }
    public void play(Node node, double from, double to, Duration duration){
        get(node, from, to, duration).play();
    }

    public Transition get(Node node, double to, Duration duration){
        return get(node, -1, to, duration, 0 ,false);
    }
    public Transition get(Node node, double from, double to, Duration duration){
        return get(node, from, to, duration, 0 ,false);
    }
    public Transition get(Node node, double from, double to, Duration duration, int cycleCount, boolean autoReverse){
        return this.transition.build(node, from, to, duration, cycleCount, autoReverse);
    }

    private static void commonBuild(Transition transition, int cycleCount, boolean autoReverse) {
        transition.setCycleCount(cycleCount);
        transition.setAutoReverse(autoReverse);
    }
}
