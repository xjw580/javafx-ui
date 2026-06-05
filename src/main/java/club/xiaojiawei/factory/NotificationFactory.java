package club.xiaojiawei.factory;

import club.xiaojiawei.animations.AnimationStrategy;
import club.xiaojiawei.animations.SlideAnimationStrategy;
import club.xiaojiawei.annotations.ValidSizeRange;
import club.xiaojiawei.controls.Notification;
import club.xiaojiawei.enums.NotificationTypeEnum;
import club.xiaojiawei.enums.SizeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知工厂
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/6 22:39
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationFactory<T> {

    @Builder.Default
    private double contentMaxWidth = -1;

    @Builder.Default
    private boolean showingCloseBtn = true;

    @Builder.Default
    private double transitionTime = 200D;

    @Builder.Default
    @ValidSizeRange({SizeEnum.TINY, SizeEnum.SMALL, SizeEnum.MEDDLE, SizeEnum.DEFAULT, SizeEnum.BIG})
    private SizeEnum size = SizeEnum.DEFAULT;

    /**
     * 动画策略
     */
    @Builder.Default
    private AnimationStrategy animationStrategy = SlideAnimationStrategy.instance();

    public Notification<T> ofNew() {
        Notification<T> notification = new Notification<>();
        notification.setContentMaxWidth(contentMaxWidth);
        notification.setShowingCloseBtn(showingCloseBtn);
        notification.setSize(size);
        return notification;
    }

    public Notification<T> ofNew(NotificationTypeEnum notificationTypeEnum, String title, T content) {
        Notification<T> notification = ofNew();
        notification.setType(notificationTypeEnum);
        notification.setTitle(title);
        notification.setContent(content);
        return notification;
    }

    public Notification<T> ofNew(NotificationTypeEnum notificationTypeEnum, String title) {
        return ofNew(notificationTypeEnum, title, null);
    }

    public Notification<T> ofNew(String title, T content) {
        return ofNew(NotificationTypeEnum.INFO, title, content);
    }
}
