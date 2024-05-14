package club.xiaojiawei.func;

import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 10:05
 */
@FunctionalInterface
public interface BaseTransition {
    Transition build(Node node, double from, double to, Duration duration, int cycleCount, boolean autoReverse);
}
