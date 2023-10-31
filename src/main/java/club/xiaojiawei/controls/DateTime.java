package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.DateIco;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static club.xiaojiawei.enums.BaseTransitionEnum.FADE;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/26 8:34
 */
public class DateTime extends AnchorPane {
    /**
     * 日期时间
     */
    private final ObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    /**
     * 显示日期时间选择器
     */
    private boolean showSelector = true;
    /**
     * 默认显示边框
     */
    private boolean showBG = true;
    public String getDateTime() {
        return DATE_TIME_FORMATTER.format(dateTime.get());
    }
    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }
    public boolean isShowSelector() {
        return showSelector;
    }
    public void setShowSelector(boolean showSelector) {
        dateTimeIco.setVisible(this.showSelector = showSelector);
        dateTimeIco.setManaged(false);
        if (showSelector){
            dateTimeBG.getStyleClass().remove(HIDE_ICO_DATE_TIME_BACKGROUND);
        }else {
            dateTimeBG.getStyleClass().add(HIDE_ICO_DATE_TIME_BACKGROUND);
        }
    }
    public boolean isShowBG() {
        return showBG;
    }
    public void setShowBG(boolean showBG) {
        dateTimeBG.setVisible(this.showBG = showBG);
    }

    /**
     * @param dateTime 格式：yyyy/MM/dd-HH:mm
     */
    public void setDateTime(String dateTime) {
        this.dateTime.set(LocalDateTime.from(DATE_TIME_FORMATTER.parse(dateTime)));
    }
    public void setLocalDateTime(LocalDateTime localDateTime){
        this.dateTime.set(localDateTime);
    }

    @FXML
    private Label dateTimeBG;
    @FXML
    private DateIco dateTimeIco;
    @FXML
    private Date dateControls;
    @FXML
    private Time timeControls;
    private Popup dateTimeSelectorPopup;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm");
    public static final String HIDE_ICO_DATE_TIME_BACKGROUND = "hideIcoDateTimeBackground";
    private boolean isFromDateTime;
    private static final String DATE_TIME_BACKGROUND_FOCUS = "dateTimeBackgroundFocus";
    public DateTime() {
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
        dateControls.setOnFocusChangeListener((observable, oldValue, newValue) -> {
            dealFocusChange(newValue);
        });
        timeControls.setOnFocusChangeListener((observable, oldValue, newValue) -> {
            dealFocusChange(newValue);
        });
        LocalDate localDate = dateControls.dateProperty().get();
        LocalTime localTime = timeControls.timeProperty().get();
        dateTime.set(LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), localTime.getHour(), localTime.getMinute()));
        dateControls.dateProperty().addListener(dateChangeListener());
        timeControls.timeProperty().addListener(timeChangeListener());
        dateTime.addListener((observable, oldValue, newValue) -> updateCompleteDateTime(newValue));
        initDateTimeSelectorPopup();
        initDateTimeIco();
    }
    private ChangeListener<LocalDate> dateChangeListener(){
        return (observable, oldValue, newValue) -> afterDateTimeChange();
    }
    private ChangeListener<LocalTime> timeChangeListener(){
        return (observable, oldValue, newValue) -> afterDateTimeChange();
    }
    private void afterDateTimeChange(){
        if (!isFromDateTime){
            LocalDate localDate = dateControls.dateProperty().get();
            LocalTime localTime = timeControls.timeProperty().get();
            dateTime.set(LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), localTime.getHour(), localTime.getMinute()));
        }
    }
    private void updateCompleteDateTime(LocalDateTime newDateTime){
        isFromDateTime = true;
        dateControls.setLocalDate(LocalDate.of(newDateTime.getYear(), newDateTime.getMonthValue(), newDateTime.getDayOfMonth()));
        timeControls.setLocalTime(LocalTime.of(newDateTime.getHour(), newDateTime.getMinute()));
        isFromDateTime = false;
    }
    /**
     * 初始化日期时间选择器弹窗
     */
    private void initDateTimeSelectorPopup(){
        dateTimeSelectorPopup = new Popup();
        HBox hBox = new HBox();
        hBox.setSpacing(2);
        TimeSelector timeSelector = (TimeSelector) timeControls.getTimeSelectorPopup().getContent().get(0);
        timeSelector.setShowRowCount(9.7);
        Calendar calendar = (Calendar) dateControls.getDateSelectorPopup().getContent().get(0);
        hBox.getChildren().add(calendar);
        hBox.getChildren().add(timeSelector);
        dateTimeSelectorPopup.getContent().add(hBox);
        dateTimeSelectorPopup.setAutoHide(true);
    }

    /**
     * 初始化日期时间图标
     */
    private void initDateTimeIco(){
        dateTimeIco.setOnMouseClicked(e -> {
            Bounds bounds = dateTimeIco.localToScreen(dateTimeIco.getBoundsInLocal());
            dateTimeSelectorPopup.setAnchorX(bounds.getMaxX() - 100);
            dateTimeSelectorPopup.setAnchorY(bounds.getMaxY() - 5);
            dateTimeSelectorPopup.show(this.getScene().getWindow());
            FADE.play(dateTimeSelectorPopup.getContent().get(0), 0.5D, 1D, Duration.millis(200));
        });
    }

    private void dealFocusChange(boolean isFocus) {
        if (isFocus){
            dateTimeBG.getStyleClass().add(DATE_TIME_BACKGROUND_FOCUS);
        }else {
            dateTimeBG.getStyleClass().remove(DATE_TIME_BACKGROUND_FOCUS);
        }
    }
}
