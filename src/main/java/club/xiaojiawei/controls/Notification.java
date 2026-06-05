package club.xiaojiawei.controls;

import club.xiaojiawei.annotations.ValidSizeRange;
import club.xiaojiawei.enums.NotificationTypeEnum;
import club.xiaojiawei.enums.SizeEnum;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * 通知组件
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 22:54
 */
@Slf4j
public class Notification<T> extends Pane {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 通知类型
     */
    @Getter
    private NotificationTypeEnum type = NotificationTypeEnum.INFO;
    /**
     * 是否显示关闭通知按钮
     */
    @Getter
    private boolean showingCloseBtn = true;
    /**
     * 通知的尺寸
     */
    @Getter
    @ValidSizeRange({SizeEnum.TINY, SizeEnum.SMALL, SizeEnum.MEDDLE, SizeEnum.DEFAULT, SizeEnum.BIG})
    private SizeEnum size = SizeEnum.DEFAULT;

    /**
     * 内容最大宽度，超过此宽度将换行
     */
    private final DoubleProperty contentMaxWidth;
    /**
     * 通知标题
     */
    private final StringProperty title;
    /**
     * 通知内容
     */
    private final ObjectProperty<T> content = new SimpleObjectProperty<>();

    /**
     * 关闭请求回调
     */
    @Setter
    private Consumer<Notification<T>> onRequestClose;

    public void setShowingCloseBtn(boolean showingCloseBtn) {
        this.showingCloseBtn = showingCloseBtn;
        closeIcoPane.setManaged(showingCloseBtn);
        closeIcoPane.setVisible(showingCloseBtn);
    }

    public void setSize(SizeEnum size) {
        if (size == null) return;
        this.size = size;
        switch (size) {
            case BIG -> {
                removeSizeStyleClass();
                notificationVBox.getStyleClass().add("notificationVBox-big");
            }
            case MEDDLE, DEFAULT -> removeSizeStyleClass();
            case SMALL -> {
                removeSizeStyleClass();
                notificationVBox.getStyleClass().add("notificationVBox-small");
            }
            case TINY -> {
                removeSizeStyleClass();
                notificationVBox.getStyleClass().add("notificationVBox-tiny");
            }
        }
    }

    public double getContentMaxWidth() {
        return contentMaxWidth.get();
    }

    public DoubleProperty contentMaxWidthProperty() {
        return contentMaxWidth;
    }

    public void setContentMaxWidth(double contentMaxWidth) {
        this.contentMaxWidth.set(contentMaxWidth);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public T getContent() {
        return content.get();
    }

    public ObjectProperty<T> contentProperty() {
        return content;
    }

    public void setContent(T content) {
        this.content.set(content);
    }

    public void setType(NotificationTypeEnum type) {
        if (type == null) return;
        this.type = type;
        tipIcoPane.getChildren().setAll(type.getBuilder().get());
        notificationVBox.setStyle("-fx-border-color: " + type.getColor());
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public Notification() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            contentMaxWidth = bottomHBox.maxWidthProperty();
            title = titleLabel.textProperty();
            content.addListener((observable, oldValue, newValue) -> {
                if (newValue instanceof Node node) {
                    contentLabel.setGraphic(node);
                    contentLabel.setText("");
                } else {
                    contentLabel.setGraphic(null);
                    contentLabel.setText(newValue == null ? "" : newValue.toString());
                }
            });

            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Notification(String title) {
        this();
        setTitle(title);
    }

    public Notification(NotificationTypeEnum type, String title) {
        this(title);
        setType(type);
    }

    public Notification(String title, T content) {
        this(title);
        setContent(content);
    }

    public Notification(NotificationTypeEnum type, String title, T content) {
        this(title, content);
        setType(type);
    }

    @FXML
    private Label titleLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private HBox bottomHBox;
    @FXML
    private StackPane closeIcoPane;
    @FXML
    private StackPane tipIcoPane;
    @FXML
    private VBox notificationVBox;

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded() {
//        this.setVisible(false);
//        this.setManaged(false);
//        this.setOpacity(0D);
        addListener();
    }

    public void updateTextMaxWidth(Scene scene) {
        if (scene == null) return;
        Parent parent = getParent();
        while (parent != null) {
            if (parent instanceof Region region) {
                if (region.getWidth() > 0 && !region.getStyleClass().contains(NotificationManager.NOTIFICATION_PANE_STYLE_CLASS)) {
                    double width = Math.max(region.getWidth() - (70 + 14 + 14), 20);
                    titleLabel.setMaxWidth(Math.max(width, -1));
                    contentLabel.setMaxWidth(Math.max(width, -1));
                    break;
                } else {
                    parent = region.getParent();
                }
            } else {
                break;
            }
        }
    }

    private void addListener() {
        closeIcoPane.setOnMouseClicked(mouseEvent -> {
            if (onRequestClose != null) {
                onRequestClose.accept(this);
            }
        });
        content.addListener((observableValue, s, t1) -> {
            if (t1 == null || t1.toString().isBlank()) {
                bottomHBox.setManaged(false);
                bottomHBox.setVisible(false);
            } else {
                bottomHBox.setManaged(true);
                bottomHBox.setVisible(true);
            }
        });
    }

    private void removeSizeStyleClass() {
        notificationVBox.getStyleClass().removeIf(s -> s.startsWith("notificationVBox-"));
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/
}
