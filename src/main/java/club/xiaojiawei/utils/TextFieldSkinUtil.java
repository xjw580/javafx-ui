package club.xiaojiawei.utils;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

/**
 * 文本框皮肤工具类
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/20 11:11
 */
public class TextFieldSkinUtil {

    public static Button buildButton(){
        return buildButton(null, null);
    }

    public static Button buildButton(String svgContent){
        return buildButton(svgContent, Color.BLACK);
    }

    public static Button buildButton(String svgContent, Paint svgPaint){
        Button button = new Button();

        if (svgContent != null){
            SVGPath svgPath = new SVGPath();
            button.setGraphic(svgPath);
            svgPath.setContent(svgContent);
            svgPath.setFill(svgPaint);
        }

        button.setPadding(new Insets(0));
        button.setFocusTraversable(false);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, new Insets(0))));
        button.setCursor(Cursor.HAND);

        return button;
    }

}
