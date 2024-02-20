package club.xiaojiawei.readme.tab.controls;

import club.xiaojiawei.controls.NumberField;
import javafx.fxml.FXML;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/20 11:39
 */
public class NumberFieldController {

    @FXML
    private NumberField incrementNumberField;

    @FXML
    protected void incrementTenNumber(){
        incrementNumberField.increment(10, true);
    }

    @FXML
    protected void incrementHundredNumber(){
        incrementNumberField.increment(100, true);
    }

    @FXML
    protected void incrementThousandNumber(){
        incrementNumberField.increment(1000, true);
    }
}
