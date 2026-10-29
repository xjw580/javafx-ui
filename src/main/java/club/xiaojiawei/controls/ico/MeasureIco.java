package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 22:28
 */
public class MeasureIco extends AbstractIco {

    public MeasureIco() {
        this(null);
    }

    public MeasureIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }
}