package club.xiaojiawei.controls;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 14:57
 */
public class NumberField extends TextField {

    public NumberField() {
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TextFieldSkin(this){
        };
    }
}
