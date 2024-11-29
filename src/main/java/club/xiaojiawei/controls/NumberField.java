package club.xiaojiawei.controls;

import club.xiaojiawei.component.IconTextField;
import club.xiaojiawei.skin.IconTextFieldSkin;
import club.xiaojiawei.skin.NumberFieldSkin;
import javafx.application.Platform;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 数字输入框
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 14:57
 */
@Setter
@Getter
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


    public void setMinValue(String minValue) {
        this.minValue = new BigDecimal(minValue);
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = new BigDecimal(maxValue);
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
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
    }

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private boolean isInTheInterval(String text){
        if (Objects.equals(text, "-")){
            if (minValue.compareTo(new BigDecimal("0")) >= 0){
                return false;
            }
            text = "0";
        }
        BigDecimal temp;
        return text == null
                || text.isBlank()
                || (maxValue.compareTo((temp = new BigDecimal(text))) >= 0 && minValue.compareTo(temp) <= 0);
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
}
