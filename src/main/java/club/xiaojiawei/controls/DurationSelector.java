package club.xiaojiawei.controls;

import club.xiaojiawei.func.DateTimeInterceptor;
import club.xiaojiawei.utils.ScrollUtil;
import club.xiaojiawei.utils.TimeUtils;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static club.xiaojiawei.config.JavaFXUIThreadPoolConfig.SCHEDULED_POOL;

/**
 * 时间选择器
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/25 17:33
 */
@SuppressWarnings("unused")
public class DurationSelector extends FlowPane implements DateTimeInterceptor<Duration> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 时间
     */
    protected final ReadOnlyObjectWrapper<Duration> time = new ReadOnlyObjectWrapper<>();
    /**
     * 展示行数
     */
    @Getter
    private double showRowCount;
    /**
     * 显示秒
     */
    @Getter
    private boolean showSec;

    /**
     * 时间拦截器，点击日期时拦截是否应用
     */
    private final ObjectProperty<Predicate<Duration>> timeInterceptor = new SimpleObjectProperty<>();

    /**
     * @param time 格式：HH:mm，showSec=true时HH:mm:ss
     */
    public void setTime(String time) {
        if (time == null || time.isBlank()) {
            setLocalTime(null);
        } else if (showSec) {
            setLocalTime(timeUtils.secStringToDuration(time));
        } else {
            setLocalTime(timeUtils.stringToDuration(time));
        }
    }

    public void setLocalTime(Duration duration) {
        if (test(duration)) {
            time.set(duration);
        }
    }

    public String getTime() {
        return getLocalTime() == null ? null : (showSec ? timeUtils.durationToSecString(getLocalTime()) : timeUtils.durationToString(getLocalTime()));
    }

    public Duration getLocalTime() {
        return time.get();
    }

    public ObjectProperty<Duration> timeProperty() {
        if (virtualTime == null) {
            virtualTime = new SimpleObjectProperty<>(getLocalTime());
            virtualTime.addListener((observableValue, localDate, t1) -> {
                if (test(t1)) {
                    time.set(t1);
                } else {
                    virtualTime.set(localDate);
                }
            });
        }
        return virtualTime;
    }

    public ReadOnlyObjectProperty<Duration> readOnlyTimeProperty() {
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
        if (showSec) {
            this.setMinWidth(133);
            this.setMaxWidth(133);
            secSelector.setVisible(true);
            secSelector.setManaged(true);
        } else {
            this.setMinWidth(91);
            this.setMaxWidth(91);
            secSelector.setVisible(false);
            secSelector.setManaged(false);
        }
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public DurationSelector(
            @NamedArg("maxHour") int maxHour, @NamedArg("maxMin") int maxMin, @NamedArg("maxSec") int maxSec,
            @NamedArg("minHour") int minHour, @NamedArg("minMin") int minMin, @NamedArg("minSec") int minSec
    ) {
        timeUtils = new TimeUtils(maxHour, maxMin, maxSec, minHour, minMin, minSec);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(TimeSelector.class.getResource(TimeSelector.class.getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private ScrollPane hourSelector;
    @FXML
    private ScrollPane minSelector;
    @FXML
    private ScrollPane secSelector;
    @FXML
    private VBox hourVbox;
    @FXML
    private VBox minVbox;
    @FXML
    private VBox secVbox;

    private static final String SELECTED_TIME_LABEL_STYLE_CLASS = "selectedTimeLabel";
    private static final String TIME_LABEL_STYLE_CLASS = "timeLabel";

    @Getter
    private final TimeUtils timeUtils;
    private static final double ROW_HEIGHT = 30D;

    private final ToggleGroup hourGroup = new ToggleGroup();
    private final ToggleGroup minGroup = new ToggleGroup();
    private final ToggleGroup secGroup = new ToggleGroup();
    private ObjectProperty<Duration> virtualTime;

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded() {
        addTimePropertyChangedListener();
        buildTimeSelector(hourVbox.getChildren(), timeUtils.maxHour(), hourGroup);
        buildTimeSelector(minVbox.getChildren(), timeUtils.maxMin(), minGroup);
        buildTimeSelector(secVbox.getChildren(), timeUtils.maxSec(), secGroup);
        setShowRowCount(6);
        addTimeSelectorKeyPressedListener();
    }

    /**
     * 添加时间监听器
     */
    private void addTimePropertyChangedListener() {
        time.addListener((observable, oldTime, newTime) -> {
            if (virtualTime != null) {
                virtualTime.set(newTime);
            }
            if (newTime == null) {
                hourGroup.selectToggle(null);
                minGroup.selectToggle(null);
                if (showSec) {
                    secGroup.selectToggle(null);
                }
            } else {
                String[] timeArr;
                if (showSec) {
                    timeArr = timeUtils.durationToSecString(newTime).split(":");
                    for (Toggle toggle : secGroup.getToggles()) {
                        if (toggle instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), timeArr[2])) {
                            secGroup.selectToggle(toggle);
                            break;
                        }
                    }
                } else {
                    timeArr = timeUtils.durationToString(newTime).split(":");
                }
                for (Toggle toggle : hourGroup.getToggles()) {
                    if (toggle instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), timeArr[0])) {
                        hourGroup.selectToggle(toggle);
                        break;
                    }
                }
                for (Toggle toggle : minGroup.getToggles()) {
                    if (toggle instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), timeArr[1])) {
                        minGroup.selectToggle(toggle);
                        break;
                    }
                }
                syncTimeSelector(hourVbox.getChildren(), newTime.toHours(), timeUtils.maxHour(), showRowCount, hourSelector);
//                延迟同步，防止动画播放失败
                SCHEDULED_POOL.schedule(() -> syncTimeSelector(minVbox.getChildren(), timeUtils.getMinutesFromDuration(newTime), timeUtils.maxMin(), showRowCount, minSelector), 60, TimeUnit.MILLISECONDS);
                if (showSec) {
                    SCHEDULED_POOL.schedule(() -> syncTimeSelector(secVbox.getChildren(), timeUtils.getSecondsFromDuration(newTime), timeUtils.maxSec(), showRowCount, secSelector), 120, TimeUnit.MILLISECONDS);
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
    private void syncTimeSelector(ObservableList<Node> timeChildren, long pointTime, long maxValue, double showRowCount, ScrollPane timeScroll) {
        for (int i = 0; i < timeChildren.size(); i++) {
            Node child = timeChildren.get(i);
            child.getStyleClass().remove(SELECTED_TIME_LABEL_STYLE_CLASS);
            if (i == pointTime) {
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
    private void buildTimeSelector(ObservableList<Node> timeChildren, int maxTime, ToggleGroup timeToggleGroup) {
        for (int i = 0; i <= maxTime; i++) {
            ToggleButton label = new ToggleButton();
            label.setToggleGroup(timeToggleGroup);
            label.getStyleClass().add(TIME_LABEL_STYLE_CLASS);
            if (i < 10) {
                label.setText("0" + i);
            } else {
                label.setText(String.valueOf(i));
            }
            timeChildren.add(label);
        }
        timeToggleGroup.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
//            移除旧选中效果
            if (oldToggle != null) {
                ((ToggleButton) oldToggle).getStyleClass().remove(SELECTED_TIME_LABEL_STYLE_CLASS);
            }
//            添加新选中效果
            if (newToggle != null) {
                ((ToggleButton) newToggle).getStyleClass().add(SELECTED_TIME_LABEL_STYLE_CLASS);
            }
            ToggleButton hourSelected = (ToggleButton) hourGroup.getSelectedToggle();
//            设置时间
            if (hourSelected == null) {
                return;
            }
            ToggleButton minSelected = (ToggleButton) minGroup.getSelectedToggle();
            ToggleButton secSelected = (ToggleButton) secGroup.getSelectedToggle();
            if (getInterceptor() == null) {
                if (minSelected != null) {
                    if (showSec) {
                        if (secSelected != null) {
                            setLocalTime(timeUtils.secStringToDuration(hourSelected.getText() + ":" + minSelected.getText() + ":" + secSelected.getText()));
                        }
                    } else {
                        setLocalTime(timeUtils.stringToDuration(hourSelected.getText() + ":" + minSelected.getText()));
                    }
                }
                return;
            }
            if (showSec) {
                if (minSelected == null && secSelected == null) {
                    if (getInterceptor().test(timeUtils.secStringToDuration(String.format("%s:%02d:%02d", hourSelected.getText(), timeUtils.maxMin(), timeUtils.maxSec())))
                        || getInterceptor().test(timeUtils.secStringToDuration(String.format("%s:%02d:%02d", hourSelected.getText(), timeUtils.minMin(), timeUtils.minSec())))
                    ) {
                        return;
                    }
                } else if (minSelected != null && secSelected != null) {
                    Duration duration = timeUtils.secStringToDuration(hourSelected.getText() + ":" + minSelected.getText() + ":" + secSelected.getText());
                    if (getInterceptor().test(duration)) {
                        setLocalTime(duration);
                        return;
                    }
                } else if (minSelected == null) {
                    if (getInterceptor().test(timeUtils.secStringToDuration(String.format("%s:%02d:%s", hourSelected.getText(), timeUtils.maxMin(), secSelected.getText())))
                        || getInterceptor().test(timeUtils.secStringToDuration(String.format("%s:%02d:%s", hourSelected.getText(), timeUtils.minMin(), secSelected.getText())))
                    ) {
                        return;
                    }
                } else {
                    if (getInterceptor().test(timeUtils.secStringToDuration(String.format("%s:%s:%02d", hourSelected.getText(), minSelected.getText(), timeUtils.maxSec())))
                        || getInterceptor().test(timeUtils.secStringToDuration(String.format("%s:%s:%02d", hourSelected.getText(), minSelected.getText(), timeUtils.minSec())))
                    ) {
                        return;
                    }
                }
            } else {
                if (minSelected == null) {
                    if (getInterceptor().test(timeUtils.stringToDuration(String.format("%s:%02d", hourSelected.getText(), timeUtils.maxMin())))
                        || getInterceptor().test(timeUtils.stringToDuration(String.format("%s:%02d", hourSelected.getText(), timeUtils.minMin())))
                    ) {
                        return;
                    }
                } else {
                    Duration duration = timeUtils.stringToDuration(String.format("%s:%s", hourSelected.getText(), minSelected.getText()));
                    if (getInterceptor().test(duration)) {
                        setLocalTime(duration);
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
    private void addTimeSelectorKeyPressedListener() {
        hourSelector.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> setLocalTime(getLocalTime().plusHours(1));
                case DOWN -> setLocalTime(getLocalTime().minusHours(1));
            }
        });
        minSelector.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> setLocalTime(getLocalTime().plusMinutes(1));
                case DOWN -> setLocalTime(getLocalTime().minusMinutes(1));
            }
        });
        secSelector.setOnKeyPressed(event -> {
            switch (event.getCode()) {
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
    public Predicate<Duration> getInterceptor() {
        return timeInterceptor.get();
    }

    @Override
    public ObjectProperty<Predicate<Duration>> interceptorProperty() {
        return timeInterceptor;
    }

    @Override
    public void setInterceptor(Predicate<Duration> dateInterceptor) {
        this.timeInterceptor.set(dateInterceptor);
    }
}
