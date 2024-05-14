package club.xiaojiawei.controls;

import club.xiaojiawei.annotations.OnlyOnce;
import club.xiaojiawei.component.AbstractTableFilter;
import club.xiaojiawei.component.TableFilter;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;

import java.io.IOException;

/**
 * 表格字符串过滤器，T最好是String类型，或者其toString方法返回的字符串和表格中显示的字符串是一样的
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/10 17:54
 */
public class TableFilterManager<S, T> extends StackPane {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 是否处于过滤状态
     */
    private final ReadOnlyObjectWrapper<Boolean> isFilter = new ReadOnlyObjectWrapper<>();

    private TableColumn<S, T> outerTableColumn;

    private TableView<S> outerTableView;

    public TableColumn<S, T> getTableColumn() {
        return outerTableColumn;
    }

    @OnlyOnce
    public void setTableColumn(TableColumn<S, T> tableColumn) {
        if (outerTableColumn == null){
            outerTableColumn = tableColumn;
        }
    }

    public TableView<S> getTableView() {
        return outerTableView;
    }

    @OnlyOnce
    public void setTableView(TableView<S> tableView) {
        if (outerTableView == null){
            outerTableView = tableView;
        }
    }

    public Boolean getIsFilter() {
        return isFilter.get();
    }

    public ReadOnlyObjectProperty<Boolean> isFilterProperty() {
        return isFilter.getReadOnlyProperty();
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public TableFilterManager() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(TableFilterManager.class.getResource(TableFilterManager.class.getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected Button filterBtn;

    private Popup popup;

    private AbstractTableFilter<S, T> tableFilter;

    /**
     * 是否纯为外部tableView的item发生改变，而不是因内部过滤导致的外部改变
     */
    private boolean isOuterChange = true;

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded() {
        addListener();
    }

    private void addListener(){
        filterBtn.setOnAction(actionEvent -> {
            if (outerTableColumn == null || outerTableView == null){
                return;
            }
            if (popup == null){
                popup = getPopup();
            }
            popup.show(outerTableView.getScene().getWindow());
        });
    }

    private Popup getPopup(){
        ObservableList<S> copyItems = FXCollections.observableArrayList(outerTableView.getItems());
        outerTableView.getItems().addListener((ListChangeListener<? super S>) change -> {
            if (isOuterChange && change.next() && !change.wasPermutated()){
                copyItems.setAll(outerTableView.getItems());
            }
        });
        String filteringStyleClass = "filteringBtn";
        tableFilter = getTableFilter(copyItems, outerTableColumn);
        tableFilter.setFilterCallback(list -> {
            isOuterChange = false;
            filterBtn.getStyleClass().remove(filteringStyleClass);
            if (list == null){
                isFilter.set(false);
                outerTableView.getItems().setAll(copyItems);
            }else {
                isFilter.set(true);
                filterBtn.getStyleClass().add(filteringStyleClass);
                outerTableView.getItems().setAll(list);
            }
            outerTableView.refresh();
            isOuterChange = true;
        });

        Popup newPopup = new Popup();
        newPopup.getContent().add(tableFilter);
        newPopup.setAutoHide(true);
        Bounds bounds = this.localToScreen(this.getBoundsInLocal());
        newPopup.setX(bounds.getMinX() + 5);
        newPopup.setY(bounds.getMinY() + 10);
        return newPopup;
    }

    protected AbstractTableFilter<S, T> getTableFilter(ObservableList<S> copyTableItems, TableColumn<S, T> tableColumn){
        return new TableFilter<>(copyTableItems, tableColumn);
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public void refresh(){
        if (tableFilter != null){
            tableFilter.refresh();
        }
    }

}
