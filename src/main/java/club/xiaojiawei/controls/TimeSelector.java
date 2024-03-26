package club.xiaojiawei.controls;

import club.xiaojiawei.func.Interceptor;
import club.xiaojiawei.utils.ScrollUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static club.xiaojiawei.config.JavaFXUIThreadPoolConfig.SCHEDULED_POOL;

/**
 * 事件选择器
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/25 17:33
 */
@SuppressWarnings("unused")
public class TimeSelector extends FlowPane implements Interceptor<LocalTime> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 时间
     */
    protected final ReadOnlyObjectWrapper<LocalTime> time = new ReadOnlyObjectWrapper<>();
    /**
     * 展示行数
     */
    @Getter private double showRowCount;
    /**
     * 显示秒
     */
    @Getter private boolean showSec;

    /**
     * 时间拦截器，点击日期时拦截是否应用
     */
    private final ObjectProperty<Predicate<LocalTime>> timeInterceptor = new SimpleObjectProperty<>();

    /**
     * @param time 格式：HH:mm，showSec=true时HH:mm:ss
     */
    public void setTime(String time) {
        if (time == null || time.isBlank()){
            setLocalTime(null);
        }else if (showSec){
            setLocalTime(LocalTime.from(TIME_FULL_FORMATTER.parse(time)));
        }else {
            setLocalTime(LocalTime.from(TIME_FORMATTER.parse(time)));
        }
    }

    public void setLocalTime(LocalTime localTime){
        if (test(localTime)){
            time.set(localTime);
        }
    }

    public String getTime() {
        return getLocalTime() == null? null : (showSec? TIME_FULL_FORMATTER.format(getLocalTime()) : TIME_FORMATTER.format(getLocalTime()));
    }

    public LocalTime getLocalTime(){
        return time.get();
    }

    public ObjectProperty<LocalTime> timeProperty(){
        if (virtualTime == null){
            virtualTime = new SimpleObjectProperty<>(getLocalTime());
            virtualTime.addListener((observableValue, localDate, t1) -> {
                if (test(t1)){
                    time.set(t1);
                }else {
                    virtualTime.set(localDate);
                }
            });
        }
        return virtualTime;
    }

    public ReadOnlyObjectProperty<LocalTime> readOnlyTimeProperty(){
        return time.getReadOnlyProperty();
    }

    public void setShowRowCount(double showRowCount) {
        this.showRowCount = showRowCount;
        double height = showRowCount * ROW_HEIGHT;
        hourSelector.setMaxHeight(height);
        minSelector.setMaxHeight(height);
        secSelector.setMaxHeight(height);
        this.setMaxHeight(height);
    }

    public void setShowSec(boolean showSec) {
        this.showSec = showSec;
        if (showSec){
            this.setMinWidth(132);
            this.setMaxWidth(132);
            secSelector.setVisible(true);
            secSelector.setManaged(true);
        }else {
            this.setMinWidth(90);
            this.setMaxWidth(90);
            secSelector.setVisible(false);
            secSelector.setManaged(false);
        }
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public TimeSelector() {
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

    @FXML private ScrollPane hourSelector;
    @FXML private ScrollPane minSelector;
    @FXML private ScrollPane secSelector;
    @FXML private VBox hourVbox;
    @FXML private VBox minVbox;
    @FXML private VBox secVbox;

    private static final String SELECTED_TIME_LABEL_STYLE_CLASS = "selectedTimeLabel";
    private static final String TIME_LABEL_STYLE_CLASS = "timeLabel";
    private static final int MAX_HOUR = 23;
    private static final int MAX_MIN = 59;
    private static final int MAX_SEC = 59;
    private static final double ROW_HEIGHT = 30D;
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter TIME_FULL_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ToggleGroup hourGroup = new ToggleGroup();
    private final ToggleGroup minGroup = new ToggleGroup();
    private final ToggleGroup secGroup = new ToggleGroup();
    private ObjectProperty<LocalTime> virtualTime;

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded(){
        addTimePropertyChangedListener();
        buildTimeSelector(hourVbox.getChildren(), MAX_HOUR, hourGroup);
        buildTimeSelector(minVbox.getChildren(), MAX_MIN, minGroup);
        buildTimeSelector(secVbox.getChildren(), MAX_SEC, secGroup);
        setShowRowCount(6);
        addTimeSelectorKeyPressedListener();
    }

    /**
     * 添加时间监听器
     */
    private void addTimePropertyChangedListener(){
        time.addListener((observable, oldTime, newTime) -> {
            if (virtualTime != null){
                virtualTime.set(newTime);
            }
            if (newTime == null){
                hourGroup.selectToggle(null);
                minGroup.selectToggle(null);
                if (showSec){
                    secGroup.selectToggle(null);
                }
            }else {
                String[] timeArr;
                if (showSec){
                    timeArr = TIME_FULL_FORMATTER.format(newTime).split(":");
                    for (Toggle toggle : secGroup.getToggles()) {
                        if (toggle instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), timeArr[2])){
                            secGroup.selectToggle(toggle);
                            break;
                        }
                    }
                }else {
                    timeArr = TIME_FORMATTER.format(newTime).split(":");
                }
                for (Toggle toggle : hourGroup.getToggles()) {
                    if (toggle instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), timeArr[0])){
                        hourGroup.selectToggle(toggle);
                        break;
                    }
                }
                for (Toggle toggle : minGroup.getToggles()) {
                    if (toggle instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), timeArr[1])){
                        minGroup.selectToggle(toggle);
                        break;
                    }
                }
                syncTimeSelector(hourVbox.getChildren(), newTime.getHour(), MAX_HOUR, showRowCount, hourSelector);
