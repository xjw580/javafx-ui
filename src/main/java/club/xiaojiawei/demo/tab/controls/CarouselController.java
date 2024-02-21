package club.xiaojiawei.demo.tab.controls;

import club.xiaojiawei.controls.Carousel;
import club.xiaojiawei.controls.NumberField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 16:06
 */
public class CarouselController implements Initializable {

    @FXML
    private Carousel carouselDemo;
    @FXML
    private NumberField nudeScaleField;

    @FXML
    protected void changeNudeScale(){
        carouselDemo.setNudeScale(Double.parseDouble(nudeScaleField.getText()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
