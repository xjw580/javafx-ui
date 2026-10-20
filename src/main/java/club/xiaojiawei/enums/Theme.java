package club.xiaojiawei.enums;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/7 10:59
 */
public enum Theme {
    BASE("/club/xiaojiawei/controls/css/themes/base.css"),
    WINDOWS("/club/xiaojiawei/controls/css/themes/windows.css");

    private final String resourcePath;

    Theme(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String resourcePath() {
        return resourcePath;
    }
}
