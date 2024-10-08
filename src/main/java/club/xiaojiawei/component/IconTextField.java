package club.xiaojiawei.component;

import club.xiaojiawei.skin.IconTextFieldSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;

/**
 * @author 肖嘉威
 * @date 2024/10/8 9:31
 */
public abstract class IconTextField extends TextField {

    private final BooleanProperty hideIcon = new SimpleBooleanProperty(false);

    public boolean isHideIcon() {
        return hideIcon.get();
    }

    public void setHideIcon(boolean hideIcon) {
        this.hideIcon.set(hideIcon);
    }

    public BooleanProperty hideIconProperty() {
        return hideIcon;
    }

    public IconTextField() {
        skinProperty().addListener((observableValue, skin, t1) -> {
            if (t1 instanceof IconTextFieldSkin iconTextFieldSkin) {
                iconTextFieldSkin.hideIconProperty().bindBidirectional(hideIcon);
            }
        });
    }

    @Override
    abstract protected IconTextFieldSkin createDefaultSkin();

}
