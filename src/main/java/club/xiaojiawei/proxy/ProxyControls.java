package club.xiaojiawei.proxy;

/**
 * 表示是一个代理组件
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 16:11
 */
public interface ProxyControls <T>{

    /**
     * 返回真实组件，也就是被代理的组件
     * @return
     */
    T getRealControls();
}
