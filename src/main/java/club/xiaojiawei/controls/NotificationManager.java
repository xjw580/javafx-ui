package club.xiaojiawei.controls;

import club.xiaojiawei.enums.NotificationPosEnum;
import club.xiaojiawei.enums.NotificationTypeEnum;
import club.xiaojiawei.factory.NotificationFactory;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.*;

import java.util.concurrent.*;

import static club.xiaojiawei.config.JavaFXUIThreadPoolConfig.SCHEDULED_POOL;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/2 10:00
 */
@DefaultProperty("notificationFactory")
public class NotificationManager extends HBox {

    /**
     * 通知位置
     */
    @Getter
    private NotificationPosEnum notificationPos = NotificationPosEnum.BOTTOM_RIGHT;
    /**
     * 通知最大条数
     */
    @Getter
    @Setter
    private int maxCount = Integer.MAX_VALUE;

    public void setNotificationPos(NotificationPosEnum notificationPos) {
        this.notificationPos = notificationPos;
        changeNotificationPos();
    }

    private ChangeListener<Scene> sceneListener;
    private final VBox notificationVBox = new VBox();
    @Getter
    @Setter
    private NotificationFactory notificationFactory = new NotificationFactory();

    public NotificationManager() {
        setManaged(false);
        getChildren().add(notificationVBox);
        sceneProperty().addListener(sceneListener = (observableValue, aBoolean, t1) -> {
            if (t1 != null){
                sceneProperty().removeListener(sceneListener);
                sceneListener = null;
                changeNotificationPos();
            }
        });
    }

    private void changeNotificationPos(){
        if (getScene() == null){
            return;
        }
        Pane rootPane = null;
        Parent rootParent = this;
        while (rootParent.getParent() != null){
            rootParent = rootParent.getParent();
            if (rootParent instanceof Pane pane){
                rootPane = pane;
            }
        }

        layoutXProperty().unbind();
        layoutYProperty().unbind();

        ReadOnlyDoubleProperty widthProperty;
        ReadOnlyDoubleProperty heightProperty;
        if (rootPane == null){
            widthProperty = getScene().widthProperty();
            heightProperty = getScene().heightProperty();
        }else {
            if (this.getParent() instanceof Pane pane){
                pane.getChildren().remove(this);
            }
            rootPane.getChildren().add(this);
            widthProperty = rootPane.widthProperty();
            heightProperty = rootPane.heightProperty();
        }

        switch (notificationPos){
            case TOP_LEFT -> {
                setLayoutX(0);
                setLayoutY(0);
                notificationVBox.setAlignment(Pos.TOP_LEFT);
            }
            case TOP_CENTER -> {
                layoutXProperty().bind(widthProperty.subtract(notificationVBox.widthProperty()).divide(2));
                setLayoutY(0);
                notificationVBox.setAlignment(Pos.TOP_CENTER);
            }
            case TOP_RIGHT -> {
                layoutXProperty().bind(widthProperty.subtract(notificationVBox.widthProperty()));
                setLayoutY(0);
                notificationVBox.setAlignment(Pos.TOP_RIGHT);
            }
            case CENTER -> {
                layoutXProperty().bind(widthProperty.subtract(notificationVBox.widthProperty()).divide(2));
                layoutYProperty().bind(heightProperty.subtract(notificationVBox.heightProperty()).divide(2));
                notificationVBox.setAlignment(Pos.CENTER);
            }
            case BOTTOM_LEFT -> {
                setLayoutX(0);
                layoutYProperty().bind(heightProperty.subtract(notificationVBox.heightProperty()));
                notificationVBox.setAlignment(Pos.BOTTOM_LEFT);
            }
            case BOTTOM_CENTER -> {
                layoutXProperty().bind(widthProperty.subtract(notificationVBox.widthProperty()).divide(2));
                layoutYProperty().bind(heightProperty.subtract(notificationVBox.heightProperty()));
                notificationVBox.setAlignment(Pos.BOTTOM_CENTER);
            }
            case BOTTOM_RIGHT -> {
                layoutXProperty().bind(widthProperty.subtract(notificationVBox.widthProperty()));
                layoutYProperty().bind(heightProperty.subtract(notificationVBox.heightProperty()));
                notificationVBox.setAlignment(Pos.BOTTOM_RIGHT);
            }
        }
    }

    public void showInfo(String title, String content, long closeTime){
        show(notificationFactory.ofNew(title, content), closeTime);
    }
    public void showInfo(String title, String content){
        showInfo(title, content, -1L);
    }
    public void showInfo(String title, long closeTime){
        showInfo(title, null, closeTime);
    }
    public void showInfo(String title){
        showInfo(title, null);
    }

    public void showSuccess(String title, String content, long closeTime){
        show(notificationFactory.ofNew(NotificationTypeEnum.SUCCESS, title, content), closeTime);
    }
    public void showSuccess(String title, String content){
        showSuccess(title, content, -1L);
    }
    public void showSuccess(String title, long closeTime){
        showSuccess(title, null, closeTime);
    }
    public void showSuccess(String title){
        showSuccess(title, null);
    }

    public void showWarn(String title, String content, long closeTime){
        show(notificationFactory.ofNew(NotificationTypeEnum.WARN, title, content), closeTime);
    }
    public void showWarn(String title, String content){
        showWarn(title, content, -1L);
    }
    public void showWarn(String title, long closeTime){
        showWarn(title, null, closeTime);
    }
    public void showWarn(String title){
        showWarn(title ,null);
    }

    public void showError(String title, String content, long closeTime){
        show(notificationFactory.ofNew(NotificationTypeEnum.ERROR, title, content), closeTime);
    }
    public void showError(String title, String content){
        showError(title, content, -1L);
    }
    public void showError(String title, long closeTime){
        showError(title, null, closeTime);
    }
    public void showError(String title){
        showError(title, null);
    }

    public void show(Notification notification){
        if (notificationVBox.getChildren().size() >= maxCount){
            return;
        }
        notificationVBox.getChildren().add(notification);
        notification.setOnCloseEvent(() -> notificationVBox.getChildren().remove(notification));
        notification.show();
    }
    public void show(Notification notification, long closeSecTime){
        show(notification, closeSecTime, TimeUnit.SECONDS);
    }
    public void show(Notification notification, long closeTime, TimeUnit timeUnit){
        show(notification);
        if (closeTime > 0){
            SCHEDULED_POOL.schedule(() -> Platform.runLater(() -> notification.hide(() -> notificationVBox.getChildren().remove(notification))), closeTime, timeUnit);
        }
    }

}
