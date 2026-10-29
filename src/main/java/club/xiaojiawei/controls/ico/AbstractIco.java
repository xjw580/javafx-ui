package club.xiaojiawei.controls.ico;

import club.xiaojiawei.controls.images.ImagesLoader;
import club.xiaojiawei.svg.SvgView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/26 15:20
 */
public abstract class AbstractIco extends StackPane{

    private String colorStylesheet;

    public AbstractIco() {
        getStyleClass().add("javafx-ui-ico");
        color.addListener((observableValue, s, t1) -> {
            if (colorStylesheet != null) {
                getStylesheets().remove(colorStylesheet);
                colorStylesheet = null;
            }
            if (t1 != null && !t1.isBlank()){
                colorStylesheet = dataUriStylesheet(t1);
                getStylesheets().add(0, colorStylesheet);
            }
        });
    }

    public AbstractIco(String color) {
        this();
        if (color != null && !color.isEmpty()){
            setColor(color);
        }
    }

    /** Loads a cached document into a fresh scene graph for this icon. */
    protected final void loadSvg(String resourceName) {
        URL resource = Objects.requireNonNull(ImagesLoader.class.getResource(resourceName),
                "Missing icon resource: " + resourceName);
        SvgView view = new SvgView();
        view.setSource(resource.toExternalForm());
        // Unlike SVGImage, SvgView is a resizable Region. Keep the icon's natural
        // size even when its StackPane is laid out smaller or larger than it.
        view.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        view.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxWidth(view.prefWidth(-1));
        getChildren().add(view);
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
        return String.format(".svg-view { " +
                "-svg-fill: %s;" +
                "}", color);
    }

}
