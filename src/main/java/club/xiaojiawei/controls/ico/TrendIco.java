package club.xiaojiawei.controls.ico;

import club.xiaojiawei.controls.images.ImagesLoader;
import javafx.scene.layout.StackPane;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/5 9:57
 */
public class TrendIco extends AbstractIco {

    public TrendIco() {
        this("black");
    }

    public TrendIco(String color) {
        super(color);
        SVGImage svgImage = SVGLoader.load(ImagesLoader.class.getResource(this.getClass().getSimpleName() + ".svg"));
        if (svgImage != null) {
            this.setMaxWidth(svgImage.getWidth());
            this.getChildren().add(svgImage);
        }
    }

}