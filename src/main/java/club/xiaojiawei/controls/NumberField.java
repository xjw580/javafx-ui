package club.xiaojiawei.controls;

import club.xiaojiawei.component.IconTextField;
import club.xiaojiawei.skin.IconTextFieldSkin;
import club.xiaojiawei.skin.NumberFieldSkin;
import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static club.xiaojiawei.config.JavaFXUIThreadPoolConfig.SCHEDULED_POOL;

/**
 * 数字输入框
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 14:57
 */
@Slf4j
@SuppressWarnings("all")
public class NumberField extends IconTextField {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 小数数量
     */
    private int decimalCount = 0;

    private BigDecimal minValue = new BigDecimal("-" + Double.MAX_VALUE);

    private BigDecimal maxValue = new BigDecimal(String.valueOf(Double.MAX_VALUE));

    /**
     * 是否弹出选择器
     */
    private final BooleanProperty popSelector = new SimpleBooleanProperty(false);
    /**
     * 是否强制范围检查（输入时拦截）
     */
    private final BooleanProperty forceRangeCheck = new SimpleBooleanProperty(true);

    private Popup selectorPopup;
    private NumberSelector numberSelector;
    private boolean isUpdating = false;
    private Integer pendingScrollValue;

    public int getDecimalCount() {
        return decimalCount;
    }

