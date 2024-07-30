package club.xiaojiawei;

import club.xiaojiawei.demo.DemoApplication;
import club.xiaojiawei.enums.StylesheetEnum;
import javafx.application.Application;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 获取或添加样式表路径
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/30 0:03
 */
@SuppressWarnings("all")
public class JavaFXUI {

    @Getter
    @Setter
    private static Marker logMark;

    /**
     * 启动演示程序
     */
    public static void launchDemoProgram(){
        Application.launch(DemoApplication.class);
    }

    /**
     * 向Scene添加主样式表（主样式javafx-ui.css包含所有样式）
     * @param scene
     */
    public static void addjavafxUIStylesheet(Scene scene){
        scene.getStylesheets().add(javafxUIStylesheet());
    }

    /**
     * 获取主样式表路径（主样式javafx-ui.css包含所有样式）
     * @return String 主样式表路径
     */
    public static String javafxUIStylesheet(){
        return stylesheet(StylesheetEnum.JAVAFX_UI).get(0);
    }

    /**
     * 获取指定样式表路径
     * @param stylesheetEnums
     * @return
     */
    public static List<String> stylesheet(StylesheetEnum... stylesheetEnums){
        List<String> styleSheetList = new ArrayList<>();
        if (stylesheetEnums != null){
            for (StylesheetEnum stylesheetEnum : stylesheetEnums) {
                String styleSheetName = stylesheetEnum.name().toLowerCase().replace("_", "-") + ".css";
                styleSheetList.add(Objects.requireNonNull(JavaFXUI.class.getResource("controls/css/common/" + styleSheetName), styleSheetName).toExternalForm());
            }
        }
        return styleSheetList;
    }

    /**
     * 向Scene添加指定样式表
     * @param scene
     * @param stylesheetEnums
     */
    public static void addStylesheet(Scene scene, StylesheetEnum... stylesheetEnums){
        scene.getStylesheets().addAll(stylesheet(stylesheetEnums));
    }

}
