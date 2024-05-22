package club.xiaojiawei.demo.tab.controls;

import club.xiaojiawei.controls.Date;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.ico.ClearIco;
import club.xiaojiawei.controls.ico.FileIco;
import club.xiaojiawei.enums.NotificationPosEnum;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/2 17:44
 */
public class NotificationController {

    @FXML
    private NotificationManager<Object> notificationManager;

    @FXML
    protected void topLeft(){
        notificationManager.setNotificationPos(NotificationPosEnum.TOP_LEFT);
        Label label = new Label("我在这捏");
        label.setGraphic(new FileIco());
        VBox hBox = new VBox(label, new Date());
        notificationManager.showInfo("我在这捏", hBox, 2);
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
        notificationManager.showWarn("我在这捏", 1);
    }
    @FXML
    protected void bottomRight(){
        notificationManager.setNotificationPos(NotificationPosEnum.BOTTOM_RIGHT);
        notificationManager.showInfo("我在这捏", 1);
    }

}
