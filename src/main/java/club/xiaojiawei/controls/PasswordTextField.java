package club.xiaojiawei.controls;

import club.xiaojiawei.skin.PasswordTextFieldSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;

/**
 * 密码输入框
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 14:25
 */
public class PasswordTextField extends PasswordField {

    /**
     * 是否永远隐藏（是否显示眼睛图标）
     */
    private final BooleanProperty hideForever = new SimpleBooleanProperty();

    public boolean isHideForever() {
        return hideForever.get();
    }

    public BooleanProperty hideForeverProperty() {
        return hideForever;
    }

    public void setHideForever(boolean hideForever) {
        this.hideForever.set(hideForever);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new PasswordTextFieldSkin(this);
    }

}