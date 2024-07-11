package club.xiaojiawei.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * 具有复制功能的Label，点击此组件将复制到剪切板
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/7/10 15:49
 */
public class CopyLabel extends Label {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    private final ObjectProperty<NotificationManager<Object>> notificationManager = new SimpleObjectProperty<>();

    /**
     * copyTextProperty.get()值为空时，复制text
     */
    private final StringProperty copyTextProperty = new SimpleStringProperty();

    public String getCopyText() {
        return copyTextProperty.get();
    }

    public StringProperty copyTextProperty() {
        return copyTextProperty;
    }

    public void setCopyText(String copyTextProperty) {
        this.copyTextProperty.set(copyTextProperty);
    }

    public NotificationManager<Object> getNotificationManager() {
        return notificationManager.get();
    }

    public ObjectProperty<NotificationManager<Object>> notificationManagerProperty() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager<Object> notificationManagerObjectProperty) {
        this.notificationManager.set(notificationManagerObjectProperty);
    }

    {
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, createEventHandler());
        setCursor(Cursor.HAND);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public CopyLabel() {
        super();
    }

    public CopyLabel(String text) {
        super(text);
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public EventHandler<MouseEvent> createEventHandler() {
        return event -> {
            if (getCopyText() == null || getCopyText().isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(getText()), null);
            }else {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(getCopyText()), null);
            }
            if (notificationManager.get() != null) {
                notificationManager.get().showSuccess("已复制到剪切板", 1);
            }
        };
    }

}
