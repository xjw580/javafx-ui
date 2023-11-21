package club.xiaojiawei.controls.ico;

import club.xiaojiawei.controls.images.ImagesLoader;
import javafx.scene.layout.StackPane;
import org.girod.javafx.svgimage.SVGLoader;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 22:28
 */
public class ExitIco extends StackPane {

    public ExitIco() {
        this.getChildren().add(SVGLoader.load(ImagesLoader.class.getResource(this.getClass().getSimpleName() + ".svg")));
    }
}