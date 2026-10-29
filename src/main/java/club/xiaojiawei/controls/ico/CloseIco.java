package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 0:28
 */
public class CloseIco extends AbstractIco {

    public CloseIco() {
        this(null);
    }

    public CloseIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}