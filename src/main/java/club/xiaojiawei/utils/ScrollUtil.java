package club.xiaojiawei.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/25 13:32
 */
public class ScrollUtil {

    /**
     * @param deltaY 小于0往下滑，大于0往上滑
     * @return
     */
    public static ScrollEvent ofNewEvent(double deltaY){
        return new ScrollEvent(ScrollEvent.SCROLL, 0D, 0D, 0D, 0D, false, false, false, false, false, false, 0D, deltaY, 0D, 0D, null, 0D, null, 0D, 0, null);
    }

    /**
     * 创建时间线动画
     * @param start 滚条起始位置
     * @param end 滚条结束位置
     * @param scrollPane 滚动面板
     * @return
     */
    public static Timeline buildSlideTimeLine(double start, double end, ScrollPane scrollPane){
        return new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(scrollPane.vvalueProperty(), start)),
                new KeyFrame(Duration.millis(((Region) scrollPane.getContent()).getHeight() * Math.abs(end - start) * 0.8), new KeyValue(scrollPane.vvalueProperty(), end))
        );
    }

    /**
     * 创建时间线动画(只适用于ScrollPane的content是VBox，且VBox中垂直节点高度相同)
     * @param endIndex VBox中的结束节点下标
     * @param totalSize VBox总垂直节点
     * @param showSize VBox在ScrollPane中可见的垂直节点数
     * @param scrollPane 滚动面板
     * @return
     */
    public static Timeline buildSlideTimeLine(double endIndex, double totalSize, double showSize, ScrollPane scrollPane){
        return buildSlideTimeLine(scrollPane.getVvalue(), endIndex / (totalSize - showSize), scrollPane);
    }
}
