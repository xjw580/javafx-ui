package club.xiaojiawei.controls;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.enums.NotificationPosEnum;
import club.xiaojiawei.enums.NotificationTypeEnum;
import club.xiaojiawei.factory.NotificationFactory;
import club.xiaojiawei.func.MarkLogging;
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
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.config.JavaFXUIThreadPoolConfig.V_THREAD_POOL;

/**
 * 通知管理器
 *
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/2 10:00
 */
@DefaultProperty("notificationFactory")
public class NotificationManager<T> extends HBox implements MarkLogging {
    private static final Logger log = LoggerFactory.getLogger(NotificationManager.class);

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
            if (t1 != null) {
                sceneProperty().removeListener(sceneListener);
                sceneListener = null;
                changeNotificationPos();
            }
        });
    }

    private ChangeListener<Scene> sceneListener;

    public static final String NOTIFICATION_PANE_STYLE_CLASS = "notification-pane";

    private final VBox notificationVBox = new VBox(){{
        getStyleClass().add(NOTIFICATION_PANE_STYLE_CLASS);
    }};

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void changeNotificationPos() {
        if (getScene() == null) {
            return;
        }
        Pane rootPane = null;
        Parent rootParent = this;
        while (rootParent.getParent() != null) {
            rootParent = rootParent.getParent();
            if (!autoTop) {
                break;
            }
            if (rootParent instanceof Pane pane) {
                rootPane = pane;
            }
        }

        layoutXProperty().unbind();
        layoutYProperty().unbind();

        ReadOnlyDoubleProperty widthProperty;
        ReadOnlyDoubleProperty heightProperty;
        if (rootPane == null) {
            if (rootParent instanceof Pane pane) {
                widthProperty = pane.widthProperty();
                heightProperty = pane.heightProperty();
            } else {
                widthProperty = getScene().widthProperty();
                heightProperty = getScene().heightProperty();
            }
        } else {
            if (this.getParent() instanceof Pane pane) {
                pane.getChildren().remove(this);
            }
            rootPane.getChildren().add(this);
            widthProperty = rootPane.widthProperty();
            heightProperty = rootPane.heightProperty();
        }
        switch (notificationPos) {
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

    public Notification<T> showInfo(String title, T content, long closeTime) {
        return show(notificationFactory.ofNew(title, content), closeTime);
    }

    public Notification<T> showInfo(String title, T content) {
        return showInfo(title, content, -1L);
    }

    public Notification<T> showInfo(String title, long closeTime) {
        return showInfo(title, null, closeTime);
    }

    public Notification<T> showInfo(String title) {
        return showInfo(title, null);
    }

    public Notification<T> showSuccess(String title, T content, long closeTime) {
        return show(notificationFactory.ofNew(NotificationTypeEnum.SUCCESS, title, content), closeTime);
    }

    public Notification<T> showSuccess(String title, T content) {
        return showSuccess(title, content, -1L);
    }

    public Notification<T> showSuccess(String title, long closeTime) {
        return showSuccess(title, null, closeTime);
    }

    public Notification<T> showSuccess(String title) {
        return showSuccess(title, null);
    }

    public Notification<T> showWarn(String title, T content, long closeTime) {
        return show(notificationFactory.ofNew(NotificationTypeEnum.WARN, title, content), closeTime);
    }

    public Notification<T> showWarn(String title, T content) {
        return showWarn(title, content, -1L);
    }

    public Notification<T> showWarn(String title, long closeTime) {
        return showWarn(title, null, closeTime);
    }

    public Notification<T> showWarn(String title) {
        return showWarn(title, null);
    }

    public Notification<T> showError(String title, T content, long closeTime) {
        return show(notificationFactory.ofNew(NotificationTypeEnum.ERROR, title, content), closeTime);
    }

    public Notification<T> showError(String title, T content) {
        return showError(title, content, -1L);
    }

    public Notification<T> showError(String title, long closeTime) {
        return showError(title, null, closeTime);
    }

    public Notification<T> showError(String title) {
        return showError(title, null);
    }

    public Notification<T> show(Notification<T> notification) {
        if (notificationVBox.getChildren().size() >= maxCount) {
            return notification;
        }
        notificationVBox.getChildren().add(notification);
        notification.setOnCloseEvent(() -> notificationVBox.getChildren().remove(notification));
        notification.show();
        if (JavaFXUI.getLogMark() != null) {
            switch (notification.getType()) {
                case INFO, SUCCESS ->
                        log.info(JavaFXUI.getLogMark(), "通知:{ title: {}, content: {}}", notification.getTitle(), notification.getContent());
                case WARN ->
                        log.warn(JavaFXUI.getLogMark(), "通知:{ title: {}, content: {}}", notification.getTitle(), notification.getContent());
                case ERROR ->
                        log.error(JavaFXUI.getLogMark(), "通知:{ title: {}, content: {}}", notification.getTitle(), notification.getContent());
            }
        }
        return notification;
    }

    public Notification<T> show(Notification<T> notification, long closeSecTime) {
        return show(notification, closeSecTime, TimeUnit.SECONDS);
    }

    public void hide(Notification<T> notification) {
        notification.hide();
    }

    public Notification<T> show(Notification<T> notification, long closeTime, TimeUnit timeUnit) {
        show(notification);
        if (closeTime > 0) {
            V_THREAD_POOL.submit(() -> {
                try {
                    timeUnit.sleep(closeTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                hide(notification);
            });
        }
        return notification;
    }

}
