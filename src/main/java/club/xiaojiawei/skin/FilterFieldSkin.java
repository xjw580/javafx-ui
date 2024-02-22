package club.xiaojiawei.skin;

import club.xiaojiawei.controls.FilterField;
import club.xiaojiawei.controls.NumberField;
import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.scene.control.Button;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 17:35
 */
public class FilterFieldSkin extends IconTextFieldSkin {

    public FilterFieldSkin(FilterField filterField) {
        super(filterField);

        Button button = buildIconButton("M13.7,12.31l-2.47-2.39c.68-1.02,1.08-2.25,1.08-3.58C12.31,2.84,9.55,0,6.15,0S0,2.84,0,6.34s2.76,6.34,6.15,6.34c1.42,0,2.74-.5,3.78-1.34l2.47,2.39c.38,.37,.98,.35,1.34-.04,.36-.39,.34-1.01-.04-1.38ZM1.42,6.34C1.42,3.64,3.54,1.46,6.15,1.46s4.73,2.18,4.73,4.88-2.12,4.88-4.73,4.88S1.42,9.03,1.42,6.34Z", Paint.valueOf("#4E5053FF"));
        button.setId("action-btn");
        button.setOnMouseEntered(event -> BaseTransitionEnum.SCALE.play(button, 1.3, Duration.millis(25)));
        button.setOnMouseExited(event -> BaseTransitionEnum.SCALE.play(button, 1, Duration.millis(25)));
        button.setOnAction(actionEvent -> {
            if (filterField.getOnFilterAction() != null){
                filterField.getOnFilterAction().handle(filterField.getText());
            }
        });
        addIconButton(button);
    }

}
