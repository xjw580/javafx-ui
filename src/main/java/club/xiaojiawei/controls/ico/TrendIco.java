package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/5 9:57
 */
public class TrendIco extends AbstractIco {

    public TrendIco() {
        this("black");
    }

    public TrendIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}