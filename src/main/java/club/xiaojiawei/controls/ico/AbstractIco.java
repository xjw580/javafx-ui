package club.xiaojiawei.controls.ico;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.StackPane;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/26 15:20
 */
public abstract class AbstractIco extends StackPane{

    public AbstractIco() {
        getStyleClass().add("javafx-ui-ico");
        color.addListener((observableValue, s, t1) -> {
            if (!this.getStylesheets().isEmpty()){
                this.getStylesheets().remove(0);
            }
            if (t1 != null && !t1.isBlank()){
                this.getStylesheets().add(0, dataUriStylesheet(t1));
            }
        });
    }

    public AbstractIco(String color) {
        this();
        setColor(color);
    }

    private final StringProperty color = new SimpleStringProperty();

    public String getColor() {
        return color.get();
    }

    public StringProperty colorProperty() {
        return color;
    }

    public void setColor(String color) {
        this.color.set(color);
    }

    private String dataUriStylesheet(String color) {
        return "data:text/css;charset=utf-8;base64," + Base64.getEncoder().encodeToString(getColorStyle(color).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 更改颜色时构建的style字符串
     * @param color
     * @return
     */
    public String getColorStyle(String color){
        return String.format("* { " +
                "-fx-fill: %s;" +
                "}", color);
    }

}
