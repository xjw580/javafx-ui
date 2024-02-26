package club.xiaojiawei.controls;

import club.xiaojiawei.skin.FilterComboBoxListViewSkin;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:48
 * @todo fix bug：
 * 1. 数据只能增加String类型，不能通过设置cellFactory的方式添加其他类型
 * 2. 必须要界面显示后再select指定item，否则popup里不会有选中效果
 */
public class FilterComboBox<T> extends ComboBox<T> {

    @Override
    protected Skin<?> createDefaultSkin() {
        return new FilterComboBoxListViewSkin<>(this);
    }

}
