package club.xiaojiawei.controls;

import club.xiaojiawei.component.AbstractTableFilter;
import club.xiaojiawei.component.TableDateFilter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumn;

/**
 * 表格时间过滤管理器，T最好是String类型，或者其toString方法返回的字符串和表格中显示的字符串是一样的
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/11 18:12
 */
public class TableDateFilterManager<S, T> extends AbstractTableFilterManager<S, T> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                     *
     *                                                                         *
     **************************************************************************/

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

    @Override
    protected AbstractTableFilter<S, T> getTableFilter(TableColumn<S, T> tableColumn, TableFilterManagerGroup<S, T> group) {
        TableDateFilter<S, T> filter = new TableDateFilter<>(tableColumn, group);
        filter.dateFormatProperty().bind(dateFormat);
        return filter;
    }

    @Override
    public boolean needHandle(String userData) {
        boolean isNeed = userData != null && userData.startsWith("date");
        if (isNeed) {
            String[] split = userData.split("=", 2);
            if (split.length > 1){
                setDateFormat(split[1]);
            }
        }
        return isNeed;
    }

}
