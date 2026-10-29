package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 0:05
 */
public class PauseIco extends AbstractIco {

    public PauseIco() {
        this(null);
    }

    public PauseIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}