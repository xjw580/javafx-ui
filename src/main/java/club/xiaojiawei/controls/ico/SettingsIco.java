package club.xiaojiawei.controls.ico;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 0:05
 */
public class SettingsIco extends AbstractIco {

    public SettingsIco() {
        this(null);
    }

    public SettingsIco(String color) {
        super(color);
        loadSvg(getClass().getSimpleName() + ".svg");
    }

}