package club.xiaojiawei.animations;

import javafx.geometry.Side;
import javafx.scene.Node;

/**
 * 通用的动画策略接口
 * @author 肖嘉威 xjw580@qq.com
 * @date 2026/6/5 15:55
 */
public interface AnimationStrategy {

    /**
     * 播放显示动画
     * @param node 要播放动画的节点
     * @param durationTime 动画持续时间（毫秒）
     * @param side 动画起始/参考方向（可为 null）
     * @param onFinished 动画结束后的回调
     */
    void playShowAnimation(Node node, double durationTime, Side side, Runnable onFinished);

    /**
     * 播放隐藏动画
     * @param node 要播放动画的节点
     * @param durationTime 动画持续时间（毫秒）
     * @param side 动画结束/参考方向（可为 null）
     * @param onFinished 动画结束后的回调
     */
    void playHideAnimation(Node node, double durationTime, Side side, Runnable onFinished);
}
