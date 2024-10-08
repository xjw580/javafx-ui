package club.xiaojiawei.controls;

import club.xiaojiawei.component.IconTextField;
import club.xiaojiawei.func.FilterAction;
import club.xiaojiawei.skin.FilterFieldSkin;
import club.xiaojiawei.skin.IconTextFieldSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

/**
 * 搜索/过滤框
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/22 15:55
 */
public class FilterField extends IconTextField {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 搜索/过滤事件
     */
    private final ObjectProperty<FilterAction> onFilterAction = new SimpleObjectProperty<>();

    /**
     * 实时过滤
     */
    private final BooleanProperty realTime = new SimpleBooleanProperty();

    public FilterAction getOnFilterAction() {
        return onFilterAction.get();
    }

    public ObjectProperty<FilterAction> onFilterActionProperty() {
        return onFilterAction;
    }

    public void setOnFilterAction(FilterAction onFilterAction) {
        this.onFilterAction.set(onFilterAction);
    }

    public boolean isRealTime() {
        return realTime.get();
    }

    public BooleanProperty realTimeProperty() {
        return realTime;
    }

    public void setRealTime(boolean realTime) {
        this.realTime.set(realTime);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/


    public FilterField() {
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isRealTime() && getOnFilterAction() != null) {
                getOnFilterAction().handle(newValue);
            }
        });
        this.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER && getOnFilterAction() != null && !isRealTime()){
                getOnFilterAction().handle(getText());
                keyEvent.consume();
            }
        });
    }

    @Override
    protected IconTextFieldSkin createDefaultSkin() {
        return new FilterFieldSkin(this);
    }
}
