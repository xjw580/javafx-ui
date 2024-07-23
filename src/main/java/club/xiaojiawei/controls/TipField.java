package club.xiaojiawei.controls;

import club.xiaojiawei.func.FilterAction;
import club.xiaojiawei.skin.FilterFieldSkin;
import club.xiaojiawei.skin.TipFieldSkin;
import javafx.beans.property.*;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

/**
 * 搜索/过滤框
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/22 15:55
 */
public class TipField extends TextField {

    private final StringProperty tip = new SimpleStringProperty();

    public String getTip() {
        return tip.get();
    }

    public StringProperty tipProperty() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip.set(tip);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public TipField() {
    }

    public TipField(String tip) {
        setTip(tip);
    }

    public TipField(String text, String tip) {
        super(text);
        setTip(tip);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TipFieldSkin(this);
    }
}
