package club.xiaojiawei.func;

import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * 默认的淡入淡出动画策略
 * @author 肖嘉威 xjw580@qq.com
 * @date 2026/6/5 15:55
 */
public class FadeAnimationStrategy implements NotificationAnimationStrategy {

    @Override
    public void playShowAnimation(Node node, double durationTime, Runnable onFinished) {
        Transition fade = BaseTransitionEnum.FADE.get(node, 1D, Duration.millis(durationTime / 2));
        fade.setOnFinished(actionEvent -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fade.play();
    }

    @Override
    public void playHideAnimation(Node node, double durationTime, Runnable onFinished) {
        Transition fade = BaseTransitionEnum.FADE.get(node, 0D, Duration.millis(durationTime));
        fade.setOnFinished(actionEvent -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fade.play();
    }
}
