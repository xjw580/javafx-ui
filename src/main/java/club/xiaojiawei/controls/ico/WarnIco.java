package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 22:33
 */
public class WarnIco extends AbstractIco {

    public WarnIco() {
        this(null);
    }

    public WarnIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}