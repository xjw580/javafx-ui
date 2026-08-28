package club.xiaojiawei.controls.ico;

import club.xiaojiawei.controls.images.ImagesLoader;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import java.util.Objects;

public class DownloadIco extends AbstractIco {

    public DownloadIco() {
        this(null);
    }

    public DownloadIco(String color) {
        super(color);
        SVGImage svgImage = Objects.requireNonNull(
                SVGLoader.load(ImagesLoader.class.getResource("DownloadIco.svg")));
        setMaxWidth(svgImage.getWidth());
        getChildren().add(svgImage);
    }
}
