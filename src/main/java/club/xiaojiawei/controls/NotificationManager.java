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
 * 通知管理器
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/2 10:00
 */
@DefaultProperty("notificationFactory")
public class NotificationManager<T> extends HBox {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

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
    /**
     * 自动将本组件移至最父级pane的子元素中
     */
    @Getter
    private boolean autoTop = true;

    public void setAutoTop(boolean autoTop) {
        this.autoTop = autoTop;
        changeNotificationPos();
    }

    /**
     * 通知工厂
     */
    @Getter
    @Setter
    private NotificationFactory<T> notificationFactory = new NotificationFactory<>();

    public void setNotificationPos(NotificationPosEnum notificationPos) {
        this.notificationPos = notificationPos;
        changeNotificationPos();
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public NotificationManager() {
        setManaged(false);
        setPickOnBounds(false);
        notificationVBox.setPickOnBounds(false);
        getChildren().add(notificationVBox);
        sceneProperty().addListener(sceneListener = (observableValue, aBoolean, t1) -> {
            if (t1 != null){
                sceneProperty().removeListener(sceneListener);
                sceneListener = null;
                changeNotificationPos();
            }
        });
    }

    private ChangeListener<Scene> sceneListener;

    private final VBox notificationVBox = new VBox();

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void changeNotificationPos(){
        if (getScene() == null){
            return;
        }
        Pane rootPane = null;
        Parent rootParent = this;
        while (rootParent.getParent() != null){
            rootParent = rootParent.getParent();
            if (!autoTop) {
                break;
            }
            if (rootParent instanceof Pane pane){
                rootPane = pane;
            }
        }

        layoutXProperty().unbind();
        layoutYProperty().unbind();

        ReadOnlyDoubleProperty widthProperty;
        ReadOnlyDoubleProperty heightProperty;
        if (rootPane == null){
            if (rootParent instanceof Pane pane) {
                widthProperty = pane.widthProperty();
                heightProperty = pane.heightProperty();
            }else {
                widthProperty = getScene().widthProperty();
                heightProperty = getScene().heightProperty();
            }
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

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public void showInfo(String title, T content, long closeTime){
        show(notificationFactory.ofNew(title, content), closeTime);
    }
    public void showInfo(String title, T content){
        showInfo(title, content, -1L);
    }
    public void showInfo(String title, long closeTime){
        showInfo(title, null, closeTime);
    }
    public void showInfo(String title){
        showInfo(title, null);
    }

    public void showSuccess(String title, T content, long closeTime){
        show(notificationFactory.ofNew(NotificationTypeEnum.SUCCESS, title, content), closeTime);
    }
    public void showSuccess(String title, T content){
        showSuccess(title, content, -1L);
    }
    public void showSuccess(String title, long closeTime){
        showSuccess(title, null, closeTime);
    }
    public void showSuccess(String title){
        showSuccess(title, null);
    }

    public void showWarn(String title, T content, long closeTime){
        show(notificationFactory.ofNew(NotificationTypeEnum.WARN, title, content), closeTime);
    }
    public void showWarn(String title, T content){
        showWarn(title, content, -1L);
    }
    public void showWarn(String title, long closeTime){
        showWarn(title, null, closeTime);
    }
    public void showWarn(String title){
        showWarn(title ,null);
    }

    public void showError(String title, T content, long closeTime){
        show(notificationFactory.ofNew(NotificationTypeEnum.ERROR, title, content), closeTime);
    }
    public void showError(String title, T content){
        showError(title, content, -1L);
    }
    public void showError(String title, long closeTime){
        showError(title, null, closeTime);
    }
    public void showError(String title){
        showError(title, null);
    }

    public void show(Notification<T> notification){
        if (notificationVBox.getChildren().size() >= maxCount){
            return;
        }
        notificationVBox.getChildren().add(notification);
        notification.setOnCloseEvent(() -> notificationVBox.getChildren().remove(notification));
        notification.show();
    }
    public void show(Notification<T> notification, long closeSecTime){
        show(notification, closeSecTime, TimeUnit.SECONDS);
    }
    public void show(Notification<T> notification, long closeTime, TimeUnit timeUnit){
        show(notification);
        if (closeTime > 0){
            SCHEDULED_POOL.schedule(() -> Platform.runLater(() -> notification.hide(() -> notificationVBox.getChildren().remove(notification))), closeTime, timeUnit);
        }
    }

}
