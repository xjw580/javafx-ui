package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/5 14:29
 */
public class ImportIco extends AbstractIco {

    public ImportIco() {
        this(null);
    }

    public ImportIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}