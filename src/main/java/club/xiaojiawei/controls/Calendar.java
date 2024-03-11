package club.xiaojiawei.controls;

import club.xiaojiawei.func.Interceptor;
import club.xiaojiawei.utils.ScrollUtil;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.*;

import static club.xiaojiawei.controls.DateSelector.DATE_FORMATTER;
import static club.xiaojiawei.enums.BaseTransitionEnum.FADE;
import static club.xiaojiawei.enums.BaseTransitionEnum.SLIDE_Y;

/**
 * 日历
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 22:54
 */
@Slf4j
@SuppressWarnings("unused")
public class Calendar extends VBox implements Interceptor<LocalDate> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 日期
     */
    protected ObjectProperty<LocalDate> date;
    /**
     * 点击清除按钮事件处理器
     */
    private final ObjectProperty<EventHandler<MouseEvent>> onClearEventHandler = new SimpleObjectProperty<>();
    /**
     * 点击今天按钮事件处理器
     */
    private final ObjectProperty<EventHandler<MouseEvent>> onTodayEventHandler = new SimpleObjectProperty<>();
    /**
     * 日期拦截器，点击日期时拦截是否应用
     */
    private final ObjectProperty<Predicate<LocalDate>> dateInterceptor = new SimpleObjectProperty<>();

    public String getDate() {
        return getLocalDate() == null? "" : DATE_FORMATTER.format(getLocalDate());
    }

    public LocalDate getLocalDate(){
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty(){
        return dateSelector.dateProperty();
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
        if (test(localDate)){
            date.set(localDate);
        }
    }

    public EventHandler<MouseEvent> getOnClearEventHandler() {
        return onClearEventHandler.get();
    }

    public ObjectProperty<EventHandler<MouseEvent>> onClearEventHandlerProperty() {
        return onClearEventHandler;
    }

    public void setOnClearEventHandler(EventHandler<MouseEvent> onClearEventHandler) {
        this.onClearEventHandler.set(onClearEventHandler);
    }

    public EventHandler<MouseEvent> getOnTodayEventHandler() {
        return onTodayEventHandler.get();
    }

    public ObjectProperty<EventHandler<MouseEvent>> onTodayEventHandlerProperty() {
        return onTodayEventHandler;
    }

    public void setOnTodayEventHandler(EventHandler<MouseEvent> onTodayEventHandler) {
        this.onTodayEventHandler.set(onTodayEventHandler);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public Calendar() {
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

    @FXML
    private Label dateMsg;
    @FXML
    private HBox icoBox;
    @FXML
    private Label lastMonth;
    @FXML
    private Label nextMonth;
    @FXML
    private ScrollPane monthPaneScroll;
    @FXML
    private VBox monthsPane;
    @FXML
    private Label clear;
    @FXML
    private Label today;
    @FXML
    private VBox monthPane;
    @FXML
    private HBox bottomMsg;

    private static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月");
    private static final String SELECTED_DAY_LABEL_STYLE_CLASS = "selectedDayLabel";
    private static final String DAY_LABEL_STYLE_CLASS = "dayLabel";
    private static final String DAY_PANE_STYLE_CLASS = "dayPane";
    private static final String BLACK_FONT_STYLE_CLASS = "blackFont";

    private TilePane showMonthPane;
    private Node selectedLabel;
    private DateSelector dateSelector;
    private Popup popup;

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded(){
        lastMonth.setOnMouseClicked(event -> beforeDate());
        nextMonth.setOnMouseClicked(event -> laterDate());
        today.setOnMouseClicked(event -> {
            LocalDate nowLocalDate = LocalDate.now();
            if (Objects.equals(nowLocalDate, getLocalDate())){
//                可能不在当前日期面板，需要跳转
                skipToPointDate(nowLocalDate);
            }else {
                setLocalDate(nowLocalDate);
            }
        });
        clear.setOnMouseClicked(mouseEvent -> setLocalDate(null));
        initDateSelectorPopup();
        addDatePropertyListener();
        LocalDate now = LocalDate.now();
        showMonthPane = buildMonthPane(now);
//        建造完月份pane后跳转到指定天
//        skipToPointDate(now);
    }

    private void initDateSelectorPopup(){
        popup = new Popup();
        dateSelector = new DateSelector();
        date = dateSelector.dateProperty();
        dateMsg.setText(SHORT_DATE_FORMATTER.format(LocalDate.now()));
        popup.getContent().add(dateSelector);
        popup.setAutoHide(true);
        popup.setOnHidden(event -> {
            monthPane.setVisible(true);
            icoBox.setVisible(true);
            bottomMsg.setVisible(true);
        });
        dateMsg.setOnMouseClicked(event -> {
            Bounds bounds = this.localToScreen(this.getBoundsInLocal());
            popup.setAnchorX(bounds.getMaxX() - bounds.getWidth() + 33);
            popup.setAnchorY(bounds.getMaxY() - bounds.getHeight() + 50);
            monthPane.setVisible(false);
            icoBox.setVisible(false);
            bottomMsg.setVisible(false);
            popup.show(this.getScene().getWindow());
            FADE.play(dateSelector, 0.5D, 1D, Duration.millis(200));
        });
    }
    private void addDatePropertyListener(){
        date.addListener((observable, oldDate, newDate) -> {
            popup.hide();
            skipToPointDate(newDate);
        });
    }

    /**
     * 跳转到指定日期
     * @param newDate
     */
    private void skipToPointDate(LocalDate newDate){
        if (newDate == null){
            if (selectedLabel != null){
                selectedLabel.getStyleClass().remove(SELECTED_DAY_LABEL_STYLE_CLASS);
            }
            return;
        }
        if (Integer.parseInt(dateMsg.getText().substring(0, 4)) * 100 + Integer.parseInt(dateMsg.getText().substring(5, 7)) == newDate.getYear() * 100 + newDate.getMonthValue()){
            if (selectedLabel != null){
                selectedLabel.getStyleClass().remove(SELECTED_DAY_LABEL_STYLE_CLASS);
            }
            (selectedLabel = findPointDay(newDate)).getStyleClass().add(SELECTED_DAY_LABEL_STYLE_CLASS);
        }else if (Integer.parseInt(dateMsg.getText().substring(0, 4)) * 100 + Integer.parseInt(dateMsg.getText().substring(5, 7)) < newDate.getYear() * 100 + newDate.getMonthValue()){
            laterDate(newDate);
        }else {
            beforeDate(newDate);
        }
    }

    /**
     * 向月份面板中添加天
     * @param tilePane
     * @return
     */
    private Label addDay(TilePane tilePane, LocalDate selfDate){
        Label label = new Label(String.valueOf(selfDate.getDayOfMonth()));
        label.getStyleClass().add(DAY_LABEL_STYLE_CLASS);
        label.setOnMouseClicked(event -> setLocalDate(selfDate));
        tilePane.getChildren().add(label);
        return label;
    }

    /**
     * 时间流逝
     * @param laterDate
     */
    private void laterDate(LocalDate laterDate){
        icoBox.setDisable(true);
        if (laterDate.getYear() > 9999){
            laterDate = laterDate.withYear(1);
        }
        dateMsg.setText(SHORT_DATE_FORMATTER.format(laterDate));
        showMonthPane = buildMonthPane(laterDate);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Timeline timeline = ScrollUtil.buildSlideTimeLine(monthPaneScroll.getVvalue(), 1D, monthPaneScroll);
                timeline.setOnFinished(event -> {
                    Platform.runLater(() -> monthsPane.getChildren().remove(0));
                    icoBox.setDisable(false);
                });
                timeline.play();
                if (selectedLabel != null){
                    try {
                        selectedLabel.getStyleClass().remove(SELECTED_DAY_LABEL_STYLE_CLASS);
                    }catch (NullPointerException e){
                        log.warn("方向键按太快导致，不影响正常使用", e);
                    }
                }
                if ((selectedLabel = findPointDay()) != null){
                    selectedLabel.getStyleClass().add(SELECTED_DAY_LABEL_STYLE_CLASS);
                }
            }
        }, 50);
    }

    private void laterDate(){
        LocalDate tempDate = LocalDate.from(DateTimeFormatter.ofPattern("yyyy年MM月dd").parse(dateMsg.getText() + "01"));
        if (tempDate.getMonthValue() < 12){
            tempDate = tempDate.plusMonths(1);
        }else {
            tempDate = tempDate.plusYears(1).withMonth(1);
        }
        laterDate(tempDate);
    }

    /**
     * 时间回溯
     * @param beforeDate
     */
    private void beforeDate(LocalDate beforeDate){
        icoBox.setDisable(true);
        if (beforeDate.getYear() <= 0){
            beforeDate = beforeDate.withYear(9999);
        }
        dateMsg.setText(SHORT_DATE_FORMATTER.format(beforeDate));
        TilePane oldMonthPane = showMonthPane;
        showMonthPane = buildMonthPane(beforeDate);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                double distance = 158.5D;
                ParallelTransition parallelTransition = new ParallelTransition(
                        SLIDE_Y.get(oldMonthPane, 0, distance, Duration.millis(253)),
                        SLIDE_Y.get(showMonthPane, -distance * 2, -distance, Duration.millis(253))
                );
                parallelTransition.setOnFinished(event -> {
                    monthsPane.getChildren().remove(0);
                    showMonthPane.setTranslateY(0);
                    icoBox.setDisable(false);
                });
                parallelTransition.play();
                if (selectedLabel != null){
                    selectedLabel.getStyleClass().remove(SELECTED_DAY_LABEL_STYLE_CLASS);
                }
                if ((selectedLabel = findPointDay()) != null){
                    selectedLabel.getStyleClass().add(SELECTED_DAY_LABEL_STYLE_CLASS);
                }
            }
        }, 50);
    }

    private void beforeDate(){
        LocalDate tempDate = LocalDate.from(DateTimeFormatter.ofPattern("yyyy年MM月dd").parse(dateMsg.getText() + "01"));
        if (tempDate.getMonthValue() > 1){
            tempDate = tempDate.minusMonths(1);
        }else {
            tempDate = tempDate.minusYears(1).withMonth(12);
        }
        beforeDate(tempDate);
    }

    /**
     * 从展示的月份面板中寻找指定的日期
     */
    private Node findPointDay(LocalDate pointDate){
        if (pointDate == null){
            return null;
        }
        int showPaneYear = Integer.parseInt(dateMsg.getText().substring(0, 4));
        int showPaneMonth = Integer.parseInt(dateMsg.getText().substring(5, 7));
        ObservableList<Node> showPaneChildren = showMonthPane.getChildren();
        for (int i = 1; i <= showPaneChildren.size(); i++) {
            if (i >= pointDate.getDayOfMonth()
                    && pointDate.getYear() == showPaneYear
                    && pointDate.getMonthValue() == showPaneMonth
                    && Integer.parseInt(((Label)showPaneChildren.get(i - 1)).getText()) == pointDate.getDayOfMonth()){
                return showPaneChildren.get(i - 1);
            }
        }
        return new Label();
    }

    private Node findPointDay(){
        return findPointDay(getLocalDate());
    }

    /**
     * 创建月份面板
     * @param buildDate
     * @return
     */
    private TilePane buildMonthPane(LocalDate buildDate){
        TilePane tilePane = new TilePane();
        int lastMonthDays = buildDate.withDayOfMonth(1).getDayOfWeek().getValue() - 1;
        LocalDate tempDate;
//            添加上月剩余天数
        if (lastMonthDays != 0){
            int lastMonthMaxDay = DateSelector.calcMaxDayForMonth(buildDate.minusMonths(1));
            tempDate = buildDate.minusMonths(1);
            if (tempDate.getYear() <= 0){
                tempDate = tempDate.withYear(9999);
            }
            for (int i = lastMonthMaxDay - lastMonthDays + 1; i <= lastMonthMaxDay; i++) {
                addDay(tilePane, tempDate.withDayOfMonth(i));
            }
        }
//            添加当月所有天数
        int currentMonthMaxDay = DateSelector.calcMaxDayForMonth(buildDate);
        for (int i = 1; i <= currentMonthMaxDay; i++) {
            addDay(tilePane, buildDate.withDayOfMonth(i)).getStyleClass().add(BLACK_FONT_STYLE_CLASS);
        }
//            添加下月剩余天数
        tempDate = buildDate.plusMonths(1);
        if (tempDate.getYear() > 9999){
            tempDate = tempDate.withYear(1);
        }
        for (int i = 1; i <= 42 - lastMonthDays - currentMonthMaxDay; i++){
            addDay(tilePane, tempDate.withDayOfMonth(i));
        }
        tilePane.getStyleClass().add(DAY_PANE_STYLE_CLASS);
        monthsPane.getChildren().add(tilePane);
        return tilePane;
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public Predicate<LocalDate> getInterceptor() {
        return this.dateSelector.getInterceptor();
    }

    @Override
    public ObjectProperty<Predicate<LocalDate>> interceptorProperty() {
        return this.dateInterceptor;
    }

    @Override
    public void setInterceptor(Predicate<LocalDate> dateInterceptor) {
        this.dateSelector.setInterceptor(dateInterceptor);
    }

}
