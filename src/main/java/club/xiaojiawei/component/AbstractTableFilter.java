package club.xiaojiawei.component;

import club.xiaojiawei.controls.TableFilterManager;
import club.xiaojiawei.controls.TableFilterManagerGroup;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * 表格过滤器
 * {@link TableFilterManager}
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/10 17:54
 */
abstract public class AbstractTableFilter<S, T> extends VBox {

    /**
     * 需要过滤的表格列
     */
    protected final TableColumn<S, T> tableColumn;

    private final TableFilterManagerGroup<S, T> managerGroup;

    public AbstractTableFilter(TableColumn<S, T> tableColumn, TableFilterManagerGroup<S, T> managerGroup) {
        setStyle("""
                    -fx-background-color: #F7F8FAFF;
                    -fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 0, 0);
                    -fx-background-radius: 5;
                """);
        this.tableColumn = tableColumn;
        this.managerGroup = managerGroup;
    }

    /**
     * 是否在过滤
     */
    private final ReadOnlyBooleanWrapper isFiltering = new ReadOnlyBooleanWrapper();

    public boolean isIsFiltering() {
        return isFiltering.get();
    }

    public ReadOnlyBooleanProperty isFilteringReadOnlyProperty() {
        return isFiltering.getReadOnlyProperty();
    }

    /**
     * 请求过滤
     */
    protected final void requestFiltering(boolean isFiltering){
        this.isFiltering.set(isFiltering);
        managerGroup.filtration(this);
    }

    abstract protected void resetInit();

    /**
     * table的items发生改成时自动调用此方法，供子类重写
     */
    abstract protected boolean updateTableItems(List<S> newItems);

    /**
     * 获取过滤器
     * @return List 为null表示没有过滤，否则表示过滤结果
     */
    abstract public UnaryOperator<List<S>> getFilter();
    /**
     * 刷新显示，防止数据和显示不同步的情况
     */
    public void refresh(){}

    /**
     * 重置为一开始的模样
     */
    public void reset(){
        resetInit();
        isFiltering.set(false);
        managerGroup.filtration(null);
        refresh();
    }

    public void changeTableItems(List<S> newItems){
        isFiltering.set(updateTableItems(newItems));
        refresh();
    }

}
