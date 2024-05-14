package club.xiaojiawei.component;

import club.xiaojiawei.controls.TableFilterManager;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;

/**
 * 表格过滤器
 * {@link TableFilterManager}
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/10 17:54
 */
abstract public class AbstractTableFilter<S, T> extends VBox {

    protected final ObservableList<S> outerTableItems;

    protected final TableColumn<S, T> outerTableColumn;

    /**
     * 过滤后的结果
     */
    protected final ObservableList<S> showItems = FXCollections.observableArrayList();

    /**
     * 过滤条件数量
     */
    protected final IntegerProperty selectedCount = new SimpleIntegerProperty();

    /**
     * 过滤回调，参数为null表示没有过滤条件，否则表示过滤后的结果
     */
    @Getter
    @Setter
    protected Consumer<List<S>> filterCallback;

    public AbstractTableFilter(ObservableList<S> outerTableItems, TableColumn<S, T> outerTableColumn) {
        setStyle("""
                    -fx-background-color: #F7F8FAFF;
                    -fx-effect: dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 0, 0);;
                    -fx-background-radius: 5;
                """);
        this.outerTableItems = outerTableItems;
        this.outerTableColumn = outerTableColumn;
        addListener();
    }

    private void addListener(){
        outerTableItems.addListener((ListChangeListener<? super S>) change -> tableItemsChanged());
        showItems.addListener((ListChangeListener<? super S>) change -> {
            if (filterCallback != null && selectedCount.get() != 0){
                filterCallback.accept(new ArrayList<>(showItems));
            }
        });
    }

    /**
     * 尝试停止过滤，如果没有过滤条件则能成功停止过滤<br>
     * 需要手动调用!!!，建议每次设置showItems的值时调用
     * @return boolean 是否停止过滤成功
     */
    protected boolean attemptStopFilter(){
        if (filterCallback != null && selectedCount.get() == 0){
            filterCallback.accept(null);
            return true;
        }
        return false;
    }

    /**
     * 重置为一开始的模样
     */
    protected void reset(){
        selectedCount.set(0);
        showItems.clear();
        attemptStopFilter();
        refresh();
    };

    private void tableItemsChanged(){
        updateTableItems();
        reset();
    }

    /**
     * table的items发生改成时自动调用此方法，供子类重写
     */
    protected void updateTableItems(){};

    /**
     * 刷新显示，防止数据和显示不同步的情况
     */
    abstract public void refresh();
}
