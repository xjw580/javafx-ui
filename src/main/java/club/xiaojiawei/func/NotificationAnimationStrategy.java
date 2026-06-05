package club.xiaojiawei.func;

import javafx.scene.Node;

/**
 * 默认的淡入淡出动画策略
 * @author 肖嘉威 xjw580@qq.com
 * @date 2026/6/5 15:55
 */
public interface NotificationAnimationStrategy {

    /**
     * 播放显示动画
     * @param node 要播放动画的节点
     * @param durationTime 动画持续时间（毫秒）
     * @param onFinished 动画结束后的回调
     */
    void playShowAnimation(Node node, double durationTime, Runnable onFinished);

    /**
     * 播放隐藏动画
     * @param node 要播放动画的节点
     * @param durationTime 动画持续时间（毫秒）
     * @param onFinished 动画结束后的回调
     */
    void playHideAnimation(Node node, double durationTime, Runnable onFinished);
}
