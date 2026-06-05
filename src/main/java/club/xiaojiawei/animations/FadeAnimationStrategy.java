package club.xiaojiawei.animations;

import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.animation.Transition;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * 淡入淡出动画策略
 * @author 肖嘉威 xjw580@qq.com
 * @date 2026/6/5 15:55
 */
public class FadeAnimationStrategy implements AnimationStrategy {

    private FadeAnimationStrategy() {
    }

    private static class Holder {
        private static final FadeAnimationStrategy INSTANCE = new FadeAnimationStrategy();
    }

    public static FadeAnimationStrategy instance() {
        return Holder.INSTANCE;
    }

    @Override
    public void playShowAnimation(Node node, double durationTime, Side side, Runnable onFinished) {
        Transition fade = BaseTransitionEnum.FADE.get(node, 0D, 1D, Duration.millis(durationTime));
        fade.setOnFinished(actionEvent -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fade.play();
    }

    @Override
    public void playHideAnimation(Node node, double durationTime, Side side, Runnable onFinished) {
        Transition fade = BaseTransitionEnum.FADE.get(node, 0D, Duration.millis(durationTime));
        fade.setOnFinished(actionEvent -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fade.play();
    }
}
