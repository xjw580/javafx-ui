package club.xiaojiawei.controls;

import club.xiaojiawei.annotations.ValidSizeRange;
import club.xiaojiawei.enums.SizeEnum;
import club.xiaojiawei.func.Interceptor;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static club.xiaojiawei.enums.BaseTransitionEnum.FADE;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/8 9:56
 */
@Slf4j
public abstract class AbstractDateTimeField<T> extends Group implements Interceptor<T> {

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
    private final ReadOnlyBooleanWrapper focusedField = new ReadOnlyBooleanWrapper();
    /**
     * 点击图标展示的弹出页
     */
    private Popup popup;
    /**
     * 控件大小
     */
    @Getter
    @ValidSizeRange({SizeEnum.SMALL, SizeEnum.MEDDLE, SizeEnum.DEFAULT, SizeEnum.BIG})
    private SizeEnum size;

    public boolean isFocusedField() {
        return focusedField.get();
    }

    protected void setFocusedField(boolean focusedField) {
        this.focusedField.set(focusedField);
    }

    public ReadOnlyBooleanProperty focusedReadOnlyProperty() {
        return focusedField.getReadOnlyProperty();
    }

    public void setSize(SizeEnum size) {
        this.size = size;
        String smallStyleClass = "time-small-background", bigStyleClass = "time-big-background";
        switch (size){
            case BIG -> {
                dateTimeBg.getStyleClass().removeAll(smallStyleClass, bigStyleClass);
                dateTimeBg.getStyleClass().add(bigStyleClass);
            }
            case MEDDLE, DEFAULT -> dateTimeBg.getStyleClass().removeAll(smallStyleClass, bigStyleClass);
            case SMALL -> {
                dateTimeBg.getStyleClass().removeAll(smallStyleClass, bigStyleClass);
                dateTimeBg.getStyleClass().add(smallStyleClass);
            }
            default -> log.warn("未实现的尺寸: {}", size);
        }
    }

    public AbstractDateTimeField() {
        loadPage();
        afterPageLoaded();
        initPopup();
        Node iconNode = createIconNode();
        iconNode.setOnMouseClicked(event -> {
            Bounds bounds = this.localToScreen(this.getBoundsInLocal());
            showPopup(this.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY() - 10);
        });
    }

    @FXML
    protected Label dateTimeBg;


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
