package club.xiaojiawei.controls;

import club.xiaojiawei.skin.FilterComboBoxListViewSkin;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;
import lombok.Getter;
import lombok.Setter;

/**
 * 带有搜索框的ComboBox
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:48
 */
@Setter
@Getter
public class FilterComboBox<T> extends ComboBox<T> {

    private boolean ignoreCase;

    public FilterComboBox() {
        super();
    }

    public FilterComboBox(ObservableList<T> items) {
        super(items);
    }

    public FilterComboBox(ObservableList<T> items, boolean ignoreCase) {
        super(items);
        this.ignoreCase = ignoreCase;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new FilterComboBoxListViewSkin<>(this);
    }

}
