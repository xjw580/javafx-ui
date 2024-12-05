package club.xiaojiawei.annotations;

import java.lang.annotation.*;

/**
 * 不为空
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/11 11:59
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface NotNull {
}
