package club.xiaojiawei.controls;

import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

/**
 * 标题
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/23 17:04
 */
public class Title extends Label {
    public static final String DEFAULT_STYLE_CLASS = "title-ui";
    public Title() {
        this("");
    }

    public Title(String s) {
        super(s);
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }
}
