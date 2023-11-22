package club.xiaojiawei.controls;

import club.xiaojiawei.utils.ScrollUtil;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 年月选择器
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/24 23:15
 */
public class DateSelector extends HBox {

    /**
     * 日期：包含年月,格式：yyyy/MM/dd
     */
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    public String getDate() {
        return SHORT_DATE_FORMATTER.format(date.get());
    }
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }
    /**
     * @param date 格式：yyyy/MM/dd 或 yyyy/MM
     */
    public void setDate(String date) {
        Objects.requireNonNull(date, "date");
        if (date.length() == 6){
            this.date.set(LocalDate.from(DATE_FORMATTER.parse(date)));
        }else {
            this.date.set(LocalDate.from(SHORT_DATE_FORMATTER.parse(date)));
        }
    }
    public void setLocalDate(LocalDate localDate){
        date.set(localDate);
    }
    @FXML
    private Accordion yearsPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ScrollBar scrollBar;
    private Label selectedLabel;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM");
    private static final String SELECTED_LABEL = "selectedLabel";
    private boolean allowExec = true;
    private final static String TITLED_PANE_UI = "titled-pane-ui";
    public DateSelector() {
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
        loadYearPane(LocalDate.now());
        date.addListener((observable, oldDate, newDate) -> {
            yearsPane.getPanes().clear();
            selectedLabel = null;
            TitledPane titledPane = loadYearPane(newDate);
//        解决错位以及pane高度异常问题
            if (titledPane != null){
                titledPane.setExpanded(false);
                titledPane.setExpanded(true);
            }
        });
        initScrollBar();
    }
    /**
     * 加载年面板
     * @param nowDate
     * @return
     */
    private TitledPane loadYearPane(LocalDate nowDate){
        yearsPane.setOnScroll(event -> {
            if (event.getDeltaY() > 0){
//                往上滑
                if (scrollPane.getVvalue() == 0D && Integer.parseInt(yearsPane.getPanes().get(0).getText()) > 0){
                    yearsPane.getPanes().add(0, buildYearTitledPane(nowDate, Integer.parseInt(yearsPane.getPanes().get(0).getText()) - 1));
                }
            }else if (event.getDeltaY() < 0 && Integer.parseInt(yearsPane.getPanes().get(0).getText()) < 9999){
//                往下滑
                if (scrollPane.getVvalue() == 1D){
                    yearsPane.getPanes().add(buildYearTitledPane(nowDate));
                }
            }
        });
        int endYear = nowDate.getYear() + 10;
        TitledPane expandedPane = null;
        for (int y = nowDate.getYear(); y < endYear; y++) {
            TitledPane titledPane = buildYearTitledPane(nowDate, y);
            if (y == nowDate.getYear()){
                yearsPane.setExpandedPane(expandedPane = titledPane);
            }
            yearsPane.getPanes().add(titledPane);
        }
        yearsPane.prefWidthProperty().bind(this.widthProperty().subtract(18));
        scrollPane.prefWidthProperty().bind(this.widthProperty().subtract(18));
        return expandedPane;
    }

    /**
     * 初始化侧边的滚动条
     */
    private void initScrollBar(){
        scrollBar.setOnScroll(event -> {
            if (event.getDeltaY() > 0){
                yearsPane.fireEvent(event);
            }else if (event.getDeltaY() < 0){
                yearsPane.fireEvent(event);
            }
            scrollBar.setValue(50D);
        });
        scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!allowExec){
                allowExec = true;
                return;
            }
            if (newValue.doubleValue() > oldValue.doubleValue()){
                allowExec = false;
                yearsPane.fireEvent(ScrollUtil.ofNewEvent(-10D));
            }else if (newValue.doubleValue() < oldValue.doubleValue()){
                allowExec = false;
                yearsPane.fireEvent(ScrollUtil.ofNewEvent(10D));
            }
            scrollBar.setValue(50D);
        });
    }

    /**
     * 计算指定日期的月份的最大天
     * @param date
     * @return
     */
    public static int calcMaxDayForMonth(LocalDate date){
        int days = 30;
        int month = date.getMonth().getValue();
        if (month == 2){
            if (date.getYear() % 4 == 0 && (date.getYear() % 100 != 0 || date.getYear() % 400 == 0)){
                days = 29;
            }else {
                days = 28;
            }
        }else if ((month < 8 && (month & 1) == 1) || (month >= 8 && (month & 1) == 0)){
            days = 31;
        }
        return days;
    }

    /**
     * 计算指定日期的月份的最大天
     * @param year
     * @param month
     * @return
     */
    public static int calcMaxDayForMonth(int year, int month){
        int days = 30;
        if (month == 2){
            if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)){
                days = 29;
            }else {
                days = 28;
            }
        }else if ((month < 8 && (month & 1) == 1) || (month >= 8 && (month & 1) == 0)){
            days = 31;
        }
        return days;
    }

    /**
     * 创建年面板
     * @param nowDate
     * @return
     */
    private TitledPane buildYearTitledPane(LocalDate nowDate){
        return buildYearTitledPane(nowDate, Integer.parseInt(yearsPane.getPanes().get(yearsPane.getPanes().size() - 1).getText()) + 1);
    }

    /**
     * 创建年面板
     * @param nowDate
     * @param buildYear 需要创建哪一年的年面板
     * @return
     */
    private TitledPane buildYearTitledPane(LocalDate nowDate, int buildYear){
        TitledPane newYearPane = new TitledPane();
        newYearPane.getStyleClass().add(TITLED_PANE_UI);
        newYearPane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                ObservableList<TitledPane> panes = this.yearsPane.getPanes();
                int yearIndex = 0;
                for (; yearIndex < panes.size(); yearIndex++) {
                    if (panes.get(yearIndex) == newYearPane) {
                        break;
                    }
                }
                final double singleHeight = 22.2D;
                double showCount = this.getHeight() / singleHeight;
//                确保年面板在ScrollPane中不能全部显示
                for (int j = panes.size() - yearIndex; j < showCount; j++) {
                    panes.add(buildYearTitledPane(nowDate));
                }
                if (yearIndex != 0){
                    int finalYearIndex = yearIndex;
//                    延迟等待新加的pane渲染完毕
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
//                            面板展开时高度相当于4.55个未展开面板高度
                            Timeline timeline = ScrollUtil.buildSlideTimeLine(finalYearIndex, 3.55D + panes.size(), showCount, scrollPane);
                            timeline.setOnFinished(event -> {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        Platform.runLater(() -> {
                                            panes.remove(0, finalYearIndex);
                                            scrollPane.setVvalue(0);
                                        });
                                    }
                                }, 100);
                            });
                            timeline.play();
                        }
                    }, 100);
                }
            }
        });
        newYearPane.setText(String.valueOf(buildYear));
        newYearPane.setContent(buildMonthTilePane(nowDate, buildYear));
        return newYearPane;
    }
    /**
     * 创建月面板
     * @param nowDate
     * @param buildYear 需要创建哪一年的月面板
     * @return
     */
    private TilePane buildMonthTilePane(LocalDate nowDate, int buildYear){
        TilePane newMonthPane = new TilePane();
        newMonthPane.setAlignment(Pos.CENTER);
        for (int m = 1; m <= 12; m++) {
            Label label = new Label();
            if (nowDate.getYear() == buildYear && nowDate.getMonthValue() == m){
                (selectedLabel = label).getStyleClass().add(SELECTED_LABEL);
            }
            label.setText(String.valueOf(m));
            int finalM = m;
            label.setOnMouseClicked(event -> {
                selectedLabel.getStyleClass().remove(SELECTED_LABEL);
                (selectedLabel = label).getStyleClass().add(SELECTED_LABEL);
                date.set(LocalDate.of(buildYear, Month.of(finalM), Math.min((date.get() == null? LocalDate.now().getDayOfMonth() : date.get().getDayOfMonth()), calcMaxDayForMonth(LocalDate.of(buildYear, finalM, 1)))));
            });
            newMonthPane.getChildren().add(label);
        }
        return newMonthPane;
    }

}
