package club.xiaojiawei;

/**
 * 获取样式表路径
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/30 0:03
 */

import javafx.scene.Scene;

import java.util.Objects;
@SuppressWarnings("all")
public class JavaFXUI {
    public static void addjavafxUIStylesheet(Scene scene){
        scene.getStylesheets().add(JavaFXUI.javafxUIStylesheet());
    }

    /**
     * 加载主样式表（javafx-ui.css包含所有样式）
     * @return
     */
    public static String javafxUIStylesheet(){
        return Objects.requireNonNull(JavaFXUI.class.getResource("controls/css/common/javafx-ui.css")).toExternalForm();
    }

    /**
     * 加载指定样式表
     * @param stylesheetName
     * @return
     */
    public static String stylesheet(String stylesheetName){
        return Objects.requireNonNull(JavaFXUI.class.getResource("controls/css/common/" + stylesheetName)).toExternalForm();
    }
//    TODO ADD ENUMS

}
