package club.xiaojiawei.annotations;

import club.xiaojiawei.enums.SizeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标定有效尺寸范围
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/4/8 20:05
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface ValidSizeRange {

    SizeEnum[] value();

}
