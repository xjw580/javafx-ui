package club.xiaojiawei.func;

import javafx.beans.property.ObjectProperty;

import java.util.function.Predicate;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/8 15:12
 */
public interface Interceptor<T> {

    public abstract Predicate<T> getInterceptor();

    public abstract ObjectProperty<Predicate<T>> interceptorProperty();

    public abstract void setInterceptor(Predicate<T> dateInterceptor);

    default boolean test(T t){
        return getInterceptor() == null || getInterceptor().test(t);
    }

}
