package club.xiaojiawei.controls;

import club.xiaojiawei.skin.NumberFieldSkin;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 14:57
 */
public class NumberField extends TextField {

    /**
     * 小数数量
     */
    private final IntegerProperty decimalCount = new SimpleIntegerProperty();

    public int getDecimalCount() {
        return decimalCount.get();
    }

    public IntegerProperty decimalCountProperty() {
        return decimalCount;
    }

    public void setDecimalCount(int decimalCount) {
        this.decimalCount.set(decimalCount);
    }

    public NumberField() {
        setTextFormatter(new TextFormatter<>(change -> {
            String text = getText().substring(0, change.getRangeStart()) + change.getText() + getText().substring(change.getRangeEnd());
            if (decimalCount.get() > 0 && text.matches("^-?\\d*(\\.\\d{0," + decimalCount.get() + "})?$")){
                return change;
            }else if (decimalCount.get() <= 0 && text.matches("^-?\\d*$")){
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

    public void increment(){
        increment(calcStep(),false);
    }

    /**
     * 数值增加
     * @param value
     * @param playTransition 是否播放数值增加动画（注意：value值过大时动画时间很长，(value * Math.pow(10, decimalCount.get()))不要超过500太多）
     */
    public void increment(double value, boolean playTransition){
        if (playTransition){
            new Thread(() -> {
                int count = (int) (value * Math.pow(10, decimalCount.get()));
                int totalTime = 500;
                int step = totalTime / count;
                step = Math.max(step, 1);
                step = Math.min(step, 25);
                if (value > 0){
                    for (int i = 0; i < count; i++) {
                        try {
                            Thread.sleep(step);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Platform.runLater(this::increment);
                    }
                }else {
                    for (int i = 0; i < count; i++) {
                        try {
                            Thread.sleep(step);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Platform.runLater(this::decrement);
                    }
                }
            }).start();
            return;
        }
        String temp;
        if (decimalCount.get() > 0){
            temp = new BigDecimal(getTextNotBlank()).add(BigDecimal.valueOf(value)).setScale(decimalCount.get(), RoundingMode.DOWN).toString();
        }else {
            temp = String.valueOf(Integer.parseInt(getTextNotBlank()) + (int) value);
        }
        setText(temp);
        end();
    }

    public void decrement(){
        increment(-calcStep(), false);
    }
    public void decrement(double value, boolean playTransition){
        increment(-value, playTransition);
    }

    private String getTextNotBlank(){
        if (getText() == null || getText().isBlank()){
            return "0";
        }
        return getText();
    }

    private double calcStep(){
        return 1 / Math.pow(10, decimalCount.get());
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NumberFieldSkin(this);
    }
}
