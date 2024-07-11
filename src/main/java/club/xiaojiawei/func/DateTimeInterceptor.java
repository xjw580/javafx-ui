package club.xiaojiawei.func;

import javafx.beans.property.ObjectProperty;

import java.util.function.Predicate;

/**
 * 日期拦截器
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/8 15:12
 */
public interface DateTimeInterceptor<T> {

    /**
     * 获取日期拦截器
     * @return
     */
    Predicate<T> getInterceptor();

    /**
     * 获取日期拦截器属性
     * @return
     */
    ObjectProperty<Predicate<T>> interceptorProperty();

    /**
     * 设置日期拦截器
     * @param dateInterceptor
     */
    void setInterceptor(Predicate<T> dateInterceptor);

    default boolean test(T t){
        return getInterceptor() == null || getInterceptor().test(t);
    }

}
