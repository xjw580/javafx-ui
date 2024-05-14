package club.xiaojiawei.annotations;

import java.lang.annotation.*;

/**
 * 该方法只能调用一次或调用多次不生效
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/11 11:59
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface OnlyOnce {
}