//                延迟同步，防止动画播放失败
                SCHEDULED_POOL.schedule(() -> syncTimeSelector(minVbox.getChildren(), newTime.getMinute(), MAX_MIN, showRowCount, minSelector), 60, TimeUnit.MILLISECONDS);
                if (showSec){
                    SCHEDULED_POOL.schedule(() -> syncTimeSelector(secVbox.getChildren(), newTime.getSecond(), MAX_SEC, showRowCount, secSelector), 120, TimeUnit.MILLISECONDS);
                }
            }
        });
    }

    /**
     * 同步时间选择器里小时或分钟
     * @param timeChildren 小时或分钟Label列表
     * @param pointTime 同步到此时间
     * @param maxValue 最大时间
     * @param showRowCount 展示时间行数
     * @param timeScroll 时间选择器
     */
    private void syncTimeSelector(ObservableList<Node> timeChildren, int pointTime, int maxValue, double showRowCount, ScrollPane timeScroll){
        for (int i = 0; i < timeChildren.size(); i++) {
            Node child = timeChildren.get(i);
            child.getStyleClass().remove(SELECTED_TIME_LABEL_STYLE_CLASS);
            if (i == pointTime){
                child.getStyleClass().add(SELECTED_TIME_LABEL_STYLE_CLASS);
                ScrollUtil.buildSlideTimeLine(pointTime, maxValue + 1, showRowCount, timeScroll).play();
            }
        }
    }

    /**
     * 构建时间选择器
     * @param timeChildren 容器的可观察列表
     * @param maxTime 最大时或最大分
     * @param timeToggleGroup 时间组
     */
    private void buildTimeSelector(ObservableList<Node> timeChildren, int maxTime, ToggleGroup timeToggleGroup){
        for (int i = 0; i <= maxTime ; i++) {
            ToggleButton label = new ToggleButton();
            label.setToggleGroup(timeToggleGroup);
            label.getStyleClass().add(TIME_LABEL_STYLE_CLASS);
            if (i < 10){
                label.setText("0" + i);
            }else {
                label.setText(String.valueOf(i));
            }
            timeChildren.add(label);
        }
        timeToggleGroup.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
//            移除旧选中效果
            if (oldToggle != null){
                ((ToggleButton)oldToggle).getStyleClass().remove(SELECTED_TIME_LABEL_STYLE_CLASS);
            }
//            添加新选中效果
            if (newToggle != null){
                ((ToggleButton)newToggle).getStyleClass().add(SELECTED_TIME_LABEL_STYLE_CLASS);
            }
            ToggleButton hourSelected = (ToggleButton) hourGroup.getSelectedToggle();
//            设置时间
            if (hourSelected == null){
                return;
            }
            ToggleButton minSelected = (ToggleButton) minGroup.getSelectedToggle();
            ToggleButton secSelected = (ToggleButton) secGroup.getSelectedToggle();
            if (getInterceptor() == null){
                if (minSelected != null){
                    if (showSec){
                        if (secSelected != null){
                            setLocalTime(LocalTime.from(TIME_FULL_FORMATTER.parse(hourSelected.getText() + ":" + minSelected.getText() + ":" + secSelected.getText())));
                        }
                    }else {
                        setLocalTime(LocalTime.from(TIME_FORMATTER.parse(hourSelected.getText() + ":" + minSelected.getText())));
                    }
                }
                return;
            }
            if (showSec){
                if (minSelected == null && secSelected == null){
                    if (getInterceptor().test(LocalTime.from(TIME_FULL_FORMATTER.parse(hourSelected.getText() + ":59:59")))
                            || getInterceptor().test(LocalTime.from(TIME_FULL_FORMATTER.parse(hourSelected.getText() + ":00:00")))
                    ){
                        return;
                    }
                }else if (minSelected != null && secSelected != null){
                    LocalTime localTime = LocalTime.from(TIME_FULL_FORMATTER.parse(hourSelected.getText() + ":" + minSelected.getText() + ":" + secSelected.getText()));
                    if (getInterceptor().test(localTime)){
                        setLocalTime(localTime);
                        return;
                    }
                }else if (minSelected == null){
                    if (getInterceptor().test(LocalTime.from(TIME_FULL_FORMATTER.parse(hourSelected.getText() + ":59:" + secSelected.getText())))
                            || getInterceptor().test(LocalTime.from(TIME_FULL_FORMATTER.parse(hourSelected.getText() + ":00:" + secSelected.getText())))
                    ){
                        return;
                    }
                }else {
                    if (getInterceptor().test(LocalTime.from(TIME_FULL_FORMATTER.parse(hourSelected.getText() + ":" + minSelected.getText() + ":59")))
                            || getInterceptor().test(LocalTime.from(TIME_FULL_FORMATTER.parse(hourSelected.getText() + ":" + minSelected.getText() + ":00")))
                    ){
                        return;
                    }
                }
            }else {
                if (minSelected == null){
                    if (getInterceptor().test(LocalTime.from(TIME_FORMATTER.parse(hourSelected.getText() + ":59")))
                            || getInterceptor().test(LocalTime.from(TIME_FORMATTER.parse(hourSelected.getText() + ":00")))
                    ){
                        return;
                    }
                }else {
                    LocalTime localTime = LocalTime.from(TIME_FORMATTER.parse(hourSelected.getText() + ":" + minSelected.getText()));
                    if (getInterceptor().test(localTime)){
                        setLocalTime(localTime);
                        return;
                    }
                }
            }
            timeToggleGroup.selectToggle(oldToggle);
        });
    }

    /**
     * 为时间选择器添加按键监听器
     */
    private void addTimeSelectorKeyPressedListener(){
        hourSelector.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> setLocalTime(getLocalTime().plusHours(1));
                case DOWN -> setLocalTime(getLocalTime().minusHours(1));
            }
        });
        minSelector.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> setLocalTime(getLocalTime().plusMinutes(1));
                case DOWN -> setLocalTime(getLocalTime().minusMinutes(1));
            }
        });
        secSelector.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> setLocalTime(getLocalTime().plusSeconds(1));
                case DOWN -> setLocalTime(getLocalTime().minusSeconds(1));
            }
        });
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public Predicate<LocalTime> getInterceptor() {
        return timeInterceptor.get();
    }

    @Override
    public ObjectProperty<Predicate<LocalTime>> interceptorProperty() {
        return timeInterceptor;
    }

    @Override
    public void setInterceptor(Predicate<LocalTime> dateInterceptor) {
        this.timeInterceptor.set(dateInterceptor);
    }
}
