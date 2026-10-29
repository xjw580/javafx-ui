package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 14:54
 */
public class UpdateIco extends AbstractIco {

    public UpdateIco() {
        this(null);
    }

    public UpdateIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}