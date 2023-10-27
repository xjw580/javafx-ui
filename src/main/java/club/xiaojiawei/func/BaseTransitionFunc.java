package club.xiaojiawei.func;

import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 10:05
 */
@FunctionalInterface
public interface BaseTransitionFunc {
    void play(Node node, double from, double to, Duration duration, int cycleCount, boolean autoReverse);
}
