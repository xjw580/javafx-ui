package club.xiaojiawei.utils;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;

import java.util.function.BiFunction;

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

    public static void slide(double start, double end, ScrollPane scrollPane, long intervalSleep){
        double step = (end - start) / 100;
        if (step == 0D){
            return;
        }
        BiFunction<Double, Double, Boolean> useFunc = end > start? (v1, v2) -> v1 <= v2 : (v1, v2) -> v1 >= v2;
        for (; useFunc.apply(start, end); start += step){
            try {
                Thread.sleep(intervalSleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            double finalStart = start;
            Platform.runLater(() -> scrollPane.setVvalue(finalStart));
        }
        Platform.runLater(() -> scrollPane.setVvalue(end));
    }
    public static void slide(double endIndex, double size, double showSize, ScrollPane scrollPane, long intervalSleep){
        double end = endIndex / (size - showSize);
        double start = scrollPane.getVvalue();
        slide(start, end, scrollPane, intervalSleep);
    }
}
