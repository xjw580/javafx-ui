package club.xiaojiawei.controls;

import club.xiaojiawei.utils.ScrollUtil;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static club.xiaojiawei.controls.DateSelector.DATE_FORMATTER;
import static club.xiaojiawei.enums.BaseTransitionEnum.FADE;
import static club.xiaojiawei.enums.BaseTransitionEnum.SLIDE_Y;

/**
 * 日历
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 22:54
 */
@SuppressWarnings("unused")
public class Calendar extends VBox {
    /**
     * 日期
     */
    private ObjectProperty<LocalDate> date;
    public String getDate() {
        return DATE_FORMATTER.format(date.get());
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
    private TilePane showMonthPane;
    private static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月");
    private EventHandler<MouseEvent> clearEventHandler;
    private EventHandler<MouseEvent> todayEventHandler;
    private static final String SELECTED_DAY_LABEL = "selectedDayLabel";
    private static final String DAY_LABEL = "dayLabel";
    private static final String DAY_PANE = "dayPane";
    private static final String BLACK_FONT = "blackFont";
    private Node selectedLabel;
    private Popup popup;
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
    private void afterFXMLLoaded(){
        lastMonth.setOnMouseClicked(event -> beforeDate());
        nextMonth.setOnMouseClicked(event -> laterDate());
        initDateSelectorPopup();
        today.setOnMouseClicked(event -> {
            LocalDate nowLocalDate = LocalDate.now();
            if (Objects.equals(nowLocalDate, date.get())){
                skipToPointDate(nowLocalDate);
            }else {
                date.set(nowLocalDate);
            }
        });
        addDatePropertyListener();
        showMonthPane = buildMonthPane(date.get());
    }

    private void initDateSelectorPopup(){
        popup = new Popup();
        DateSelector dateSelector = new DateSelector();
        dateMsg.setText(SHORT_DATE_FORMATTER.format((date = dateSelector.dateProperty()).get()));
        dateSelector.dateProperty().addListener((observable, oldValue, newValue) -> {
            date.set(newValue);
            popup.hide();
        });
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
            dateSelector.setLocalDate(date.get());
            monthPane.setVisible(false);
            icoBox.setVisible(false);
            bottomMsg.setVisible(false);
            popup.show(this.getScene().getWindow());
            FADE.play(dateSelector, 0.5D, 1D, Duration.millis(200));
        });
    }
    private void addDatePropertyListener(){
        date.addListener((observable, oldDate, newDate) -> {
            skipToPointDate(newDate);
        });
    }

    /**
     * 跳转到指定日期
     * @param newDate
     */
    private void skipToPointDate(LocalDate newDate){
        if (Integer.parseInt(dateMsg.getText().substring(0, 4)) * 100 + Integer.parseInt(dateMsg.getText().substring(5, 7)) == newDate.getYear() * 100 + newDate.getMonthValue()){
            if (selectedLabel != null){
                selectedLabel.getStyleClass().remove(SELECTED_DAY_LABEL);
            }
            (selectedLabel = findPointDay(newDate)).getStyleClass().add(SELECTED_DAY_LABEL);
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
        label.getStyleClass().add(DAY_LABEL);
        label.setOnMouseClicked(event -> date.set(selfDate));
        tilePane.getChildren().add(label);
        return label;
    }

    /**
     * 时间流逝
     * @param nextDate
     */
    private void laterDate(LocalDate nextDate){
        icoBox.setDisable(true);
        dateMsg.setText(SHORT_DATE_FORMATTER.format(nextDate));
        showMonthPane = buildMonthPane(nextDate);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ScrollUtil.slide(monthPaneScroll.getVvalue(), 1D, monthPaneScroll, 2L);
                Platform.runLater(() -> monthsPane.getChildren().remove(0));
                icoBox.setDisable(false);
            }
        }, 50);
        if (selectedLabel != null){
            selectedLabel.getStyleClass().remove(SELECTED_DAY_LABEL);
        }
        (selectedLabel = findPointDay()).getStyleClass().add(SELECTED_DAY_LABEL);
    }
    private void laterDate(){
        LocalDate tempDate = LocalDate.from(DateTimeFormatter.ofPattern("yyyy年MM月dd").parse(dateMsg.getText() + "01"));
        if (tempDate.getMonthValue() < 12){
            dateMsg.setText(SHORT_DATE_FORMATTER.format(tempDate = tempDate.plusMonths(1)));
        }else {
            dateMsg.setText(SHORT_DATE_FORMATTER.format(tempDate = tempDate.plusYears(1).withMonth(1)));
        }
        laterDate(tempDate);
    }

    /**
     * 时间回溯
     * @param lastDate
     */
    private void beforeDate(LocalDate lastDate){
        icoBox.setDisable(true);
        dateMsg.setText(SHORT_DATE_FORMATTER.format(lastDate));
        TilePane oldMonthPane = showMonthPane;
        showMonthPane = buildMonthPane(lastDate);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                double distance = 158.5D;
                SLIDE_Y.play(showMonthPane, -distance * 2, -distance, Duration.millis(350));
                SLIDE_Y.play(oldMonthPane, 0, distance, Duration.millis(350));
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> monthsPane.getChildren().remove(0));
                        showMonthPane.setTranslateY(0);
                        icoBox.setDisable(false);
                    }
                }, 400);
            }
        }, 50);
        if (selectedLabel != null){
            selectedLabel.getStyleClass().remove(SELECTED_DAY_LABEL);
        }
        (selectedLabel = findPointDay()).getStyleClass().add(SELECTED_DAY_LABEL);
    }
    private void beforeDate(){
        LocalDate tempDate = LocalDate.from(DateTimeFormatter.ofPattern("yyyy年MM月dd").parse(dateMsg.getText() + "01"));
        if (tempDate.getMonthValue() > 1){
            dateMsg.setText(SHORT_DATE_FORMATTER.format(tempDate = tempDate.minusMonths(1)));
        }else {
            dateMsg.setText(SHORT_DATE_FORMATTER.format(tempDate = tempDate.minusYears(1).withMonth(12)));
        }
        beforeDate(tempDate);
    }

    /**
     * 从展示的月份面板中寻找指定的日期
     */
    private Node findPointDay(LocalDate pointDate){
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
        return findPointDay(date.get());
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
            for (int i = lastMonthMaxDay - lastMonthDays + 1; i <= lastMonthMaxDay; i++) {
                addDay(tilePane, tempDate.withDayOfMonth(i));
            }
        }
