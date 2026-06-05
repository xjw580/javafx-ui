package club.xiaojiawei.animations;

import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.animation.Transition;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * 缩放动画策略
 * @author 肖嘉威 xjw580@qq.com
 * @date 2026/6/5 16:00
 */
public class ScaleAnimationStrategy implements AnimationStrategy {

    private ScaleAnimationStrategy() {
    }

    private static class Holder {
        private static final ScaleAnimationStrategy INSTANCE = new ScaleAnimationStrategy();
    }

    public static ScaleAnimationStrategy instance() {
        return Holder.INSTANCE;
    }

    @Override
    public void playShowAnimation(Node node, double durationTime, Side side, Runnable onFinished) {
        Transition scale = BaseTransitionEnum.SCALE.get(node, 0D, 1D, Duration.millis(durationTime));
        Transition fade = BaseTransitionEnum.FADE.get(node, 0D, 1D, Duration.millis(durationTime));
        scale.play();
        fade.setOnFinished(actionEvent -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fade.play();
    }

    @Override
    public void playHideAnimation(Node node, double durationTime, Side side, Runnable onFinished) {
        Transition scale = BaseTransitionEnum.SCALE.get(node, 0D, Duration.millis(durationTime));
        Transition fade = BaseTransitionEnum.FADE.get(node, 0D, Duration.millis(durationTime));
        scale.play();
        fade.setOnFinished(actionEvent -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fade.play();
    }
}
