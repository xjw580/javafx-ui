package club.xiaojiawei.controls;

import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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
                    break;
                }
            }
        }else {
            this.prefWidthProperty().bind(parentRegion.widthProperty());
            this.prefHeightProperty().bind(parentRegion.heightProperty());
        }
    }

    public String getTip(){
        return tip.getText();
    }

    public void setTip(String tip){
        this.tip.setText(tip);
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
    private Text tip;

    private DoubleProperty progress;

    private ChangeListener<Number> progressListener;

    public ProgressModal(@NamedArg("parent") Region parentRegion, @NamedArg("tip") String tip){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            setParentRegion(parentRegion);
            setTip(tip);
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
    }

    private void changeProgress(Number progress) {
        if (Platform.isFxApplicationThread()) {
            int v = (int) (progress.doubleValue() * 100);
            v = Math.min(v, 100);
            if (v == 100) {
                this.setVisible(false);
                progressLabel.setText("0%");
            }else {
                progressLabel.setText(v + "%");
            }
        }else {
            Platform.runLater(() -> changeProgress(progress));
        }
    }

    public DoubleProperty show(){
        return show(tip.getText(), -1D);
    }

    public DoubleProperty show(String tip){
        return show(tip, -1D);
    }

    public DoubleProperty show(String tip, double progress){
        if (this.progress != null) {
            this.progress.removeListener(progressListener);
        }
        this.progress = new SimpleDoubleProperty();
        this.progress.addListener(progressListener = (observable, oldValue, newValue) -> {
            changeProgress(newValue);
        });
        if (progress >= 1) {
            return this.progress;
        } else progressLabel.setVisible(!(progress < 0));
        progressLabel.setText("0%");
        setTip(tip);
        progress = Math.min(progress, 1D);
        progress = Math.max(0D, progress);
        this.progress.set(progress);
        this.setVisible(true);
        BaseTransitionEnum.FADE.play(this, 0, 1, Duration.millis(transitionDuration));
        return this.progress;
    }

    public DoubleProperty showByZero(){
        return show(tip.getText(), 0D);
    }

    public DoubleProperty showByZero(String tip){
        return show(tip, 0D);
    }

    public void hide(DoubleProperty progress){
        if (progress == null) {
            return;
        }
        progress.set(1D);
        progress.removeListener(progressListener);
    }

}