//            添加当月所有天数
        int currentMonthMaxDay = DateSelector.calcMaxDayForMonth(buildDate);
        for (int i = 1; i <= currentMonthMaxDay; i++) {
            addDay(tilePane, buildDate.withDayOfMonth(i)).getStyleClass().add(BLACK_FONT);
        }
//            添加下月剩余天数
        tempDate = buildDate.plusMonths(1);
        for (int i = 1; i <= 42 - lastMonthDays - currentMonthMaxDay; i++){
            addDay(tilePane, tempDate.withDayOfMonth(i));
        }
        tilePane.getStyleClass().add(DAY_PANE);
        monthsPane.getChildren().add(tilePane);
        return tilePane;
    }
    public void addClearMouseClickedListener(EventHandler<MouseEvent> eventHandler){
        clear.addEventHandler(MouseEvent.MOUSE_CLICKED, clearEventHandler = eventHandler);
    }
    public void removeClearMouseClickedListener(){
        clear.removeEventHandler(MouseEvent.MOUSE_CLICKED, clearEventHandler);
    }
    public void addTodayMouseClickedListener(EventHandler<MouseEvent> eventHandler){
        today.addEventHandler(MouseEvent.MOUSE_CLICKED, todayEventHandler = eventHandler);
    }
    public void removeTodayMouseClickedListener(){
        today.removeEventHandler(MouseEvent.MOUSE_CLICKED, todayEventHandler);
    }
}
