package club.xiaojiawei.controls.ico;

import club.xiaojiawei.controls.images.ImagesLoader;
import javafx.scene.layout.StackPane;
import org.girod.javafx.svgimage.SVGLoader;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/7 17:32
 */
public class RightIco extends StackPane {

    public RightIco() {
        this.getChildren().add(SVGLoader.load(ImagesLoader.class.getResource(this.getClass().getSimpleName() + ".svg")));
    }
}