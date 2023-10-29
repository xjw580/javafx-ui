package club.xiaojiawei;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/30 0:03
 */

import java.util.Objects;
@SuppressWarnings("all")
public class JavaFXUI {

    public static String javafxUIStylesheet(){
        return Objects.requireNonNull(JavaFXUI.class.getResource("controls/css/common/javafx-ui.css")).toExternalForm();
    }

}
