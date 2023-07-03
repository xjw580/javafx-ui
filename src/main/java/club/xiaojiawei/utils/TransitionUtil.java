package club.xiaojiawei.utils;

import club.xiaojiawei.enums.TransitionType;
import javafx.animation.FadeTransition;
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
    public static double MODERATION_DURATION = 200.0D;
    public static double FAST_DURATION = 50.0D;
    public static double SLOW_DURATION = 600.0D;

    public static void playTransition(Node node, double from, double to, double duration, int cycleCount, boolean autoReverse, TransitionType transitionType) {
        switch (transitionType) {
            case SLIDE -> playTranslate(node, from, to, duration, cycleCount, autoReverse);
            case FADE -> playFadeTransition(node, from, to, duration, cycleCount, autoReverse);
        }

    }
    public static void playTransition(Node node, double from, double to, double duration, TransitionType transitionType) {
        switch (transitionType) {
            case SLIDE -> playTranslate(node, from, to, duration);
            case FADE -> playFadeTransition(node, from, to, duration);
        }

    }
    public static void playTransition(Node node, double from, double to, TransitionType transitionType) {
        switch (transitionType) {
            case SLIDE -> playTranslate(node, from, to);
            case FADE -> playFadeTransition(node, from, to);
        }
    }

    public static void playTranslate(Node node, double from, double to, double duration, int cycleCount, boolean autoReverse) {
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(node);
        translate.setFromX(from);
        translate.setToX(to);
        translate.setDuration(Duration.millis(duration));
        setAndPlayTransition(translate, cycleCount, autoReverse);
    }
    public static void playTranslate(Node node, double from, double to, double duration) {
        playTranslate(node, from, to, duration, 0, false);
    }
    public static void playTranslate(Node node, double from, double to) {
        playTranslate(node, from, to, MODERATION_DURATION, 0, false);
    }

    public static void playFadeTransition(Node node, double from, double to, double duration, int cycleCount, boolean autoReverse) {
        FadeTransition fade = new FadeTransition();
        fade.setNode(node);
        fade.setFromValue(from);
        fade.setToValue(to);
        fade.setDuration(Duration.millis(duration));
        setAndPlayTransition(fade, cycleCount, autoReverse);
    }
    public static void playFadeTransition(Node node, double from, double to, double duration) {
        playFadeTransition(node, from, to, duration, 0, false);
    }
    public static void playFadeTransition(Node node, double from, double to) {
        playFadeTransition(node, from, to, MODERATION_DURATION, 0, false);
    }

    private static void setAndPlayTransition(Transition transition, int cycleCount, boolean autoReverse) {
        transition.setCycleCount(cycleCount);
        transition.setAutoReverse(autoReverse);
        transition.play();
    }
}
