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

    SUCCESS(OKIco::new),
    INFO(MessageIco::new),
    WARN(WarnIco::new),
    ERROR(FailIco::new),
    ;
    private final Supplier<Pane> builder;

    NotificationTypeEnum(Supplier<Pane> builder) {
        this.builder = builder;
    }

}
