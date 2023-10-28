package club.xiaojiawei.rewite;

import javafx.beans.value.WritableObjectValue;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 14:03
 */
public class ReferenceProperty<T> implements WritableObjectValue<T> {
    @Override
    public T get() {
        return getValue();
    }

    @Override
    public void set(T value) {

    }

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public void setValue(T value) {

    }
}
