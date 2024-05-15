package club.xiaojiawei.component;

import club.xiaojiawei.controls.FilterField;
import club.xiaojiawei.controls.TableFilterManagerGroup;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import lombok.Data;

import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/13 15:55
 */
public class TableFilter<S, T> extends AbstractTableFilter<S, T> {

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public TableFilter(TableColumn<S, T> tableColumn, TableFilterManagerGroup<S, T> managerGroup) {
        super(tableColumn, managerGroup);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private FilterField filterField;
    @FXML
    private TableView<Statistics<T>> tableView;
    @FXML
    private TableColumn<Statistics<T>, Statistics<T>> valueColumn;
    @FXML
    private TableColumn<Statistics<T>, Integer> countColumn;
    @FXML
    private CheckBox allCheckBox;
    @FXML
    private WindowBar windowBar;

    private final List<TableFilter.Statistics<T>> hideItems = new ArrayList<>();

    private int selectedCount;

    private final Set<T> selectedValues = new HashSet<>();

    private boolean disableRequestFiltering;

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded() {
        windowBar.setTitle(tableColumn.getText() + "的本地过滤器");
        filterField.setPromptText("输入关键字以过滤值");
        initTableStructure();
        addListener();
    }

    private void addListener(){
        tableView.setOnMouseClicked(event -> {
            Statistics<T> statistics = tableView.getSelectionModel().getSelectedItem();
            if (statistics != null){
                statistics.setSelected(!statistics.isSelected());
            }
        });
        filterField.setOnFilterAction(text -> {
            Iterator<Statistics<T>> hideIterator = hideItems.iterator();
            while (hideIterator.hasNext()){
                Statistics<T> next = hideIterator.next();
                tableView.getItems().add(next);
                hideIterator.remove();
            }
            Iterator<Statistics<T>> showIterator = tableView.getItems().iterator();
            while (showIterator.hasNext()){
                Statistics<T> next = showIterator.next();
                if (!next.getValue().toString().contains(text)){
                    hideItems.add(next);
                    showIterator.remove();
                }
            }
            refresh();
        });
        allCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            disableRequestFiltering = true;
            for (Statistics<T> item : tableView.getItems()) {
                item.setSelected(t1);
            }
            requestFiltering(selectedCount > 0);
            disableRequestFiltering = false;
        });
    }

    @Override
    public UnaryOperator<List<S>> getFilter() {
        return list -> {
            if (selectedCount <= 0){
                return null;
            }
            ArrayList<S> result = new ArrayList<>();
            for (S s : list) {
                if (selectedValues.contains(tableColumn.getCellObservableValue(s).getValue())){
                    result.add(s);
                }
            }
            return result;
        };
    }

    private void initTableStructure(){
        valueColumn.setCellValueFactory(observable -> new SimpleObjectProperty<>(observable.getValue()));
        valueColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Statistics<T>, Statistics<T>> call(TableColumn<Statistics<T>, Statistics<T>> statisticsTTableColumn) {
                TableCell<Statistics<T>, Statistics<T>> cell = new TableCell<>(){
                    @Override
                    protected void updateItem(Statistics<T> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            CheckBox checkBox = new CheckBox();
                            checkBox.getStyleClass().addAll("check-box-ui", "check-box-ui-main");
                            checkBox.selectedProperty().bindBidirectional(item.selectedProperty());

                            HBox space = new HBox();
                            HBox.setHgrow(space, Priority.ALWAYS);

                            Label label = new Label(item.getValue().toString());

                            HBox rootHBox = new HBox(checkBox, space, label);
                            rootHBox.setStyle("-fx-padding: 0 5 0 5");
                            rootHBox.setSpacing(5);
                            rootHBox.setAlignment(Pos.CENTER);
                            this.setGraphic(rootHBox);
                        }
                    }
                };
                cell.setStyle("-fx-border-width: 1 0 0 0");
                return cell;
            }
        });
        countColumn.setCellValueFactory(observable -> new SimpleObjectProperty<>(observable.getValue().getCount()));
    }

    @Override
    protected void resetInit(){
        selectedCount = 0;
        filterField.setText("");
        disableRequestFiltering = true;
        for (Statistics<T> item : tableView.getItems()) {
            item.setSelected(false);
        }
        for (Statistics<T> hideItem : hideItems) {
            tableView.getItems().add(hideItem);
            hideItem.setSelected(false);
        }
        disableRequestFiltering = false;
        hideItems.clear();
    }

    @Override
    protected boolean updateTableItems(List<S> newItems) {
//        获取原先被勾选的的item
        ObservableList<Statistics<T>> items = tableView.getItems();
        Set<T> selectedValueSet = new HashSet<>();
        for (Statistics<T> item : items) {
            if (item.isSelected()){
                selectedValueSet.add(item.getValue());
            }
        }
        items.clear();
        selectedValues.clear();
//        1.统计新items的value种类和对应的数量
//        2.根据原先被勾选的的item筛选出新items中的需要被勾选的item
        Map<T, Integer> map = new HashMap<>();
        for (S newItem : newItems) {
            T k = tableColumn.getCellObservableValue(newItem).getValue();
            Integer value = map.getOrDefault(k, 0);
            map.put(k ,++value);
        }
//        将统计出来的结果初始化后插入表中
        int newSelectedCount = 0;
        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            T k = entry.getKey();
            Integer v = entry.getValue();
            Statistics<T> statistics = new Statistics<>(k, v);
//            恢复item勾选
            if (selectedValueSet.contains(k)) {
                statistics.setSelected(true);
                selectedValues.add(statistics.getValue());
                newSelectedCount++;
            }
            statistics.getSelectedProperty().addListener((observableValue, aBoolean, t1) -> {
                if (t1) {
                    selectedCount++;
                    selectedValues.add(statistics.getValue());
                } else {
                    selectedCount--;
                    selectedValues.remove(statistics.getValue());
                }
                if (!disableRequestFiltering){
                    requestFiltering(selectedCount > 0);
                }
            });
            items.add(statistics);
        }
        selectedCount = newSelectedCount;
        return selectedCount > 0;
    }

    @Data
    private static class Statistics<T>{

        private T value;

        private int count;

        private final BooleanProperty selectedProperty = new SimpleBooleanProperty();

        public boolean isSelected() {
            return selectedProperty.get();
        }

        public void setSelected(boolean selected){
            selectedProperty.set(selected);
        }

        public BooleanProperty selectedProperty() {
            return selectedProperty;
        }

        public Statistics(T value, int count) {
            this.value = value;
            this.count = count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Statistics<?> that = (Statistics<?>) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public void refresh() {
        tableView.refresh();
    }

}
