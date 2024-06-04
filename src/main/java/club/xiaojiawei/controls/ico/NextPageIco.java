package club.xiaojiawei.controls.ico;

import club.xiaojiawei.controls.images.ImagesLoader;
import javafx.scene.layout.StackPane;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 22:28
 */
public class NextPageIco extends AbstractIco {

    public NextPageIco() {
        this("black");
    }

    public NextPageIco(String color) {
        super(color);
        SVGImage svgImage = SVGLoader.load(ImagesLoader.class.getResource(this.getClass().getSimpleName() + ".svg"));
        this.setMaxWidth(svgImage.getWidth());
        this.getChildren().add(svgImage);
    }

}