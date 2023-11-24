package club.xiaojiawei.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/23 17:04
 */
public class LabelUI extends Label {
    public LabelUI() {
        this("");
    }

    public LabelUI(String s) {
        super(s);
        getStyleClass().add("label-ui");
        Rectangle rectangle = new Rectangle();
        setClip(rectangle);
        heightProperty().addListener((observableValue, number, t1) -> {
            rectangle.setArcHeight(this.getHeight());
            rectangle.setArcWidth(this.getHeight());
            rectangle.setHeight(this.getHeight());
            rectangle.setTranslateX(-this.getHeight());
        });
        widthProperty().addListener((observableValue, number, t1) -> rectangle.setWidth(this.getWidth()));
    }
}
