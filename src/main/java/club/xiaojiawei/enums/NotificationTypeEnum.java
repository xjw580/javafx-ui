package club.xiaojiawei.enums;

import club.xiaojiawei.controls.ico.FailIco;
import club.xiaojiawei.controls.ico.MessageIco;
import club.xiaojiawei.controls.ico.OKIco;
import club.xiaojiawei.controls.ico.WarnIco;
import javafx.scene.layout.Pane;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/2 9:10
 */
@Getter
public enum NotificationTypeEnum {

    SUCCESS(OKIco::new, "#389751"),
    INFO(MessageIco::new, "#2281F0"),
    WARN(WarnIco::new, "#FF8000"),
    ERROR(FailIco::new, "#FF0000"),
    ;
    private final Supplier<Pane> builder;

    private final String color;

    NotificationTypeEnum(Supplier<Pane> builder, String color) {
        this.builder = builder;
        this.color = color;
    }

}
