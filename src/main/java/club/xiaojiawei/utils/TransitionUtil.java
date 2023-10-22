package club.xiaojiawei.utils;

import club.xiaojiawei.enums.TransitionTypeEnum;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * @author 肖嘉威
 * @date 2023/7/3 12:24
 * @msg
 */
public class TransitionUtil {
    private static void setAndPlayTransition(Transition transition, int cycleCount, boolean autoReverse) {
        transition.setCycleCount(cycleCount);
        transition.setAutoReverse(autoReverse);
        transition.play();
    }
    public static void playTransition(Node node, double from, double to, Duration duration, int cycleCount, boolean autoReverse, TransitionTypeEnum transitionTypeEnum) {
        switch (transitionTypeEnum) {
            case SLIDE -> playTranslate(node, from, to, duration, cycleCount, autoReverse);
            case FADE -> playFadeTransition(node, from, to, duration, cycleCount, autoReverse);
        }

    }
    public static void playTransition(Node node, double from, double to, Duration duration, TransitionTypeEnum transitionTypeEnum) {
        switch (transitionTypeEnum) {
            case SLIDE -> playTranslate(node, from, to, duration);
            case FADE -> playFadeTransition(node, from, to, duration);
        }

    }
    public static void playTransition(Node node, double from, double to, TransitionTypeEnum transitionTypeEnum, Duration duration) {
        switch (transitionTypeEnum) {
            case SLIDE -> playTranslate(node, from, to, duration);
            case FADE -> playFadeTransition(node, from, to, duration);
        }
    }

    public static void playTranslate(Node node, double from, double to, Duration duration, int cycleCount, boolean autoReverse) {
        TranslateTransition translate = new TranslateTransition(duration, node);
        translate.setFromX(from);
        translate.setToX(to);
        setAndPlayTransition(translate, cycleCount, autoReverse);
    }
    public static void playTranslate(Node node, double from, double to, Duration duration) {
        playTranslate(node, from, to, duration, 0, false);
    }


    public static void playFadeTransition(Node node, double from, double to, Duration duration, int cycleCount, boolean autoReverse) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(from);
        fade.setToValue(to);
        setAndPlayTransition(fade, cycleCount, autoReverse);
    }
    public static void playFadeTransition(Node node, double from, double to, Duration duration) {
        playFadeTransition(node, from, to, duration, 0, false);
    }


    public static void playScaleYTransition(Node node, double from, double to, Duration duration){
        playScaleYTransition(node,from, to, duration, 0, false);
    }
    public static void playScaleYTransition(Node node, double from, double to, Duration duration, int cycleCount, boolean autoReverse){
        ScaleTransition scaleTransition = new ScaleTransition(duration, node);
        scaleTransition.setFromY(from);
        scaleTransition.setToY(to);
        setAndPlayTransition(scaleTransition, cycleCount, autoReverse);
    }
}
