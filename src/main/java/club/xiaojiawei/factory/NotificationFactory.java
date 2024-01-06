package club.xiaojiawei.factory;

import club.xiaojiawei.controls.Notification;
import club.xiaojiawei.enums.NotificationTypeEnum;
import club.xiaojiawei.enums.SizeEnum;
import javafx.scene.Group;
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
public class NotificationFactory {

    private double contentMaxWidth = -1;

    private boolean showingCloseBtn = true;

    private double transitionTime = 200D;

    private SizeEnum size = SizeEnum.DEFAULT;

    public Notification ofNew(){
        Notification notification = new Notification();
        notification.setContentMaxWidth(contentMaxWidth);
        notification.setShowingCloseBtn(showingCloseBtn);
        notification.setTransitionTime(transitionTime);
        notification.setSize(size);
        return notification;
    }

    public Notification ofNew(NotificationTypeEnum notificationTypeEnum, String title, String content){
        Notification notification = ofNew();
        notification.setType(notificationTypeEnum);
        notification.setTitle(title);
        notification.setContent(content);
        return notification;
    }

    public Notification ofNew(NotificationTypeEnum notificationTypeEnum, String title){
        return ofNew(notificationTypeEnum, title, null);
    }

    public Notification ofNew(String title, String content){
        return ofNew(NotificationTypeEnum.INFO, title, content);
    }
}
