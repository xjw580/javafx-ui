package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.TimeIco;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.NamedArg;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import lombok.Getter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.function.Predicate;

import static club.xiaojiawei.controls.TimeSelector.TIME_FORMATTER;

/**
 * 时间选择器-完整版
 *
 * @author 肖嘉威
 * @date 2023/7/3 12:21
 */
@SuppressWarnings("unused")
public class DurationTime extends AbstractDateTimeField<Duration> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 时间
     */
    protected ReadOnlyObjectWrapper<Duration> time;
    /**
     * 显示秒
     */
    @Getter
    private boolean showSec;
    /**
     * 直角背景
     */
    private final BooleanProperty rightAngleBackground = new SimpleBooleanProperty(true);

    /**
     * @return HH:mm 或 HH:mm:ss
     */
    public String getTime() {
        if (showSec) {
            return getDuration() == null ? "" : timeSelector.getTimeUtils().durationToSecString(getDuration());
        } else {
            return getDuration() == null ? "" : timeSelector.getTimeUtils().durationToString(getDuration());
        }
    }

    public Duration getDuration() {
        return time.get();
    }

    /**
     * @param time 格式：HH:mm，showSec=true时HH:mm:ss
     */
    public void setTime(String time) {
        if (time == null || time.isBlank()) {
            this.setDuration(null);
        } else {
            try {
                setDuration(showSec ? (timeSelector.getTimeUtils().secStringToDuration(time)) : (timeSelector.getTimeUtils().stringToDuration(time)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setDuration(Duration duration) {
        if (test(duration)) {
            time.set(duration);
        }
    }

    public ObjectProperty<Duration> timeProperty() {
        return timeSelector.timeProperty();
    }

    public ReadOnlyObjectProperty<Duration> readOnlyTimeProperty() {
        return time.getReadOnlyProperty();
    }

    @Override
    public void setShowIcon(boolean showIcon) {
        super.setShowIcon(showIcon);
        timeIco.setVisible(showIcon);
        timeIco.setManaged(showIcon);
        if (showIcon) {
            dateTimeBg.getStyleClass().removeAll(HIDE_ICO_TIME_FULL_BACKGROUND_STYLE_CLASS, HIDE_ICO_TIME_BACKGROUND_STYLE_CLASS);
        } else {
            dateTimeBg.getStyleClass().removeAll(HIDE_ICO_TIME_FULL_BACKGROUND_STYLE_CLASS, HIDE_ICO_TIME_BACKGROUND_STYLE_CLASS);
            dateTimeBg.getStyleClass().add(showSec ? HIDE_ICO_TIME_FULL_BACKGROUND_STYLE_CLASS : HIDE_ICO_TIME_BACKGROUND_STYLE_CLASS);
        }
    }

    @Override
    public void setShowBg(boolean showBg) {
        super.setShowBg(showBg);
        dateTimeBg.setVisible(showBg);
    }

    public void setShowSec(boolean showSec) {
        this.showSec = showSec;
        if (showSec) {
            dateTimeBg.getStyleClass().remove(TIME_BACKGROUND_STYLE_CLASS);
            dateTimeBg.getStyleClass().remove(TIME_FULL_BACKGROUND_STYLE_CLASS);
            dateTimeBg.getStyleClass().add(TIME_FULL_BACKGROUND_STYLE_CLASS);
            sec.setVisible(true);
            sec.setManaged(true);
            minWithSecSeparator.setVisible(true);
            minWithSecSeparator.setManaged(true);
            timeSelector.setShowSec(true);
        } else {
            dateTimeBg.getStyleClass().remove(TIME_FULL_BACKGROUND_STYLE_CLASS);
            dateTimeBg.getStyleClass().remove(TIME_BACKGROUND_STYLE_CLASS);
            dateTimeBg.getStyleClass().add(TIME_BACKGROUND_STYLE_CLASS);
            sec.setVisible(false);
            sec.setManaged(false);
            minWithSecSeparator.setVisible(false);
            minWithSecSeparator.setManaged(false);
            timeSelector.setShowSec(false);
        }
        setShowIcon(isShowIcon());
    }

    public void setRightAngleBackground(boolean rightAngleBackground) {
        this.rightAngleBackground.set(rightAngleBackground);
        if (rightAngleBackground) {
            dateTimeBg.getStyleClass().add("rightAngleBackground");
        } else {
            dateTimeBg.getStyleClass().removeAll("rightAngleBackground");
        }
    }

    public boolean isRightAngleBackground() {
        return rightAngleBackground.get();
    }

    public BooleanProperty rightAngleBackgroundProperty() {
        return rightAngleBackground;
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public DurationTime(
            @NamedArg("maxHour") int maxHour, @NamedArg("maxMin") int maxMin, @NamedArg("maxSec") int maxSec,
            @NamedArg("minHour") int minHour, @NamedArg("minMin") int minMin, @NamedArg("minSec") int minSec
    ) {
        super(maxHour, maxMin, maxSec, minHour, minMin, minSec);
    }

    @FXML
    private TextField hour;
    @FXML
    private TextField min;
    @FXML
    private TextField sec;
    @FXML
    private TimeIco timeIco;
    @FXML
    private Label minWithSecSeparator;

    private static final String TIME_BACKGROUND_STYLE_CLASS = "timeBackground";
    private static final String TIME_FULL_BACKGROUND_STYLE_CLASS = "timeFullBackground";
    private static final String TIME_BACKGROUND_FOCUS_STYLE_CLASS = "timeBackgroundFocus";
    private static final String HIDE_ICO_TIME_BACKGROUND_STYLE_CLASS = "hideIcoTimeBackground";
    private static final String HIDE_ICO_TIME_FULL_BACKGROUND_STYLE_CLASS = "hideIcoTimeFullBackground";

    private boolean isFromTime;

    @Override
    protected Popup createPopup() {
        Popup timeSelectorPopup = new Popup();
        time = timeSelector.time;
        time.addListener((observable, oldValue, newValue) -> updateCompleteTimeTextField(newValue));
        timeSelectorPopup.getContent().add(timeSelector);
        timeSelectorPopup.setAutoHide(true);
        return timeSelectorPopup;
    }

    @Override
    protected void loadPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Time.class.getResource(Time.class.getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void afterPageLoaded() {
        initTimeTextField(hour, timeSelector.getTimeUtils().maxHour());
        initTimeTextField(min, timeSelector.getTimeUtils().maxMin());
        initTimeTextField(sec, timeSelector.getTimeUtils().maxSec());
        focusTraversableProperty().addListener((observableValue, aBoolean, t1) -> {
            hour.setFocusTraversable(t1);
            min.setFocusTraversable(t1);
            sec.setFocusTraversable(t1);
        });
    }

    @Override
    protected Node createIconNode() {
        return timeIco;
    }

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void updateCompleteTimeTextField(Duration newTime) {
        isFromTime = true;
        if (newTime == null) {
            hour.setText("");
            min.setText("");
            if (showSec) {
                sec.setText("");
            }
        } else {
            hour.setText(timeSelector.getTimeUtils().formatHour(newTime));
            min.setText(timeSelector.getTimeUtils().formatMinute(newTime));
            if (showSec) {
                sec.setText(timeSelector.getTimeUtils().formatSecond(newTime));
            }
        }
        isFromTime = false;
    }

    /**
     * 初始化时间文本框
     *
     * @param textField
     * @param maxValue
     */
    private void initTimeTextField(TextField textField, int maxValue) {
        textField.setTextFormatter(interceptInput(textField, maxValue));
        textField.focusedProperty().addListener(timeTextFieldBlurListener(textField));
        textField.setOnKeyPressed(keyPressedEventHandler(textField, maxValue));
        textField.setOnKeyReleased(keyRealsedEventHandler(textField));
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if ((hour.getText() == null || hour.getText().isBlank()) && (min.getText() == null || min.getText().isBlank()) && (!showSec || sec.getText() == null || sec.getText().isBlank())) {
                setDuration(null);
            } else if (!isFromTime && newValue.length() == 2) {
                if ((hour.getText() == null || hour.getText().isBlank()) && (min.getText() == null || min.getText().isBlank()) && (!showSec || sec.getText() == null || sec.getText().isBlank())) {
                    setDuration(null);
                } else if (hour.getText() != null && !hour.getText().isBlank() && min.getText() != null && !min.getText().isBlank()) {
                    if (showSec && sec.getText() != null && !sec.getText().isBlank()) {
                        var duration = timeSelector.getTimeUtils().secStringToDuration((hour.getText() + ":" + min.getText() + ":" + sec.getText()));
                        if (test(duration)) {
                            setDuration(duration);
                        } else {
                            updateCompleteTimeTextField(getDuration());
                        }
                    } else {
                        LocalTime localTime = LocalTime.from(TIME_FORMATTER.parse(hour.getText() + ":" + min.getText()));
                        var duration = timeSelector.getTimeUtils().stringToDuration((hour.getText() + ":" + min.getText()));
                        if (test(duration)) {
                            setDuration(duration);
                        } else {
                            updateCompleteTimeTextField(getDuration());
                        }
                    }
                }
            }
        });
    }

    /**
     * 拦截时间输入
     *
     * @param textField
     * @param maxValue
     * @return
     */
    private TextFormatter<TextFormatter.Change> interceptInput(TextField textField, int maxValue) {
        return new TextFormatter<>(change -> {
            String temp;
            if (change.getText().matches("^\\d{0,2}")
                && (temp = textField.getText().substring(0, change.getRangeStart()) + change.getText() + textField.getText().substring(change.getRangeEnd())).length() <= 2
                && parseInt(temp) <= maxValue
            ) {
                return change;
            }
            return null;
        });
    }

    private boolean ctrlDown = false;

    /**
     * 按键处理器-通过上下键改变时间
     *
     * @param textField
     * @return
     */
    private EventHandler<? super KeyEvent> keyPressedEventHandler(TextField textField, int maxValue) {
        return (EventHandler<KeyEvent>) event -> {
            int newValue;
            switch (event.getCode()) {
                case UP -> newValue = parseInt(textField.getText()) + 1;
                case DOWN -> newValue = parseInt(textField.getText()) - 1 + (maxValue + 1);
                case CONTROL -> {
                    ctrlDown = true;
                    return;
                }
                case C -> {
                    if (ctrlDown) {
                        SystemUtil.copyTextToClipboard(getTime());
                    }
                    return;
                }
                case V -> {
                    if (ctrlDown) {
                        String clipboardText = SystemUtil.getClipboardText();
                        setTime(clipboardText);
                    }
                    return;
                }
                default -> {
                    return;
                }
            }
            standardizationTime(textField, String.valueOf(newValue % (maxValue + 1)));
        };
    }

    private EventHandler<? super KeyEvent> keyRealsedEventHandler(TextField textField) {
        return (EventHandler<KeyEvent>) event -> {
            switch (event.getCode()) {
                case CONTROL -> ctrlDown = false;
                default -> {
                }
            }
        };
    }

    /**
     * 失焦监听器-失焦后标准化时间
     *
     * @param textField
     * @return
     */
    private ChangeListener<Boolean> timeTextFieldBlurListener(TextField textField) {
        return (observableValue, aBoolean, isFocus) -> {
            if (!isFocus) {
                standardizationTime(textField, textField.getText());
                dateTimeBg.getStyleClass().remove(TIME_BACKGROUND_FOCUS_STYLE_CLASS);
                timeIco.setColor("main-shallow-color");
            } else {
                dateTimeBg.getStyleClass().add(TIME_BACKGROUND_FOCUS_STYLE_CLASS);
                timeIco.setColor("main-color");
            }
            setFocusedField(hour.isFocused() || min.isFocused() || sec.isFocused());
        };
    }

    /**
     * 标准化时间
     *
     * @param textField
     * @param time
     */
    private void standardizationTime(TextField textField, String time) {
        if (time != null && !time.isBlank()) {
            textField.setText("0".repeat(2 - time.length()) + time);
        }
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * 日期拦截器
     *
     * @return
     */
    @Override
    public Predicate<Duration> getInterceptor() {
        return this.timeSelector.getInterceptor();
    }

    @Override
    public ObjectProperty<Predicate<Duration>> interceptorProperty() {
        return this.timeSelector.interceptorProperty();
    }

    @Override
    public void setInterceptor(Predicate<Duration> timeInterceptor) {
        this.timeSelector.setInterceptor(timeInterceptor);
    }

    @Override
    public void refresh() {
        if (getDuration() == null) {
            isFromTime = true;
            hour.setText("");
            min.setText("");
            isFromTime = false;
        } else {
            String[] times = timeSelector.getTimeUtils().durationToString(getDuration()).split(":");
            hour.setText(times[0]);
            min.setText(times[1]);
        }
    }

}