package club.xiaojiawei.controls;

import club.xiaojiawei.func.FilterAction;
import club.xiaojiawei.skin.FilterFieldSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

/**
 * 搜索/过滤框
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/22 15:55
 */
public class FilterField extends TextField {

    /**
     * 搜索/过滤事件
     */
    private final ObjectProperty<FilterAction> OnFilterAction = new SimpleObjectProperty<>();

    public FilterAction getOnFilterAction() {
        return OnFilterAction.get();
    }

    public ObjectProperty<FilterAction> onFilterActionProperty() {
        return OnFilterAction;
    }

    public void setOnFilterAction(FilterAction onFilterAction) {
        this.OnFilterAction.set(onFilterAction);
    }

    public FilterField() {
        this.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER && getOnFilterAction() != null){
                getOnFilterAction().handle(getText());
                keyEvent.consume();
            }
        });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new FilterFieldSkin(this);
    }
}
