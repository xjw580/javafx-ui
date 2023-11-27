package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.DateIco;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static club.xiaojiawei.controls.DateSelector.DATE_FORMATTER;
import static club.xiaojiawei.controls.DateSelector.calcMaxDayForMonth;
import static club.xiaojiawei.enums.BaseTransitionEnum.FADE;

/**
 * 日期选择器-完整版
 * @author 肖嘉威 580@qq.com
 * @date 2023/10/23 14:38
 */
public class Date extends AnchorPane {
    /**
     * 默认当前日期
     */
    private ObjectProperty<LocalDate> date;
    /**
     * 默认显示时间选择器图标
     */
    private boolean showSelector = true;
    /**
     * 默认显示边框
     */
    private boolean showBG = true;
    public String getDate() {
        return date.get() == null? null : DATE_FORMATTER.format(date.get());
    }
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }
    /**
     * @param date 格式：yyyy/MM/dd
     */
    public void setDate(String date) {
        this.date.set(LocalDate.from(DATE_FORMATTER.parse(date)));
    }
    public void setLocalDate(LocalDate localDate){
        date.set(localDate);
    }
    public void setShowSelector(boolean showSelector) {
        dateIco.setVisible(this.showSelector = showSelector);
        dateIco.setManaged(false);
        if (showSelector){
            dateBG.getStyleClass().remove(HIDE_ICO_DATE_BACKGROUND);
        }else {
            dateBG.getStyleClass().add(HIDE_ICO_DATE_BACKGROUND);
        }
    }
    public boolean isShowSelector() {
        return showSelector;
    }
    public boolean isShowBG() {
        return showBG;
    }
    public void setShowBG(boolean showBG) {
        dateBG.setVisible(this.showBG = showBG);
    }
    @FXML
    private Label dateBG;
    @FXML
    private TextField year;
    @FXML
    private TextField month;
    @FXML
    private TextField day;
    @FXML
    private DateIco dateIco;
    private static final int MAX_YEAR = 9999;
    private static final int MAX_MONTH = 12;
    private static final int MAX_DAY = 31;
    private Popup dateSelectorPopup;
    public Popup getDateSelectorPopup() {
        return dateSelectorPopup;
    }
    private static final String HIDE_ICO_DATE_BACKGROUND = "hideIcoDateBackground";
    private static final String DATE_BACKGROUND_FOCUS = "dateBackgroundFocus";
    private final static DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private final static DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM");
    private final static DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd");
    private boolean isFromDate;
    private ChangeListener<Boolean> focusChangeListener;
    public Date() {
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
        initDateSelectorPopup();
        initDateTextField(year, MAX_YEAR, 4);
        initDateTextField(month, MAX_MONTH, 2);
        initDateTextField(day, MAX_DAY, 2);
        initDateIco();
    }

    /**
     * 初始化时间选择器弹窗
     */
    private void initDateSelectorPopup(){
        dateSelectorPopup = new Popup();
        Calendar calendar = new Calendar();
        date = calendar.dateProperty();
        date.addListener((observable, oldValue, newValue) -> updateCompleteDateTextField(newValue));
        dateSelectorPopup.getContent().add(calendar);
        dateSelectorPopup.setAutoHide(true);
    }

    private void updateCompleteDateTextField(LocalDate newDate){
        isFromDate = true;
        if (newDate == null){
            year.setText("");
            month.setText("");
            day.setText("");
        }else {
            year.setText(YEAR_FORMATTER.format(newDate));
            month.setText(MONTH_FORMATTER.format(newDate));
            day.setText(DAY_FORMATTER.format(newDate));
        }
        isFromDate = false;
    }

    /**
     * 初始化日期文本框
     * @param textField
     * @param maxValue
     * @param maxLength
     */
    private void initDateTextField(TextField textField, int maxValue, int maxLength){
        textField.setTextFormatter(interceptInput(textField, maxValue, maxLength));
        textField.focusedProperty().addListener(dateTextFieldFocusListener(textField));
//        方向键按太快会因为动画问题而报错
        textField.setOnKeyPressed(keyPressedEventHandler(textField, maxValue));
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            int yearInt = parseInt(year.getText()), monthInt = parseInt(month.getText()), dayInt = Math.min(calcMaxDayForMonth(yearInt, monthInt), parseInt(day.getText()));
//            year、month、day三项全空时，日期设置为null
            if (yearInt == 0 && monthInt == 0 && dayInt == 0){
                date.set(null);
            } else if (!isFromDate && newValue.length() == maxLength){
                if (yearInt != 0 && monthInt != 0 && dayInt != 0){
                    date.set(LocalDate.of(yearInt, monthInt, dayInt));
                }
            }
        });
    }

    /**
     * 初始化日期图标
     */
    private void initDateIco(){
        dateIco.setOnMouseClicked(e -> {
            Bounds bounds = dateIco.localToScreen(dateIco.getBoundsInLocal());
            dateSelectorPopup.setAnchorX(bounds.getMaxX() - 100);
            dateSelectorPopup.setAnchorY(bounds.getMaxY() - 5);
            dateSelectorPopup.show(this.getScene().getWindow());
            FADE.play(dateSelectorPopup.getContent().get(0), 0.5D, 1D, Duration.millis(200));
        });
    }
    /**
     * 日期文本框焦点监听器
     * @param textField
     * @return
     */
    private ChangeListener<Boolean> dateTextFieldFocusListener(TextField textField) {
        return (observableValue, aBoolean, isFocus) -> {
            if (isFocus){
                dateBG.getStyleClass().add(DATE_BACKGROUND_FOCUS);
            }else {
                textField.setText(standardizationDateText(textField, textField.getText()));
                dateBG.getStyleClass().remove(DATE_BACKGROUND_FOCUS);
            }
            if (focusChangeListener != null){
                focusChangeListener.changed(observableValue, aBoolean, isFocus);
            }
        };
    }

    /**
     * 标准化日期
     * @param textField
     * @param time
     */
    private String standardizationDateText(TextField textField, String time){
        String timeStr = "";
        if (time != null && !time.isBlank()) {
            timeStr = "0".repeat((textField == year ? 4 : 2) - time.length()) + time;
            if (timeStr.matches("0+")){
                if (textField == year){
                    timeStr = YEAR_FORMATTER.format(LocalDate.now());
                }else if (textField == month){
                    timeStr = MONTH_FORMATTER.format(LocalDate.now());
                }else if (textField == day){
                    timeStr = DAY_FORMATTER.format(LocalDate.now());
                }
            }
        }
        return timeStr;
    }

    /**
     * 拦截输入
     * @param textField
     * @param maxValue
     * @return
     */
    private TextFormatter<TextFormatter.Change> interceptInput(TextField textField, int maxValue, int maxLength) {
        return new TextFormatter<>(change -> {
            String temp;
            if (change.getText().matches("^\\d{0," + maxLength + "}")
                    && (temp = textField.getText().substring(0, change.getRangeStart()) + change.getText() + textField.getText().substring(change.getRangeEnd())).length() <= maxLength
                    && (parseInt(temp) <= maxValue || (textField == day && parseInt(temp) <= calcMaxDayForMonth(parseInt(year.getText()), parseInt(month.getText()))))
            ){
                return change;
            }
            return null;
        });
    }

    /**
     * 按键监听处理器
     * @param textField
     * @return
     */
    private EventHandler<? super KeyEvent> keyPressedEventHandler(TextField textField, int maxValue){
        return (EventHandler<KeyEvent>) event -> {
            int newValue, tempMaxValue = maxValue;
            if (textField == day){
                tempMaxValue = calcMaxDayForMonth(parseInt(year.getText()), parseInt(month.getText()));
            }
            switch (event.getCode()){
                case UP -> newValue = parseInt(textField.getText()) + 1;
                case DOWN -> newValue = parseInt(textField.getText()) - 1 + tempMaxValue;
                default -> {
                    return;
                }
            }
            textField.setText(standardizationDateText(textField, String.valueOf((newValue - 1) % tempMaxValue + 1)));
        };
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
