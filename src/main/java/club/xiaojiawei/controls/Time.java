package club.xiaojiawei.controls;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.girod.javafx.svgimage.SVGLoader;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import static club.xiaojiawei.enums.BaseTransitionEnum.FADE;

/**
 * 时间选择器-完整版
 * @author 肖嘉威
 * @date 2023/7/3 12:21
 */
@SuppressWarnings("unused")
public class Time extends AnchorPane {
    /**
     * 默认当前时间
     */
    private StringProperty time;
    /**
     * 默认显示时间选择器图标
     */
    private boolean showSelector = true;
    /**
     * 默认显示边框
     */
    private boolean showBorder = true;
    public String getTime() {
        return time.get();
    }
    public StringProperty timeProperty() {
        return time;
    }
    /**
     * @param time 格式：HH:mm
     */
    public void setTime(String time) {
        this.time.set(time);
    }
    public void setShowSelector(boolean showSelector) {
        timeIco.setVisible(this.showSelector = showSelector);
        if (showSelector){
            timeBG.getStyleClass().remove(HIDE_ICO_TIME_BACKGROUND);
        }else {
            timeBG.getStyleClass().add(HIDE_ICO_TIME_BACKGROUND);
        }
    }
    public boolean isShowSelector() {
        return showSelector;
    }
    public boolean isShowBorder() {
        return showBorder;
    }
    public void setShowBorder(boolean showBorder) {
        timeBG.setVisible(this.showBorder = showBorder);
    }
    @FXML
    private Label timeBG;
    @FXML
    private TextField hour;
    @FXML
    private TextField min;
    @FXML
    private AnchorPane timeIco;
    public AnchorPane getTimeIco() {
        return timeIco;
    }
    private static final int MAX_HOUR = 23;
    private static final int MAX_MIN = 59;
    private Popup timeSelectorPopup;
    public Popup getTimeSelectorPopup() {
        return timeSelectorPopup;
    }
    private static final String TIME_BACKGROUND_FOCUS = "timeBackgroundFocus";
    private static final String HIDE_ICO_TIME_BACKGROUND = "hideIcoTimeBackground";
    private boolean isFromTime;
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
        initTimeIco();
    }

    /**
     * 初始化时间选择器
     */
    private void initTimeSelectorPopup(){
        timeSelectorPopup = new Popup();
        TimeSelector timeSelector = new TimeSelector();
        time = timeSelector.timeProperty();
        TemporalAccessor initTime = TimeSelector.TIME_FORMATTER.parse(time.get());
        hour.setText(DateTimeFormatter.ofPattern("HH").format(initTime));
        min.setText(DateTimeFormatter.ofPattern("mm").format(initTime));
        time.addListener((observable, oldValue, newValue) -> updateCompleteTimeTextField(newValue));
        timeSelectorPopup.getContent().add(timeSelector);
        timeSelectorPopup.setAutoHide(true);
    }
    private void updateCompleteTimeTextField(String time){
        TemporalAccessor times = TimeSelector.TIME_FORMATTER.parse(time);
        isFromTime = true;
        hour.setText(DateTimeFormatter.ofPattern("HH").format(times));
        min.setText(DateTimeFormatter.ofPattern("mm").format(times));
        isFromTime = false;
    }

    private void initTimeIco(){
        timeIco.getChildren().add(SVGLoader.load(getClass().getResource("images/time.svg")));
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
            if (!isFromTime && newValue.length() == 2){
                time.setValue(hour.getText() + ":" + min.getText());
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
                timeBG.getStyleClass().remove(TIME_BACKGROUND_FOCUS);
            }else {
                timeBG.getStyleClass().add(TIME_BACKGROUND_FOCUS);
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
        textField.setText("0".repeat(2 - time.length()) + time);
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
}