package club.xiaojiawei.controls;

import club.xiaojiawei.component.AbstractTableFilter;
import club.xiaojiawei.component.TableValueFilter;
import javafx.scene.control.TableColumn;

/**
 * 表格值过滤管理器，默认过滤器管理器，T最好是String类型，或者其toString方法返回的字符串和表格中显示的字符串是一样的
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/10 17:54
 */
public class TableValueFilterManager<S, T> extends AbstractTableFilterManager<S, T> {

    @Override
    protected AbstractTableFilter<S, T> getTableFilter(TableColumn<S, T> tableColumn, TableFilterManagerGroup<S, T> tableFilterManagerGroup) {
        return new TableValueFilter<>(tableColumn, tableFilterManagerGroup);
    }

    @Override
    public boolean canFilter(String userData) {
        return userData == null || userData.isBlank();
    }

}
