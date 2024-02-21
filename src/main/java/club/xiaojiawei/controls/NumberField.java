package club.xiaojiawei.controls;

import club.xiaojiawei.skin.NumberFieldSkin;
import javafx.application.Platform;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 14:57
 */
@Setter
@Getter
public class NumberField extends TextField {

    /**
     * 小数数量
     */
    private int decimalCount = 0;

    public NumberField() {
        setTextFormatter(new TextFormatter<>(change -> {
            String text = getText().substring(0, change.getRangeStart()) + change.getText() + getText().substring(change.getRangeEnd());
            if (decimalCount > 0 && text.matches("^-?\\d*(\\.\\d{0," + decimalCount + "})?$")){
                return change;
            }else if (decimalCount <= 0 && text.matches("^-?\\d*$")){
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
     * @param playTransition 是否播放数值增加动画（注意：value值过大时动画时间很长，(value * Math.pow(10, decimalCount))不要超过500太多）
     */
    public void increment(double value, boolean playTransition){
        if (playTransition){
            new Thread(() -> {
                int count = (int) (value * Math.pow(10, decimalCount));
                int totalTime = 500;
                int step = totalTime / count;
                step = Math.max(step, 1);
                step = Math.min(step, 25);
                try {
                    if (value > 0){
                        for (int i = 0; i < count; i++) {
                            Thread.sleep(step);
                            Platform.runLater(this::increment);
                        }
                    }else {
                        for (int i = 0; i < count; i++) {
                            Thread.sleep(step);
                            Platform.runLater(this::decrement);
                        }
                    }
                }catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            return;
        }
        String temp;
        if (decimalCount > 0){
            temp = new BigDecimal(getTextNotBlank()).add(BigDecimal.valueOf(value)).setScale(decimalCount, RoundingMode.DOWN).toString();
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
        return 1 / Math.pow(10, decimalCount);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new NumberFieldSkin(this);
    }
}
