package club.xiaojiawei.animations;

import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.animation.Transition;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * 滑动动画策略（根据 Side 参数自动选择滑动方向）
 * @author 肖嘉威 xjw580@qq.com
 * @date 2026/6/5 16:10
 */
public class SlideAnimationStrategy implements AnimationStrategy {

    private SlideAnimationStrategy() {
    }

    private static class Holder {
        private static final SlideAnimationStrategy INSTANCE = new SlideAnimationStrategy();
    }

    public static SlideAnimationStrategy instance() {
        return Holder.INSTANCE;
    }

    private double getOffset(Side side) {
        if (side == Side.LEFT) {
            return -100D;
        }
        return 100D;
    }

    @Override
    public void playShowAnimation(Node node, double durationTime, Side side, Runnable onFinished) {
        double offset = getOffset(side);
        // 根据 side 从左侧或右侧偏移并淡入
        Transition slide = BaseTransitionEnum.SLIDE_X.get(node, offset, 0D, Duration.millis(durationTime));
        Transition fade = BaseTransitionEnum.FADE.get(node, 0D, 1D, Duration.millis(durationTime));
        slide.play();
        fade.setOnFinished(actionEvent -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fade.play();
    }

    @Override
    public void playHideAnimation(Node node, double durationTime, Side side, Runnable onFinished) {
        double offset = getOffset(side);
        // 根据 side 向左或向右滑动并淡出
        Transition slide = BaseTransitionEnum.SLIDE_X.get(node, offset, Duration.millis(durationTime));
        Transition fade = BaseTransitionEnum.FADE.get(node, 0D, Duration.millis(durationTime));
        slide.play();
        fade.setOnFinished(actionEvent -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fade.play();
    }
}

