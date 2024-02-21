package club.xiaojiawei.controls;

import club.xiaojiawei.skin.FilterComboBoxListViewSkin;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:48
 */
public class FilterComboBox<T> extends ComboBox<T> {


    @Override
    protected Skin<?> createDefaultSkin() {
        return new FilterComboBoxListViewSkin(this);
    }
}
