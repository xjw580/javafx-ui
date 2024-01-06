package club.xiaojiawei.readme.tab.component;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.enums.NotificationPosEnum;
import javafx.fxml.FXML;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/2 17:44
 */
public class NotificationController {

    @FXML
    private NotificationManager notificationManager;

    @FXML
    protected void topLeft(){
        notificationManager.setNotificationPos(NotificationPosEnum.TOP_LEFT);
        notificationManager.showInfo("我在这捏", 1);
    }
    @FXML
    protected void topCenter(){
        notificationManager.setNotificationPos(NotificationPosEnum.TOP_CENTER);
        notificationManager.showSuccess("我在这捏", 1);
    }
    @FXML
    protected void topRight(){
        notificationManager.setNotificationPos(NotificationPosEnum.TOP_RIGHT);
        notificationManager.showInfo("我在这捏", 1);
    }
    @FXML
    protected void center(){
        notificationManager.setNotificationPos(NotificationPosEnum.CENTER);
        notificationManager.showInfo("我在这捏", 1);
    }
    @FXML
    protected void bottomLeft(){
        notificationManager.setNotificationPos(NotificationPosEnum.BOTTOM_LEFT);
        notificationManager.showInfo("我在这捏", 1);
    }
    @FXML
    protected void bottomCenter(){
        notificationManager.setNotificationPos(NotificationPosEnum.BOTTOM_CENTER);
        notificationManager.showInfo("我在这捏", 1);
    }
    @FXML
    protected void bottomRight(){
        notificationManager.setNotificationPos(NotificationPosEnum.BOTTOM_RIGHT);
        notificationManager.showInfo("我在这捏", 1);
    }

}
