package club.xiaojiawei.controls;

import club.xiaojiawei.component.AbstractTableFilter;
import club.xiaojiawei.component.TableCheckFilter;
import club.xiaojiawei.component.TableDateFilter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumn;

/**
 * 表格时间过滤管理器，T最好是String类型，或者其toString方法返回的字符串和表格中显示的字符串是一样的
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/11 18:12
 */
public class TableCheckFilterManager<S, T> extends TableFilterManager<S, T> {

    @Override
    protected AbstractTableFilter<S, T> getTableFilter(TableColumn<S, T> tableColumn, TableFilterManagerGroup<S, T> group) {
        return new TableCheckFilter<>(tableColumn, group);
    }

}
