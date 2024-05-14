package club.xiaojiawei.component;

import club.xiaojiawei.controls.Date;
import club.xiaojiawei.controls.DateSelector;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/13 10:50
 */
public class TableDateFilter<S, T> extends AbstractTableFilter<S, T> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 日期格式化的格式
     */
    private final StringProperty dateFormat = new SimpleStringProperty(DateSelector.DATE_FORMATTER_STRING);

    public String getDateFormat() {
        return dateFormat.get();
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat.set(dateFormat);
    }

    public StringProperty dateFormatProperty() {
        return dateFormat;
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public TableDateFilter(ObservableList<S> items, TableColumn<S, T> tableColumn) {
        super(items, tableColumn);
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
    private ToggleGroup radioBtnGroup;
    @FXML
    private RadioButton allRadio;
    @FXML
    private RadioButton todayRadio;
    @FXML
    private RadioButton oneWeekRadio;
    @FXML
    private RadioButton oneMonthRadio;
    @FXML
    private RadioButton customRadio;
    @FXML
    private Date startCustomTime;
    @FXML
    private Date endCustomTime;
    @FXML
    private WindowBar windowBar;
    @FXML
    private VBox confirmCustomTimePane;

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded() {
        addListener();
        windowBar.setTitle(outerTableColumn.getText() + "的本地过滤器");
    }

    private void addListener(){
        startCustomTime.readOnlyDateProperty().addListener((observableValue, localDate, t1) -> {
            if (customRadio.isSelected()) {
                confirmCustomTimePane.setManaged(true);
                confirmCustomTimePane.setVisible(true);
            }
        });
        endCustomTime.readOnlyDateProperty().addListener((observableValue, localDate, t1) -> {
            if (customRadio.isSelected()) {
                confirmCustomTimePane.setManaged(true);
                confirmCustomTimePane.setVisible(true);
            }
        });

        radioBtnGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) -> {
            LocalDate today = LocalDate.now();
            LocalDate end = today;
            LocalDate start = null;
            selectedCount.set(1);
            if (t1 == customRadio){
                startCustomTime.setDisable(false);
                endCustomTime.setDisable(false);
                start = startCustomTime.getLocalDate();
                end = endCustomTime.getLocalDate();
                if (start == null && end == null){
                    selectedCount.set(0);
                }
            }else {
                confirmCustomTimePane.setVisible(false);
                confirmCustomTimePane.setManaged(false);
                startCustomTime.setDisable(true);
                endCustomTime.setDisable(true);
                if (t1 == allRadio){
                    selectedCount.set(0);
                }else if (t1 == todayRadio){
                    start = today;
                }else if (t1 == oneWeekRadio){
                    start = today.minusWeeks(1);
                }else if (t1 == oneMonthRadio){
                    start = today.minusMonths(1);
                }
            }
            filterTime(start, end);
        });
    }

    private void filterTime(LocalDate start, LocalDate end){
        if (attemptStopFilter()){
            return;
        }
        start = Objects.requireNonNullElse(start, LocalDate.MIN);
        end = Objects.requireNonNullElse(end, LocalDate.MAX);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat.get());
        ArrayList<S> list = new ArrayList<>();
        for (S tableItem : outerTableItems) {
            String value = outerTableColumn.getCellObservableValue(tableItem).getValue().toString();
            LocalDate localDate = LocalDate.from(formatter.parse(value));
            if (!localDate.isBefore(start) && !localDate.isAfter(end)){
                list.add(tableItem);
            }
        }
        showItems.setAll(list);
    }

    @FXML
    protected void confirmCustomTime(){
        LocalDate start = startCustomTime.getLocalDate();
        LocalDate end = endCustomTime.getLocalDate();
        if (start == null && end == null){
            selectedCount.set(0);
        }else {
            selectedCount.set(1);
        }
        filterTime(start, end);
        confirmCustomTimePane.setManaged(false);
        confirmCustomTimePane.setVisible(false);
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public void refresh() {
        allRadio.setSelected(true);
    }

}
