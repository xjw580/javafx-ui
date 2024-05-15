package club.xiaojiawei.component;

import club.xiaojiawei.controls.Date;
import club.xiaojiawei.controls.DateSelector;
import club.xiaojiawei.controls.TableFilterManagerGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

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

    public TableDateFilter(TableColumn<S, T> tableColumn, TableFilterManagerGroup<S, T> managerGroup) {
        super(tableColumn, managerGroup);
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
        windowBar.setTitle(tableColumn.getText() + "的本地过滤器");
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

        radioBtnGroup.selectedToggleProperty().addListener((observableValue, toggle, newToggle) -> {
            requestFiltering(newToggle != allRadio);
        });
    }

    @Override
    public UnaryOperator<List<S>> getFilter() {
        return list -> {
            Toggle selectedToggle = radioBtnGroup.getSelectedToggle();
            LocalDate today = LocalDate.now();
            LocalDate end = today;
            LocalDate start = null;
            if (selectedToggle == customRadio){
                startCustomTime.setDisable(false);
                endCustomTime.setDisable(false);
                start = startCustomTime.getLocalDate();
                end = endCustomTime.getLocalDate();
            }else {
                confirmCustomTimePane.setVisible(false);
                confirmCustomTimePane.setManaged(false);
                startCustomTime.setDisable(true);
                endCustomTime.setDisable(true);
                if (selectedToggle == allRadio){
                    return null;
                }else if (selectedToggle == todayRadio){
                    start = today;
                }else if (selectedToggle == oneWeekRadio){
                    start = today.minusWeeks(1);
                }else if (selectedToggle == oneMonthRadio){
                    start = today.minusMonths(1);
                }
            }
            return filterTime(start, end, list);
        };
    }

    @Override
    protected void resetInit() {
        radioBtnGroup.selectToggle(allRadio);
    }

    private List<S> filterTime(LocalDate start, LocalDate end, List<S> list){
        start = Objects.requireNonNullElse(start, LocalDate.MIN);
        end = Objects.requireNonNullElse(end, LocalDate.MAX);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat.get());
        ArrayList<S> result = new ArrayList<>();
        for (S tableItem : list) {
            String value = tableColumn.getCellObservableValue(tableItem).getValue().toString();
            LocalDate localDate = LocalDate.from(formatter.parse(value));
            if (!localDate.isBefore(start) && !localDate.isAfter(end)){
                result.add(tableItem);
            }
        }
        return result;
    }

    @FXML
    protected void confirmCustomTime(){
        requestFiltering(radioBtnGroup.getSelectedToggle() != allRadio);
        confirmCustomTimePane.setManaged(false);
        confirmCustomTimePane.setVisible(false);
    }

    @Override
    protected boolean updateTableItems(List<S> newItems) {
        return radioBtnGroup.getSelectedToggle() != allRadio;
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/
}
