package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.TimeIco;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static club.xiaojiawei.controls.TimeSelector.TIME_FORMATTER;
import static club.xiaojiawei.controls.TimeSelector.TIME_FULL_FORMATTER;
import static club.xiaojiawei.enums.BaseTransitionEnum.FADE;

/**
 * 时间选择器-完整版
 * @author 肖嘉威
 * @date 2023/7/3 12:21
 */
@SuppressWarnings("unused")
public class Time extends StackPane {
    /**
     * 时间
     */
    private ObjectProperty<LocalTime> time;
    /**
     * 默认显示时间选择器图标
     */
    @Getter
    private boolean showSelector = true;
    /**
     * 默认显示背景
     */
    @Getter
    private boolean showBg = true;
    /**
     * 显示秒
     */
    @Getter
    private boolean showSec;
    public String getTime() {
        if (showSec){
            return TIME_FULL_FORMATTER.format(time.get());
        }else {
            return TIME_FORMATTER.format(time.get());
        }
    }
    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }
    /**
     * @param time 格式：HH:mm，showSec=true时HH:mm:ss
     */
    public void setTime(String time) {
        if (time == null || time.isBlank()){
            this.time.set(null);
        }else {
            this.time.set(LocalTime.from(TIME_FORMATTER.parse(time)));
        }
    }
    public void setLocalTime(LocalTime localTime){
        time.set(localTime);
    }
    public void setShowSelector(boolean showSelector) {
        timeIco.setVisible(this.showSelector = showSelector);
        timeIco.setManaged(this.showSelector);
        if (showSelector){
            timeBG.getStyleClass().removeAll(HIDE_ICO_TIME_FULL_BACKGROUND_STYLE_CLASS, HIDE_ICO_TIME_BACKGROUND_STYLE_CLASS);
        }else {
            timeBG.getStyleClass().removeAll(HIDE_ICO_TIME_FULL_BACKGROUND_STYLE_CLASS, HIDE_ICO_TIME_BACKGROUND_STYLE_CLASS);
            timeBG.getStyleClass().add(showSec? HIDE_ICO_TIME_FULL_BACKGROUND_STYLE_CLASS : HIDE_ICO_TIME_BACKGROUND_STYLE_CLASS);
        }
    }

    public void setShowBg(boolean showBg) {
        timeBG.setVisible(this.showBg = showBg);
    }

    public void setShowSec(boolean showSec) {
        this.showSec = showSec;
        if (showSec){
            timeBG.getStyleClass().remove(TIME_BACKGROUND);
            timeBG.getStyleClass().remove(TIME_FULL_BACKGROUND);
            timeBG.getStyleClass().add(TIME_FULL_BACKGROUND);
            sec.setVisible(true);
            sec.setManaged(true);
            minWithSecSeparator.setVisible(true);
            minWithSecSeparator.setManaged(true);
            timeSelector.setShowSec(true);
        }else {
            timeBG.getStyleClass().remove(TIME_FULL_BACKGROUND);
            timeBG.getStyleClass().remove(TIME_BACKGROUND);
            timeBG.getStyleClass().add(TIME_BACKGROUND);
            sec.setVisible(false);
            sec.setManaged(false);
            minWithSecSeparator.setVisible(false);
            minWithSecSeparator.setManaged(false);
            timeSelector.setShowSec(false);
        }
        setShowSelector(this.showSelector);
    }

    @FXML
    private Label timeBG;
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
    private static final int MAX_HOUR = 23;
    private static final int MAX_MIN = 59;
    private static final int MAX_SEC = 59;
    @Getter
    private Popup timeSelectorPopup;
    private static final String TIME_BACKGROUND = "timeBackground";
    private static final String TIME_FULL_BACKGROUND = "timeFullBackground";
    private static final String TIME_BACKGROUND_FOCUS_STYLE_CLASS = "timeBackgroundFocus";
    private static final String HIDE_ICO_TIME_BACKGROUND_STYLE_CLASS = "hideIcoTimeBackground";
    private static final String HIDE_ICO_TIME_FULL_BACKGROUND_STYLE_CLASS = "hideIcoTimeFullBackground";
    private boolean isFromTime;
    private TimeSelector timeSelector;
    private ChangeListener<Boolean> focusChangeListener;
    public Time() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void afterFXMLLoaded(){
        initTimeSelectorPopup();
        initTimeTextField(hour, MAX_HOUR);
        initTimeTextField(min, MAX_MIN);
        initTimeTextField(sec, MAX_SEC);
        initTimeIco();
        focusTraversableProperty().addListener((observableValue, aBoolean, t1) -> {
            hour.setFocusTraversable(t1);
            min.setFocusTraversable(t1);
            sec.setFocusTraversable(t1);
        });
    }

    /**
     * 初始化时间选择器
     */
    private void initTimeSelectorPopup(){
        timeSelectorPopup = new Popup();
        timeSelector = new TimeSelector();
        time = timeSelector.timeProperty();
        time.addListener((observable, oldValue, newValue) -> updateCompleteTimeTextField(newValue));
        timeSelectorPopup.getContent().add(timeSelector);
        timeSelectorPopup.setAutoHide(true);
    }
    private void updateCompleteTimeTextField(LocalTime newTime){
        isFromTime = true;
        if (newTime == null){
            hour.setText("");
            min.setText("");
            if (showSec){
                sec.setText("");
            }
        }else {
            hour.setText(DateTimeFormatter.ofPattern("HH").format(newTime));
            min.setText(DateTimeFormatter.ofPattern("mm").format(newTime));
            if (showSec){
                sec.setText(DateTimeFormatter.ofPattern("ss").format(newTime));
            }
        }
        isFromTime = false;
    }

    private void initTimeIco(){
        timeIco.setOnMouseClicked(e -> {
            Bounds bounds = timeIco.localToScreen(timeIco.getBoundsInLocal());
            timeSelectorPopup.setAnchorX(bounds.getMaxX() - 50);
            timeSelectorPopup.setAnchorY(bounds.getMaxY() - 5);
            timeSelectorPopup.show(this.getScene().getWindow());
            FADE.play(timeSelectorPopup.getContent().get(0), 0.5D, 1D, Duration.millis(200));
        });
    }


    /**
     * 初始化时间文本框
     * @param textField
     * @param maxValue
     */
    private void initTimeTextField(TextField textField, int maxValue){
        textField.setTextFormatter(interceptInput(textField, maxValue));
        textField.focusedProperty().addListener(timeTextFieldBlurListener(textField));
        textField.setOnKeyPressed(keyPressedEventHandler(textField, maxValue));
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if ((hour.getText() == null || hour.getText().isBlank()) && (min.getText() == null || min.getText().isBlank()) && (!showSec || sec.getText() == null || sec.getText().isBlank())){
                time.set(null);
            }else if (!isFromTime && newValue.length() == 2){
                if ((hour.getText() == null || hour.getText().isBlank()) && (min.getText() == null || min.getText().isBlank()) && (!showSec || sec.getText() == null || sec.getText().isBlank())){
                    time.set(null);
                }else if (hour.getText() != null && !hour.getText().isBlank() && min.getText() != null && !min.getText().isBlank()){
                    if (showSec && sec.getText() != null && !sec.getText().isBlank()){
                        time.set(LocalTime.from(TIME_FULL_FORMATTER.parse(hour.getText() + ":" + min.getText() + ":" + sec.getText())));
                    }else {
                        time.set(LocalTime.from(TIME_FORMATTER.parse(hour.getText() + ":" + min.getText())));
                    }
                }
            }
        });
    }

    /**
     * 拦截时间输入
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
            ){
                return change;
            }
            return null;
        });
    }

    /**
     * 按键处理器-通过上下键改变时间
     * @param textField
     * @return
     */
    private EventHandler<? super KeyEvent> keyPressedEventHandler(TextField textField, int maxValue){
        return (EventHandler<KeyEvent>) event -> {
            int newValue;
            switch (event.getCode()){
                case UP -> newValue = parseInt(textField.getText()) + 1;
                case DOWN -> newValue = parseInt(textField.getText()) - 1 + (maxValue + 1);
                default -> {
                    return;
                }
            }
            standardizationTime(textField, String.valueOf(newValue % (maxValue + 1)));
        };
    }
    /**
     * 失焦监听器-失焦后标准化时间
     * @param textField
     * @return
     */
    private ChangeListener<Boolean> timeTextFieldBlurListener(TextField textField) {
        return (observableValue, aBoolean, isFocus) -> {
            if (!isFocus){
                standardizationTime(textField, textField.getText());
                timeBG.getStyleClass().remove(TIME_BACKGROUND_FOCUS_STYLE_CLASS);
            }else {
                timeBG.getStyleClass().add(TIME_BACKGROUND_FOCUS_STYLE_CLASS);
            }
            if (focusChangeListener != null){
                focusChangeListener.changed(observableValue, aBoolean, isFocus);
            }
        };
    }

    /**
     * 标准化时间
     * @param textField
     * @param time
     */
    private void standardizationTime(TextField textField, String time){
        if (time != null && !time.isBlank()){
            textField.setText("0".repeat(2 - time.length()) + time);
        }
    }

    private int parseInt(String s){
        if (s == null || s.isBlank()){
            return 0;
        }
        return Integer.parseInt(s);
    }
    public void setOnFocusChangeListener(ChangeListener<Boolean> changeListener){
        focusChangeListener = changeListener;
    }

    /**
     * 刷新显示的时间，和真正存储的时间同步
     */
    public void refresh(){
        if (time.get() == null){
            isFromTime = true;
            hour.setText("");
            min.setText("");
            isFromTime = false;
        }else {
            String[] times = TIME_FORMATTER.format(time.get()).split(":");
            hour.setText(times[0]);
            min.setText(times[1]);
        }
    }
}