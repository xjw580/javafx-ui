package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.DateIco;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/26 8:34
 */
public class DateTime extends AbstractDateTimeField<LocalDateTime> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 日期时间
     */
    private ReadOnlyObjectWrapper<LocalDateTime> dateTime;

    private final ObjectProperty<Predicate<LocalDateTime>> dateTimeInterceptor = new SimpleObjectProperty<>();

    public String getDateTime() {
        return dateTime.get() == null? null : DATE_TIME_FORMATTER.format(dateTime.get());
    }

    public LocalDateTime getLocalDateTime(){
        return dateTime.get();
    }

    /**
     * @param dateTime 格式：yyyy/MM/dd-HH:mm
     */
    public void setDateTime(String dateTime) {
        if (dateTime == null ||dateTime.isBlank()){
            setLocalDateTime(null);
        }else {
            setLocalDateTime(LocalDateTime.from(DATE_TIME_FORMATTER.parse(dateTime)));
        }
    }

    public void setLocalDateTime(LocalDateTime localDateTime){
        if (test(localDateTime)){
            this.dateTime.set(localDateTime);
        }
    }

    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        if (virtualDateTime == null){
            virtualDateTime = new SimpleObjectProperty<>(getLocalDateTime());
            virtualDateTime.addListener((observableValue, o, t1) -> {
                if (test(t1)){
                    dateTime.set(t1);
                }else {
                    virtualDateTime.set(o);
                }
            });
        }
        return virtualDateTime;
    }

    public ReadOnlyObjectProperty<LocalDateTime> readOnlyObjectProperty(){
        return dateTime.getReadOnlyProperty();
    }

    @Override
    public void setShowIcon(boolean showIcon) {
        super.setShowIcon(showIcon);
        dateTimeIco.setVisible(showIcon);
        dateTimeIco.setManaged(false);
        if (showIcon){
            dateTimeBg.getStyleClass().remove(HIDE_ICO_DATE_TIME_BACKGROUND_STYLE_CLASS);
        }else {
            dateTimeBg.getStyleClass().add(HIDE_ICO_DATE_TIME_BACKGROUND_STYLE_CLASS);
        }
    }

    @Override
    public void setShowBg(boolean showBg) {
        super.setShowBg(showBg);
        dateTimeBg.setVisible(showBg);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public DateTime() {}

    @FXML
    private Label dateTimeBg;
    @FXML
    private DateIco dateTimeIco;
    @FXML
    private Date dateControls;
    @FXML
    private Time timeControls;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm");
    public static final String HIDE_ICO_DATE_TIME_BACKGROUND_STYLE_CLASS = "hideIcoDateTimeBackground";
    private static final String DATE_TIME_BACKGROUND_FOCUS_STYLE_CLASS = "dateTimeBackgroundFocus";

    private boolean isFromDateTime;
    private ObjectProperty<LocalDateTime> virtualDateTime;

    @Override
    protected Popup createPopup() {
        Popup dateTimeSelectorPopup = new Popup();
        HBox hBox = new HBox();
        hBox.setStyle("-fx-effect: dropshadow(gaussian, effect-color, 10, 0, 0, 0)!important;");
        TimeSelector timeSelector = (TimeSelector) timeControls.getPopup().getContent().get(0);
        timeSelector.setStyle("-fx-background-radius: 0 10 10 0;-fx-effect: none!important;");
        timeSelector.setShowRowCount(9.55);
        Calendar calendar = (Calendar) dateControls.getPopup().getContent().get(0);
        calendar.setStyle("-fx-background-radius: 10 0 0 10;-fx-effect: none!important;");
        hBox.getChildren().add(calendar);
        hBox.getChildren().add(timeSelector);
        dateTimeSelectorPopup.getContent().add(hBox);
        dateTimeSelectorPopup.setAutoHide(true);
        return dateTimeSelectorPopup;
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
        dateControls.focusedReadOnlyProperty().addListener((observableValue, aBoolean, t1) -> dealFocusChange(t1));
        dateControls.dateProperty().addListener((observable, oldValue, newValue) -> afterDateTimeChange());
        dateControls.setInterceptor(localDate -> {
            if (localDate != null && getInterceptor() != null){
                LocalTime localTime = timeControls.getLocalTime();
                if (localTime == null){
                    return getInterceptor().test(LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), LocalTime.MIN.getHour(), LocalTime.MIN.getMinute()))
                            || getInterceptor().test(LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), LocalTime.MAX.getHour(), LocalTime.MAX.getMinute()));
                }else {
                    return getInterceptor().test(LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), localTime.getHour(), localTime.getMinute()));
                }
            }
            return true;
        });
        timeControls.timeProperty().addListener((observable, oldValue, newValue) -> afterDateTimeChange());
        timeControls.focusedReadOnlyProperty().addListener((observableValue, aBoolean, t1) -> dealFocusChange(t1));
        timeControls.setInterceptor(localTime -> {
            if (localTime != null && getInterceptor() != null){
                LocalDate localDate = dateControls.getLocalDate();
                if (localDate == null){
                    LocalDate min = LocalDate.MIN, max = LocalDate.MAX;
                    return getInterceptor().test(LocalDateTime.of(min.getYear(), min.getMonth(), min.getDayOfMonth(), localTime.getHour(), localTime.getMinute()))
                            || getInterceptor().test(LocalDateTime.of(max.getYear(), max.getMonth(), max.getDayOfMonth(), localTime.getHour(), localTime.getMinute()));
                }else {
                    return getInterceptor().test(LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), localTime.getHour(), localTime.getMinute()));
                }
            }
            return true;
        });
        dateTime = new ReadOnlyObjectWrapper<>();
        dateTime.addListener((observable, oldValue, newValue) -> {
            if (virtualDateTime != null){
                virtualDateTime.set(newValue);
            }
            updateCompleteDateTime(newValue);
        });
        focusTraversableProperty().addListener((observableValue, aBoolean, t1) -> {
            dateControls.setFocusTraversable(t1);
            timeControls.setFocusTraversable(t1);
        });
    }

    @Override
    protected Node createIconNode() {
        return dateTimeIco;
    }

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterDateTimeChange(){
        if (!isFromDateTime){
            LocalDate localDate = dateControls.dateProperty().get();
            LocalTime localTime = timeControls.timeProperty().get();
            if (localDate != null && localTime != null){
                LocalDateTime localDateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), localTime.getHour(), localTime.getMinute());
                if (test(localDateTime)){
                    dateTime.set(localDateTime);
                }else {
                    LocalDateTime dateTime = getLocalDateTime();
                    if (dateTime == null){
                        dateControls.setDate(null);
                        PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));
                        pauseTransition.setOnFinished(e -> timeControls.setTime(null));
                        pauseTransition.play();
                    }else {
                        dateControls.setLocalDate(LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth()));
                        timeControls.setLocalTime(LocalTime.of(dateTime.getHour(), dateTime.getMinute()));
                    }
                }
            }else if (localDate == null && localTime == null){
                setLocalDateTime(null);
            }
        }
    }

    private void updateCompleteDateTime(LocalDateTime newDateTime){
        isFromDateTime = true;
        if (newDateTime != null){
            dateControls.setLocalDate(LocalDate.of(newDateTime.getYear(), newDateTime.getMonthValue(), newDateTime.getDayOfMonth()));
            timeControls.setLocalTime(LocalTime.of(newDateTime.getHour(), newDateTime.getMinute()));
        }
        isFromDateTime = false;
    }

    private void dealFocusChange(boolean isFocus) {
        if (isFocus){
            dateTimeBg.getStyleClass().add(DATE_TIME_BACKGROUND_FOCUS_STYLE_CLASS);
        }else {
            dateTimeBg.getStyleClass().remove(DATE_TIME_BACKGROUND_FOCUS_STYLE_CLASS);
        }
        setFocusedField(dateControls.isFocused() || timeControls.isFocused());
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public void refresh(){
        dateControls.refresh();
        timeControls.refresh();
    }

    @Override
    public Predicate<LocalDateTime> getInterceptor() {
        return dateTimeInterceptor.get();
    }

    @Override
    public ObjectProperty<Predicate<LocalDateTime>> interceptorProperty() {
        return dateTimeInterceptor;
    }

    @Override
    public void setInterceptor(Predicate<LocalDateTime> dateInterceptor) {
        this.dateTimeInterceptor.set(dateInterceptor);
    }

}
