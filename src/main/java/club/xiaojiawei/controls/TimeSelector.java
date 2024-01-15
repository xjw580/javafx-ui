package club.xiaojiawei.controls;

import club.xiaojiawei.utils.ScrollUtil;
import javafx.beans.property.ObjectProperty;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.config.JavaFXUIThreadPoolConfig.SCHEDULED_POOL;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/25 17:33
 */
@SuppressWarnings("unused")
public class TimeSelector extends FlowPane {
    /**
     * 时间
     */
    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>();
    /**
     * 展示行数
     */
    @Getter private double showRowCount;
    /**
     * 显示秒
     */
    @Getter private boolean showSec;
    /**
     * @param time 格式：HH:mm，showSec=true时HH:mm:ss
     */
    public void setTime(String time) {
        if (time == null || time.isBlank()){
            this.time.set(null);
        }else if (showSec){
            this.time.set(LocalTime.from(TIME_FULL_FORMATTER.parse(time)));
        }else {
            this.time.set(LocalTime.from(TIME_FORMATTER.parse(time)));
        }
    }
    public void setLocalTime(LocalTime localTime){
        time.set(localTime);
    }
    public String getTime() {
        return time.get() == null? null : (showSec? TIME_FULL_FORMATTER.format(time.get()) : TIME_FORMATTER.format(time.get()));
    }
    public ObjectProperty<LocalTime> timeProperty() {
        return time;
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
            this.setMinWidth(128);
            this.setMaxWidth(128);
            secSelector.setVisible(true);
            secSelector.setManaged(true);
        }else {
            this.setMinWidth(86);
            this.setMaxWidth(86);
            secSelector.setVisible(false);
            secSelector.setManaged(false);
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
                    if (showSec){
                        for (Toggle toggle : secGroup.getToggles()) {
                            if (toggle instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), timeArr[2])){
                                secGroup.selectToggle(toggle);
                                break;
                            }
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
                SCHEDULED_POOL.schedule(() -> ScrollUtil.buildSlideTimeLine(pointTime, maxValue + 1, showRowCount, timeScroll).play(), 50, TimeUnit.MILLISECONDS);
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
//            设置时间
            if (hourGroup.getSelectedToggle() != null && minGroup.getSelectedToggle() != null){
                if (showSec){
                    if (secGroup.getSelectedToggle() != null){
                        setTime(((ToggleButton) hourGroup.getSelectedToggle()).getText() + ":" + ((ToggleButton) minGroup.getSelectedToggle()).getText() + ":" + ((ToggleButton) secGroup.getSelectedToggle()).getText());
                    }
                }else {
                    setTime(((ToggleButton) hourGroup.getSelectedToggle()).getText() + ":" + ((ToggleButton) minGroup.getSelectedToggle()).getText());
                }
            }
        });
    }

    /**
     * 为时间选择器添加按键监听器
     */
    private void addTimeSelectorKeyPressedListener(){
        hourSelector.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> time.set(time.get().plusHours(1));
                case DOWN -> time.set(time.get().minusHours(1));
            }
        });
        minSelector.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> time.set(time.get().plusMinutes(1));
                case DOWN -> time.set(time.get().minusMinutes(1));
            }
        });
        secSelector.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> time.set(time.get().plusSeconds(1));
                case DOWN -> time.set(time.get().minusSeconds(1));
            }
        });
    }
}
