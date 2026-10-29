package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 22:28
 */
public class LinkDirIco extends AbstractIco {

    public LinkDirIco() {
        this(null);
    }

    public LinkDirIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

    @Override
    public String getColorStyle(String color) {
        // Only the arrow has a source stroke; do not outline the folder path.
        return super.getColorStyle(color) + String.format(".svg-path.st0 { -fx-stroke: %s; }", color);
    }

}
