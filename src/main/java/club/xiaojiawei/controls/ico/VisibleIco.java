package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 14:45
 */
public class VisibleIco extends AbstractIco {

    public VisibleIco() {
        this(null);
    }

    public VisibleIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}