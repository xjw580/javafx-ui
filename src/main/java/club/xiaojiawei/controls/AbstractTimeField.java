package club.xiaojiawei.controls;

import club.xiaojiawei.func.Interceptor;
import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import static club.xiaojiawei.enums.BaseTransitionEnum.FADE;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/8 9:56
 */
public abstract class AbstractTimeField<T> extends Group implements Interceptor<T> {

    public AbstractTimeField() {
        loadPage();
        afterPageLoaded();
        initPopup();
        Node iconNode = createIconNode();
        iconNode.setOnMouseClicked(event -> {
            Bounds bounds = this.localToScreen(this.getBoundsInLocal());
            showPopup(this.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY() - 10);
        });
    }

    /**
     * 默认显示时间选择器图标
     */
    @Getter
    @Setter
    private boolean showIcon = true;
    /**
     * 默认显示背景
     */
    @Getter
    @Setter
    private boolean showBg = true;
    /**
     * 文本框是否有焦点
     */
    private final BooleanProperty focusedField = new SimpleBooleanProperty();
    /**
     * 点击图标展示的弹出页
     */
    private Popup popup;

    public boolean isFocusedField() {
        return focusedField.get();
    }

    protected void setFocusedField(boolean focusedField) {
        this.focusedField.set(focusedField);
    }

    public ReadOnlyBooleanProperty focusedReadOnlyProperty() {
        ReadOnlyBooleanWrapper booleanWrapper = new ReadOnlyBooleanWrapper();
        booleanWrapper.bind(focusedField);
        return booleanWrapper.getReadOnlyProperty();
    }

    protected void initPopup() {
        this.popup = createPopup();
    }

    protected Popup getPopup() {
        return popup;
    }

    public void showPopup(Window window, double anchorX, double anchorY){
        if (anchorX >= 0){
            getPopup().setX(anchorX);
        }
        if (anchorY >= 0){
            getPopup().setY(anchorY);
        }
        getPopup().show(window);
        FADE.play(getPopup().getContent().get(0), 0.5D, 1D, Duration.millis(200));
    }

    public void hidePopup(){
        popup.hide();
    }

    protected int parseInt(String s){
        if (s == null || s.isBlank()){
            return 0;
        }
        return Integer.parseInt(s);
    }

    /**
     * 刷新显示的时间，和真正存储的时间同步
     */
    public abstract void refresh();
    /**
     * 加载页面
     */
    protected abstract void loadPage();
    /**
     * 页面加载完毕
     */
    protected abstract void afterPageLoaded();
    /**
     * 创建弹窗页，点击组件图标后弹出
     * @return
     */
    protected abstract Popup createPopup();

    /**
     * 创建组件图标
     * @return
     */
    protected abstract Node createIconNode();

}
