package club.xiaojiawei.component;

import club.xiaojiawei.controls.TableFilterManagerGroup;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/13 15:55
 */
public class TableCheckFilter<S, T> extends TableValueFilter<S, T> {

    public TableCheckFilter(TableColumn<S, T> tableColumn, TableFilterManagerGroup<S, T> managerGroup) {
        super(tableColumn, managerGroup);
    }

    @Override
    protected Label createShowGraphic(Statistics<T> item) {
        T value = item.getValue();
        String text = null;
        if (value instanceof Number number) {
            text = number.intValue() > 0? "选中" : "未选中";
        } else if (value instanceof Boolean bool) {
            text = bool? "选中" : "未选中";
        }
        return new Label(text);
    }

    @Override
    protected boolean disableFilter() {
        return true;
    }
}
