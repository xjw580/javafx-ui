package club.xiaojiawei.controls;

import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

/**
 * 进度模态
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/13 16:17
 */
public class ProgressModal extends HBox {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    private final IntegerProperty decimalCount = new SimpleIntegerProperty(1);

    private final DoubleProperty size = new SimpleDoubleProperty(50);

    @Getter
    private Region parentRegion;

    @Getter
    @Setter
    private int transitionDuration = 200;


    public void setParentRegion(Region parentRegion) {
        this.parentRegion = parentRegion;
        this.prefWidthProperty().unbind();
        this.prefHeightProperty().unbind();
        if (parentRegion == null) {
            Parent parent = this;
            while ((parent = parent.getParent()) != null) {
                if (parent instanceof Region region) {
                    this.prefWidthProperty().bind(region.widthProperty());
                    this.prefHeightProperty().bind(region.heightProperty());
                    return;
                }
            }
            if (this.getScene() != null && this.getScene().getWindow() != null) {
                this.prefWidthProperty().bind(this.getScene().widthProperty());
                this.prefHeightProperty().bind(this.getScene().heightProperty());
            }
        } else {
            this.prefWidthProperty().bind(parentRegion.widthProperty());
            this.prefHeightProperty().bind(parentRegion.heightProperty());
        }
    }

    public String getTitle() {
        return title.getText();
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public String getContent() {
        return content.getText();
    }

    public void setContent(String content) {
        this.content.setText(content);
    }

    public int getDecimalCount() {
        return decimalCount.get();
    }

    public IntegerProperty decimalCountProperty() {
        return decimalCount;
    }

    public void setDecimalCount(int decimalCount) {
        this.decimalCount.set(decimalCount);
    }

    public double getSize() {
        return size.get();
    }

    public DoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public ProgressModal() {
        this(null, null);
    }

    @FXML
    private Label progressLabel;
    @FXML
    private Text title;
    @FXML
    private Text content;
    @FXML
    private StackPane progressPane;

    private DoubleProperty progress;

    private ChangeListener<Number> progressListener;

    public ProgressModal(@NamedArg("parent") Region parentRegion, @NamedArg("tip") String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            setParentRegion(parentRegion);
            setTitle(title);
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void afterFXMLLoaded() {
        this.setVisible(false);
        this.parentProperty().addListener((observable, oldValue, newValue) -> {
            if (this.parentRegion == null) {
                setParentRegion(null);
            }
        });
        progressPane.prefHeightProperty().bind(size);
        progressPane.prefWidthProperty().bind(size);
    }

    private void changeProgress(Number progress) {
        if (Platform.isFxApplicationThread()) {
            double v = (progress.doubleValue() * 100);
            v = Math.min(v, 100);
            if (v == 100) {
                this.setVisible(false);
                progressLabel.setText("0%");
            } else {
                progressLabel.setText(String.format("%." + decimalCount.get() + "f", v) + "%");
            }
        } else {
            Platform.runLater(() -> changeProgress(progress));
        }
    }

    /**
     * 显示加载模态，隐藏进度
     * @return 进度控制器
     */
    public DoubleProperty show() {
        return show(title.getText(), -1D);
    }

    /**
     * 显示加载模态，隐藏进度
     * @param tip 提示信息
     * @return 进度控制器
     */
    public DoubleProperty show(String tip) {
        return show(tip, -1D);
    }

    public DoubleProperty show(String title, String content, double progress) {
        if (this.progress != null && this.progress.get() < 1) {
            return new SimpleDoubleProperty(progress);
        }
        this.progress = new SimpleDoubleProperty(this, "progress");
        this.progress.addListener(progressListener = (observable, oldValue, newValue) -> {
            changeProgress(newValue);
        });
        if (progress >= 1) {
            return this.progress;
        } else progressLabel.setVisible(!(progress < 0));
        progressLabel.setText("0%");

        setTitle(title);
        setContent(content);

        progress = Math.min(progress, 1D);
        progress = Math.max(0D, progress);
        this.progress.set(progress);
        this.setVisible(true);
        BaseTransitionEnum.FADE.play(this, 0, 1, Duration.millis(transitionDuration));
        return this.progress;
    }

    /**
     * 显示加载模态
     * @param title
     * @param progress [0, 1] 小于0时隐藏进度
     * @return 进度控制器
     */
    public DoubleProperty show(String title, double progress) {
        return show(title, null, progress);
    }

    public DoubleProperty show(String title, String content) {
        return show(title, content, 0D);
    }

    /**
     * 显示加载模态
     * @return 进度控制器
     */
    public DoubleProperty showByZero() {
        return show(title.getText(), 0D);
    }

    /**
     * 显示加载模态
     * @param title
     * @return 进度控制器
     */
    public DoubleProperty showByZero(String title) {
        return show(title, 0D);
    }

    public DoubleProperty showByZero(String title, String content) {
        return show(title, content, 0D);
    }

    /**
     * 隐藏加载模态
     * @param progress 进度控制器
     */
    public void hide(DoubleProperty progress) {
        if (progress == null) {
            return;
        }
        progress.set(1D);
        if (progressListener != null) {
            progress.removeListener(progressListener);
        }
        if (progress == this.progress) {
            this.progress = null;
            progressListener = null;
        }
    }

    public boolean isShowing() {
        return isVisible();
    }

}
