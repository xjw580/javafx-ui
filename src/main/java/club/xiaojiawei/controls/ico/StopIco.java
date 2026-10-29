package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 22:28
 */
public class StopIco extends AbstractIco {

    public StopIco() {
        this(null);
    }

    public StopIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}