package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/5 9:58
 */
public class EventIco extends AbstractIco {

    public EventIco() {
        this(null);
    }

    public EventIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}