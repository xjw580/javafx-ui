package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 14:36
 */
public class InvisibleIco extends AbstractIco {

    public InvisibleIco() {
        this(null);
    }

    public InvisibleIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}