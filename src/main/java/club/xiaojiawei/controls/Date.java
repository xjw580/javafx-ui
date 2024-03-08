package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.DateIco;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

import static club.xiaojiawei.controls.DateSelector.DATE_FORMATTER;
import static club.xiaojiawei.controls.DateSelector.calcMaxDayForMonth;

/**
 * 日期选择器-完整版
 * @author 肖嘉威 580@qq.com
 * @date 2023/10/23 14:38
 */
public class Date extends AbstractTimeField<LocalDate> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 默认当前日期
     */
    private ObjectProperty<LocalDate> date;

    public String getDate() {
        return getLocalDate() == null? null : DATE_FORMATTER.format(getLocalDate());
    }

    public LocalDate getLocalDate(){
        return date.get();
    }

    /**
     * @param date 格式：yyyy/MM/dd
     */
    public void setDate(String date) {
        if (date == null || date.isBlank()){
            setLocalDate(null);
        }else {
            setLocalDate(LocalDate.from(DATE_FORMATTER.parse(date)));
        }
    }

    public void setLocalDate(LocalDate localDate){
        if (getInterceptor() == null || getInterceptor().test(localDate)){
            date.set(localDate);
        }
    }

    protected ObjectProperty<LocalDate> dateProperty(){
        return date;
    }

    public ReadOnlyObjectProperty<LocalDate> dateReadOnlyProperty() {
        var readOnlyObjectWrapper = new ReadOnlyObjectWrapper<LocalDate>();
        readOnlyObjectWrapper.bind(date);
        return readOnlyObjectWrapper.getReadOnlyProperty();
    }

    @Override
    public void setShowIcon(boolean showIcon) {
        super.setShowIcon(showIcon);
        dateIco.setVisible(showIcon);
        dateIco.setManaged(false);
        if (showIcon){
            dateBG.getStyleClass().remove(HIDE_ICO_DATE_BACKGROUND_STYLE_CLASS);
        }else {
            dateBG.getStyleClass().remove(HIDE_ICO_DATE_BACKGROUND_STYLE_CLASS);
            dateBG.getStyleClass().add(HIDE_ICO_DATE_BACKGROUND_STYLE_CLASS);
        }
    }

    @Override
    public void setShowBg(boolean showBg) {
        super.setShowBg(showBg);
        dateBG.setVisible(showBg);
    }

    @Override
    public Predicate<LocalDate> getInterceptor() {
        return this.calendar.getInterceptor();
    }

    @Override
    public ObjectProperty<Predicate<LocalDate>> interceptorProperty() {
        return this.calendar.interceptorProperty();
    }

    @Override
    public void setInterceptor(Predicate<LocalDate> dateInterceptor) {
        this.calendar.setInterceptor(dateInterceptor);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public Date() {}

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
    private static final String HIDE_ICO_DATE_BACKGROUND_STYLE_CLASS = "hideIcoDateBackground";
    private static final String DATE_BACKGROUND_FOCUS_STYLE_CLASS = "dateBackgroundFocus";
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd");

    private boolean isFromDate;
    private Calendar calendar;

    @Override
    protected void afterPageLoaded(){
        initDateTextField(year, MAX_YEAR, 4);
        initDateTextField(month, MAX_MONTH, 2);
        initDateTextField(day, MAX_DAY, 2);
        focusTraversableProperty().addListener((observableValue, aBoolean, t1) -> {
            year.setFocusTraversable(t1);
            month.setFocusTraversable(t1);
            day.setFocusTraversable(t1);
        });
    }

    @Override
    protected Node createIconNode() {
        return dateIco;
    }

    @Override
    public Popup createPopup() {
        Popup popup = new Popup();
        calendar = new Calendar();
        date = calendar.dateProperty();
        date.addListener((observable, oldValue, newValue) -> updateCompleteDateTextField(newValue));
        popup.getContent().add(calendar);
        popup.setAutoHide(true);
        return popup;
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
                setLocalDate(null);
            } else if (!isFromDate && newValue.length() == maxLength){
                if (yearInt != 0 && monthInt != 0 && dayInt != 0){
                    LocalDate newLocalDate = LocalDate.of(yearInt, monthInt, dayInt);
                    if (getInterceptor() == null || getInterceptor().test(newLocalDate)){
                        setLocalDate(newLocalDate);
                    }else {
                        updateCompleteDateTextField(getLocalDate());
                    }
                }
            }
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
                dateBG.getStyleClass().add(DATE_BACKGROUND_FOCUS_STYLE_CLASS);
            }else {
                textField.setText(standardizationDateText(textField, textField.getText()));
                dateBG.getStyleClass().remove(DATE_BACKGROUND_FOCUS_STYLE_CLASS);
            }
            setFocusedField(year.isFocused() || month.isFocused() || day.isFocused());
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
            if (timeStr.matches("0+")) {
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

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public void refresh(){
        if (getLocalDate() == null){
            year.setText("");
            month.setText("");
            day.setText("");
        }else {
            String[] dates = DATE_FORMATTER.format(getLocalDate()).split("/");
            year.setText(dates[0]);
            month.setText(dates[1]);
            day.setText(dates[2]);
        }
    }
}
