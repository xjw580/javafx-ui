package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.TimeIco;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import static club.xiaojiawei.controls.TimeSelector.TIME_FORMATTER;
import static club.xiaojiawei.controls.TimeSelector.TIME_FULL_FORMATTER;

/**
 * 时间选择器-完整版
 * @author 肖嘉威
 * @date 2023/7/3 12:21
 */
@SuppressWarnings("unused")
public class Time extends AbstractTimeField<LocalTime> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 时间
     */
    private ObjectProperty<LocalTime> time;
    /**
     * 默认显示时间选择器图标
     */
    @Getter
    private boolean showIcon = true;
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
    /**
     * 文本框是否有焦点
     */
    private final BooleanProperty focusedField = new SimpleBooleanProperty();

    public String getTime() {
        if (showSec){
            return getLocalTime() == null? "" : TIME_FULL_FORMATTER.format(getLocalTime());
        }else {
            return getLocalTime() == null? "" : TIME_FORMATTER.format(getLocalTime());
        }
    }

    public LocalTime getLocalTime(){
        return time.get();
    }

    /**
     * @param time 格式：HH:mm，showSec=true时HH:mm:ss
     */
    public void setTime(String time) {
        if (time == null || time.isBlank()){
            this.setLocalTime(null);
        }else {
            setLocalTime(LocalTime.from(TIME_FORMATTER.parse(time)));
        }
    }

    public void setLocalTime(LocalTime localTime){
        if (test(localTime)){
            time.set(localTime);
        }
    }

    public ObjectProperty<LocalTime> timeProperty(){
        return timeSelector.timeProperty();
    }

    public void setShowIcon(boolean showIcon) {
        timeIco.setVisible(this.showIcon = showIcon);
        timeIco.setManaged(this.showIcon);
        if (showIcon){
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
            timeBG.getStyleClass().remove(TIME_BACKGROUND_STYLE_CLASS);
            timeBG.getStyleClass().remove(TIME_FULL_BACKGROUND_STYLE_CLASS);
            timeBG.getStyleClass().add(TIME_FULL_BACKGROUND_STYLE_CLASS);
            sec.setVisible(true);
            sec.setManaged(true);
            minWithSecSeparator.setVisible(true);
            minWithSecSeparator.setManaged(true);
            timeSelector.setShowSec(true);
        }else {
            timeBG.getStyleClass().remove(TIME_FULL_BACKGROUND_STYLE_CLASS);
            timeBG.getStyleClass().remove(TIME_BACKGROUND_STYLE_CLASS);
            timeBG.getStyleClass().add(TIME_BACKGROUND_STYLE_CLASS);
            sec.setVisible(false);
            sec.setManaged(false);
            minWithSecSeparator.setVisible(false);
            minWithSecSeparator.setManaged(false);
            timeSelector.setShowSec(false);
        }
        setShowIcon(this.showIcon);
    }

    public boolean isFocusedField() {
        return focusedField.get();
    }

    public void setFocusedField(boolean focusedField) {
        this.focusedField.set(focusedField);
    }

    public ReadOnlyBooleanProperty focusedReadOnlyProperty() {
        ReadOnlyBooleanWrapper booleanWrapper = new ReadOnlyBooleanWrapper();
        booleanWrapper.bind(focusedField);
        return booleanWrapper.getReadOnlyProperty();
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public Time() {}

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
    private static final String TIME_BACKGROUND_STYLE_CLASS = "timeBackground";
    private static final String TIME_FULL_BACKGROUND_STYLE_CLASS = "timeFullBackground";
    private static final String TIME_BACKGROUND_FOCUS_STYLE_CLASS = "timeBackgroundFocus";
    private static final String HIDE_ICO_TIME_BACKGROUND_STYLE_CLASS = "hideIcoTimeBackground";
    private static final String HIDE_ICO_TIME_FULL_BACKGROUND_STYLE_CLASS = "hideIcoTimeFullBackground";

    private boolean isFromTime;
    private TimeSelector timeSelector;

    @Override
    protected Popup createPopup() {
        Popup timeSelectorPopup = new Popup();
        timeSelector = new TimeSelector();
        time = timeSelector.timeProperty();
        time.addListener((observable, oldValue, newValue) -> updateCompleteTimeTextField(newValue));
        timeSelectorPopup.getContent().add(timeSelector);
        timeSelectorPopup.setAutoHide(true);
        return timeSelectorPopup;
    }

    @Override
    protected void loadPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void afterPageLoaded() {
        initTimeTextField(hour, MAX_HOUR);
        initTimeTextField(min, MAX_MIN);
        initTimeTextField(sec, MAX_SEC);
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
                setLocalTime(null);
            }else if (!isFromTime && newValue.length() == 2){
                if ((hour.getText() == null || hour.getText().isBlank()) && (min.getText() == null || min.getText().isBlank()) && (!showSec || sec.getText() == null || sec.getText().isBlank())){
                    setLocalTime(null);
                }else if (hour.getText() != null && !hour.getText().isBlank() && min.getText() != null && !min.getText().isBlank()){
                    if (showSec && sec.getText() != null && !sec.getText().isBlank()){
                        LocalTime localTime = LocalTime.from(TIME_FULL_FORMATTER.parse(hour.getText() + ":" + min.getText() + ":" + sec.getText()));
                        if (test(localTime)){
                            setLocalTime(localTime);
                        }else {
                            updateCompleteTimeTextField(getLocalTime());
                        }
                    }else {
                        LocalTime localTime = LocalTime.from(TIME_FORMATTER.parse(hour.getText() + ":" + min.getText()));
                        if (test(localTime)){
                            setLocalTime(localTime);
                        }else {
                            updateCompleteTimeTextField(getLocalTime());
                        }
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
            setFocusedField(hour.isFocused() || min.isFocused() || sec.isFocused());
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

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public Predicate<LocalTime> getInterceptor() {
        return this.timeSelector.getInterceptor();
    }

    @Override
    public ObjectProperty<Predicate<LocalTime>> interceptorProperty() {
        return this.timeSelector.interceptorProperty();
    }

    @Override
    public void setInterceptor(Predicate<LocalTime> timeInterceptor) {
        this.timeSelector.setInterceptor(timeInterceptor);
    }

    @Override
    public void refresh(){
        if (getLocalTime() == null){
            isFromTime = true;
            hour.setText("");
            min.setText("");
            isFromTime = false;
        }else {
            String[] times = TIME_FORMATTER.format(getLocalTime()).split(":");
            hour.setText(times[0]);
            min.setText(times[1]);
        }
    }

}