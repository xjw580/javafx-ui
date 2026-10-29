package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/7 17:32
 */
public class NextIco extends AbstractIco {

    public NextIco() {
        this("black");
    }

    public NextIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}