    public void setDecimalCount(int decimalCount) {
        this.decimalCount = decimalCount;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = new BigDecimal(minValue);
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = new BigDecimal(maxValue);
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isPopSelector() {
        return popSelector.get();
    }

    public BooleanProperty popSelectorProperty() {
        return popSelector;
    }

    public void setPopSelector(boolean popSelector) {
        this.popSelector.set(popSelector);
    }

    public boolean isForceRangeCheck() {
        return forceRangeCheck.get();
    }

    public BooleanProperty forceRangeCheckProperty() {
        return forceRangeCheck;
    }

    public void setForceRangeCheck(boolean forceRangeCheck) {
        this.forceRangeCheck.set(forceRangeCheck);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public NumberField() {
        setTextFormatter(new TextFormatter<>(change -> {
            if (getText() == null) {
                return change;
            }
            String text = getText().substring(0, change.getRangeStart()) + change.getText() + getText().substring(change.getRangeEnd());
            if (decimalCount > 0
                    && text.matches("^-?\\d*(\\.\\d{0," + decimalCount + "})?$")
                    && isInTheInterval(text)
            ){
                if (text.matches("-0+")){
                    return null;
                }
                return change;
            }else if (decimalCount <= 0
                    && text.matches("^-?\\d*$")
                    && isInTheInterval(text)
            ){
                if (text.matches("-0+")){
                    return null;
                }
                return change;
            }
            return null;
        }));
        addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.UP){
                increment();
            }else if (keyEvent.getCode() == KeyCode.DOWN){
                decrement();
            }
        });
        addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.UP){
                end();
            }
        });
        setOnMouseClicked(event -> {
            if (isPopSelector()) {
                requestFocus();
                showSelectorPopup();
            }
        });
        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (selectorPopup != null) {
                    selectorPopup.hide();
                }
                // 失焦时校验范围并矫正
                if (getText() != null && !getText().isBlank() && !getText().equals("-")) {
                    try {
                        BigDecimal current = new BigDecimal(getText());
                        if (current.compareTo(minValue) < 0) {
                            setText(minValue.toPlainString());
                        } else if (current.compareTo(maxValue) > 0) {
                            setText(maxValue.toPlainString());
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdating || !isPopSelector() || numberSelector == null) {
                return;
            }
            try {
                isUpdating = true;
                if (newValue == null || newValue.isBlank() || newValue.equals("-")) {
                    // 保持原样或处理
                } else {
                    numberSelector.setValue(new BigDecimal(newValue).intValue());
                }
            } catch (Exception ignored) {
            } finally {
                isUpdating = false;
            }
        });
    }

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void showSelectorPopup() {
        if (selectorPopup == null) {
            selectorPopup = new Popup();
            selectorPopup.setAutoHide(true);
            numberSelector = new NumberSelector();
            numberSelector.setMin(minValue.intValue());
            numberSelector.setMax(maxValue.intValue());
            numberSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (isUpdating) {
                    return;
                }
                isUpdating = true;
                setText(String.valueOf(newValue));
                isUpdating = false;
            });
            selectorPopup.getContent().add(numberSelector);
        }
        numberSelector.setMin(minValue.intValue());
        numberSelector.setMax(maxValue.intValue());
        if (getText() != null && !getText().isBlank() && !getText().equals("-")) {
            numberSelector.setValue(new BigDecimal(getText()).intValue());
        }
        if (pendingScrollValue != null) {
            int valToScroll = pendingScrollValue;
            pendingScrollValue = null;
            SCHEDULED_POOL.schedule(() -> Platform.runLater(() -> numberSelector.scrollTo(valToScroll)), 100, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        Bounds bounds = localToScreen(getBoundsInLocal());
        selectorPopup.show(getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
        BaseTransitionEnum.FADE.play(numberSelector, 0.5D, 1D, Duration.millis(200));
    }

    private boolean isInTheInterval(String text){
        if (Objects.equals(text, "-")){
            if (isForceRangeCheck() && minValue.compareTo(new BigDecimal("0")) >= 0){
                return false;
            }
            return true;
        }
        BigDecimal temp;
        try {
            if (text == null || text.isBlank()){
                return true;
            }
            temp = new BigDecimal(text);
            if (isForceRangeCheck()) {
                // 强校验模式：必须在范围内（之前不能输入范围外的行为）
                return maxValue.compareTo(temp) >= 0 && minValue.compareTo(temp) <= 0;
            }
            // 非强校验模式：输入时不拦截，失焦后矫正
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String formatText(String text){
        if (text == null || text.isBlank()){
            return "0";
        }
        return text;
    }

    private double calcStep(){
        return 1 / Math.pow(10, decimalCount);
    }

    @Override
    protected IconTextFieldSkin createDefaultSkin() {
        return new NumberFieldSkin(this);
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public void increment(){
        increment(calcStep(),false);
    }

    /**
     * 数值增加
     * @param value
     * @param playTransition 是否播放数值增加动画（注意：value值过大时动画时间很长，(value * Math.pow(10, decimalCount))不要超过500太多）
     */
    public void increment(double value, boolean playTransition){
        if (!isEditable()){
            return;
        }
        if (playTransition){
            new Thread(() -> {
                long count = (long) (value * Math.pow(10, decimalCount));
                long totalTime = 500L;
                long step = totalTime / count;
                step = Math.max(step, 1);
                step = Math.min(step, 25);
                try {
                    if (value > 0){
                        for (long i = 0; i < count; i++) {
                            Thread.sleep(step);
                            Platform.runLater(this::increment);
                        }
                    }else {
                        for (long i = 0; i < count; i++) {
                            Thread.sleep(step);
                            Platform.runLater(this::decrement);
                        }
                    }
                }catch (InterruptedException e) {
                    log.error("", e);
                }
            }).start();
            return;
        }
        String text = new BigDecimal(formatText(getText()))
                .add(BigDecimal.valueOf(value))
                .setScale(decimalCount, RoundingMode.DOWN)
                .toString();
        setText(text);
        end();
    }

    public void decrement(){
        increment(-calcStep(), false);
    }

    public void decrement(double value, boolean playTransition){
        increment(-value, playTransition);
    }

    /**
     * 滚动选择器到指定数值的位置，但不改变当前值
     * @param val
     */
    public void scrollTo(int val) {
        pendingScrollValue = val;
        if (numberSelector != null) {
            numberSelector.scrollTo(val);
        }
    }
}
