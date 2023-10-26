package club.xiaojiawei.controls;

import club.xiaojiawei.utils.ScrollUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/25 17:33
 */
@SuppressWarnings("unused")
public class TimeSelector extends FlowPane {
    /**
     * 时间
     */
    private final StringProperty time = new SimpleStringProperty();
    /**
     * 展示行数
     */
    private double showRowCount;
    public String getTime() {
        return time.get();
    }
    public StringProperty timeProperty() {
        return time;
    }
    public void setTime(String time) {
        this.time.set(time);
    }
    public double getShowRowCount() {
        return showRowCount;
    }
    public void setShowRowCount(double showRowCount) {
        this.showRowCount = showRowCount;
        hourSelector.setMaxHeight(showRowCount * 30);
        minSelector.setMaxHeight(showRowCount * 30);
    }
    @FXML
    private ScrollPane hourSelector;
    @FXML
    private ScrollPane minSelector;
    @FXML
    private VBox hourVbox;
    @FXML
    private VBox minVbox;
    private static final String SELECTED_TIME_LABEL = "selectedTimeLabel";
    private static final String TIME_LABEL = "timeLabel";
    private static final int MAX_HOUR = 23;
    private static final int MAX_MIN = 59;
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
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
        fillHourSelector();
        fillMinSelector();
        setShowRowCount(6);
        addTimeSelectorKeyPressedListener();
        time.set("00:00");
    }

    /**
     * 添加时间监听器
     */
    private void addTimePropertyChangedListener(){
        time.addListener((observable, oldValue, newValue) -> {
            ObservableList<Node> hourChildren = hourVbox.getChildren();
            ObservableList<Node> minChildren = minVbox.getChildren();
            LocalTime newTime = LocalTime.from(TIME_FORMATTER.parse(newValue));
            LocalTime oldTime = oldValue == null? null : LocalTime.from(TIME_FORMATTER.parse(oldValue));
            if (oldTime == null || newTime.getHour() != oldTime.getHour()){
                syncTimeSelector(hourChildren, newTime.getHour(), MAX_HOUR, showRowCount, hourSelector);
            }
            if (oldTime == null || newTime.getMinute() != oldTime.getMinute()){
                syncTimeSelector(minChildren, newTime.getMinute(), MAX_MIN, showRowCount, minSelector);
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
            child.getStyleClass().remove(SELECTED_TIME_LABEL);
            if (i == pointTime){
                child.getStyleClass().add(SELECTED_TIME_LABEL);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ScrollUtil.slide(pointTime, maxValue + 1, showRowCount, timeScroll, 1);
                    }
                }, 50);
            }
        }
    }

    /**
     * 填充小时选择器
     */
    private void fillHourSelector(){
        ObservableList<Node> children = hourVbox.getChildren();
        for (int i = 0; i <= MAX_HOUR ; i++) {
            Label label = new Label();
            label.getStyleClass().add(TIME_LABEL);
            if (i < 10){
                label.setText("0" + i);
            }else {
                label.setText(String.valueOf(i));
            }
            label.setOnMouseClicked(e -> time.set(label.getText() + time.get().substring(2 )));
            children.add(label);
        }
    }

    /**
     * 填充分钟选择器
     */
    private void fillMinSelector(){
        ObservableList<Node> children = minVbox.getChildren();
        for (int i = 0; i <= MAX_MIN ; i++) {
            Label label = new Label();
            label.getStyleClass().add(TIME_LABEL);
            if (i < 10){
                label.setText("0" + i);
            }else {
                label.setText(String.valueOf(i));
            }
            label.setOnMouseClicked(e -> time.set(time.get().substring(0, 3) + label.getText()));
            children.add(label);
        }
    }

    /**
     * 为事件选择器添加按键监听器
     */
    private void addTimeSelectorKeyPressedListener(){
        hourSelector.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> time.set(TIME_FORMATTER.format(LocalTime.from(TIME_FORMATTER.parse(time.get())).plusHours(1)));
                case DOWN -> time.set(TIME_FORMATTER.format(LocalTime.from(TIME_FORMATTER.parse(time.get())).minusHours(1)));
            }
        });
        minSelector.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> time.set(TIME_FORMATTER.format(LocalTime.from(TIME_FORMATTER.parse(time.get())).plusMinutes(1)));
                case DOWN -> time.set(TIME_FORMATTER.format(LocalTime.from(TIME_FORMATTER.parse(time.get())).minusMinutes(1)));
            }
        });
    }
}
