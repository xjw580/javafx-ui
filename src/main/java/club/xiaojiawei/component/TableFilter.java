package club.xiaojiawei.component;

import club.xiaojiawei.controls.FilterField;
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

    public TableFilter(ObservableList<S> items, TableColumn<S, T> tableColumn) {
        super(items, tableColumn);
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

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded() {
        windowBar.setTitle(outerTableColumn.getText() + "的本地过滤器");
        filterField.setPromptText("输入关键字以过滤值");
        initTableStructure();
        updateTableItems();
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
            for (Statistics<T> item : tableView.getItems()) {
                item.setSelected(t1);
            }
        });
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
    protected void reset(){
        filterField.setText("");
        hideItems.clear();
        super.reset();
    }

    @Override
    protected void updateTableItems() {
        Map<T, Integer> map = new HashMap<>();
        for (int i = 0; i < outerTableItems.size(); i++) {
            T k = outerTableColumn.getCellObservableValue(i).getValue();
            Integer value = map.getOrDefault(k, 0);
            map.put(k ,++value);
        }
        ObservableList<Statistics<T>> items = tableView.getItems();
        items.clear();
        map.forEach((k, v) -> {
            Statistics<T> statistics = new Statistics<>(k, v);
            statistics.getSelectedProperty().addListener((observableValue, aBoolean, t1) -> {
                ArrayList<S> list = new ArrayList<>();
                for (S item : this.outerTableItems) {
                    if (Objects.equals(outerTableColumn.getCellObservableValue(item).getValue(), statistics.getValue())){
                        list.add(item);
                    }
                }
                if (t1){
                    selectedCount.set(selectedCount.get() + 1);
                    showItems.addAll(list);
                }else {
                    selectedCount.set(selectedCount.get() - 1);
                    showItems.removeAll(list);
                }
                attemptStopFilter();
            });
            items.add(statistics);
        });
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
