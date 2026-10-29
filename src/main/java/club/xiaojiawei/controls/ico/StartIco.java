package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 0:05
 */
public class StartIco extends AbstractIco {

    public StartIco() {
        this(null);
    }

    public StartIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

    @Override
    public String getColorStyle(String color) {
        return String.format(".svg-view { " +
                "-svg-stroke: %s;" +
                "}", color);
    }

}