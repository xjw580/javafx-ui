package club.xiaojiawei.controls;

import club.xiaojiawei.bean.LogRunnable;
import club.xiaojiawei.enums.NotificationPosEnum;
import club.xiaojiawei.enums.NotificationTypeEnum;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/2 10:00
 */
public class NotificationManager extends HBox {

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
        changePos();
    }

    private ChangeListener<Scene> sceneListener;
    private final VBox notificationVBox = new VBox();
    private final ScheduledExecutorService notificationPool = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
        private final AtomicInteger num = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(new LogRunnable(r), "NotificationPool Thread-" + num.getAndIncrement());
        }
    }, new ThreadPoolExecutor.AbortPolicy());

    public NotificationManager() {
        setManaged(false);
        getChildren().add(notificationVBox);
        sceneProperty().addListener(sceneListener = (observableValue, aBoolean, t1) -> {
            if (t1 != null){
                sceneProperty().removeListener(sceneListener);
                sceneListener = null;
                changePos();
            }
        });
    }

    private void changePos(){
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
        show(new Notification(title, content), closeTime);
    }
    public void showInfo(String title, String content){
        show(new Notification(title, content));
    }
    public void showInfo(String title, long closeTime){
        show(new Notification(title), closeTime);
    }
    public void showInfo(String title){
        show(new Notification(title));
    }

    public void showSuccess(String title, String content, long closeTime){
        show(new Notification(NotificationTypeEnum.SUCCESS, title, content), closeTime);
    }
    public void showSuccess(String title, String content){
        show(new Notification(NotificationTypeEnum.SUCCESS, title, content));
    }
    public void showSuccess(String title, long closeTime){
        show(new Notification(NotificationTypeEnum.SUCCESS, title), closeTime);
    }
    public void showSuccess(String title){
        show(new Notification(NotificationTypeEnum.SUCCESS, title));
    }

    public void showWarn(String title, String content, long closeTime){
        show(new Notification(NotificationTypeEnum.WARN, title, content), closeTime);
    }
    public void showWarn(String title, String content){
        show(new Notification(NotificationTypeEnum.WARN, title, content));
    }
    public void showWarn(String title, long closeTime){
        show(new Notification(NotificationTypeEnum.WARN, title), closeTime);
    }
    public void showWarn(String title){
        show(new Notification(NotificationTypeEnum.WARN, title));
    }

    public void showError(String title, String content, long closeTime){
        show(new Notification(NotificationTypeEnum.ERROR, title, content), closeTime);
    }
    public void showError(String title, String content){
        show(new Notification(NotificationTypeEnum.ERROR, title, content));
    }
    public void showError(String title, long closeTime){
        show(new Notification(NotificationTypeEnum.ERROR, title), closeTime);
    }
    public void showError(String title){
        show(new Notification(NotificationTypeEnum.ERROR, title));
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
        notificationPool.schedule(() -> Platform.runLater(() -> notification.hide(() -> notificationVBox.getChildren().remove(notification))), closeTime, timeUnit);
    }
}
