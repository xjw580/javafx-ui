package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 0:28
 */
public class DateIco extends AbstractIco {

    public DateIco() {
        this(null);
    }

    public DateIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}