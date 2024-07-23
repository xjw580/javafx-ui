package club.xiaojiawei.skin;

import club.xiaojiawei.controls.TipField;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.InputEvent;
import javafx.scene.text.Text;

import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 17:35
 */
public class TipFieldSkin extends IconTextFieldSkin {

    private boolean isShow = false;

    private String focusText;

    public TipFieldSkin(TipField tipField) {
        super(tipField);
        Button button = new Button(tipField.getTip());
        button.textProperty().bind(tipField.tipProperty());
        button.setFocusTraversable(false);
        button.setStyle("-fx-background-color: transparent;-fx-border-color: transparent;-fx-text-fill: gray!important;-fx-font-size: 10!important;-fx-padding: 1 0 0 2");

        change(button, false);
        addTipButton(button);

        tipField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (tipField.isFocused()) {
                if (Objects.equals(focusText, newValue)) {
                    isShow = false;
//                    removeTipButton(button);
                    change(button, false);
                }else if (!isShow) {
                    isShow = true;
//                    addTipButton(button);
                    change(button, true);
                }
            }
        });
        tipField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                focusText = tipField.getText();
            }else {
                isShow = false;
//                removeTipButton(button);
                change(button, false);
            }
        });

    }

    private void change(Button button, boolean enable){
        button.setVisible(enable);
//        button.setManaged(enable);
    }

}
