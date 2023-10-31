package club.xiaojiawei.proxy;

import javafx.scene.layout.Region;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/1 0:11
 */
public interface ProxyRegion <T extends Region>{

    T getRegion();

}
