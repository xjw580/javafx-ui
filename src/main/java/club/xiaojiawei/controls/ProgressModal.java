package club.xiaojiawei.controls;

import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

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

    private final Deque<ProgressContext> activeContexts = new ArrayDeque<>();

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
    private ProgressIndicator progressIndicator;
    @FXML
    private Text title;
    @FXML
    private Text content;
    @FXML
    private StackPane progressPane;

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

    private void refreshUI() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::refreshUI);
            return;
        }

        if (activeContexts.isEmpty()) {
            this.setVisible(false);
            return;
        }

        ProgressContext context = activeContexts.peek();
        setTitle(context.title);
        setContent(context.content);

        if (context.indeterminate || context.progress < 0) {
            progressLabel.setVisible(false);
            progressIndicator.setProgress(-1);
        } else {
            progressLabel.setVisible(true);
            double v = Math.min(context.progress, 1.0);
            progressIndicator.setProgress(v);
            progressLabel.setText(String.format("%." + decimalCount.get() + "f", v * 100) + "%");
        }

        if (!this.isVisible()) {
            this.setVisible(true);
            BaseTransitionEnum.FADE.play(this, 0, 1, Duration.millis(transitionDuration));
        }
    }

    /**
     * 显示加载模态（不确定进度）
     * @return 进度上下文
     */
    public ProgressContext show() {
        return show(title.getText(), content.getText(), true);
    }

    /**
     * 显示加载模态（不确定进度）
     * @param title 标题
     * @return 进度上下文
     */
    public ProgressContext show(String title) {
        return show(title, content.getText(), true);
    }

    /**
     * 显示加载模态
     * @param title 标题
     * @param progress 初始进度 [0, 1]，负数表示不确定进度
     * @return 进度上下文
     */
    public ProgressContext show(String title, double progress) {
        return show(title, content.getText(), progress);
    }

    /**
     * 显示加载模态
     * @param title 标题
     * @param content 内容
     * @param progress 初始进度 [0, 1]，负数表示不确定进度
     * @return 进度上下文
     */
    public ProgressContext show(String title, String content, double progress) {
        return show(title, content, progress < 0, progress);
    }

    /**
     * 显示加载模态
     * @param title 标题
     * @param content 内容
     * @param indeterminate 是否不确定进度
     * @return 进度上下文
     */
    public ProgressContext show(String title, String content, boolean indeterminate) {
        return show(title, content, indeterminate, indeterminate ? -1 : 0);
    }

    private ProgressContext show(String title, String content, boolean indeterminate, double progress) {
        ProgressContext context = new ProgressContext(title, content, indeterminate, progress);
        activeContexts.push(context);
        refreshUI();
        return context;
    }

    /**
     * 绑定 JavaFX Task，自动同步进度、标题和消息
     * @param task 要绑定的任务
     * @return 进度上下文
     */
    public ProgressContext bindTask(Task<?> task) {
        ProgressContext context = show(task.getTitle(), task.getMessage(), true);

        task.titleProperty().addListener((obs, old, val) -> context.updateTitle(val));
        task.messageProperty().addListener((obs, old, val) -> context.updateContent(val));
        task.progressProperty().addListener((obs, old, val) -> {
            if (val.doubleValue() < 0) {
                context.setIndeterminate(true);
            } else {
                context.setIndeterminate(false);
                context.updateProgress(val.doubleValue());
            }
        });

        task.setOnSucceeded(e -> context.finish());
        task.setOnFailed(e -> context.finish());
        task.setOnCancelled(e -> context.finish());

        return context;
    }

    public boolean isShowing() {
        return isVisible();
    }

    /**
     * 进度上下文，用于更新进度模态的状态
     */
    public class ProgressContext {
        private String title;
        private String content;
        private boolean indeterminate;
        private double progress;
        private boolean finished;
        private final List<Runnable> finishCallbacks = new ArrayList<>();

        private ProgressContext(String title, String content, boolean indeterminate, double progress) {
            this.title = title;
            this.content = content;
            this.indeterminate = indeterminate;
            this.progress = progress;
        }

        private void runOnFxThread(Runnable runnable) {
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            } else {
                Platform.runLater(runnable);
            }
        }

        public void updateTitle(String title) {
            this.title = title;
            refreshUI();
        }

        public void updateContent(String content) {
            this.content = content;
            refreshUI();
        }

        public void incrementProgress(double progress) {
            updateProgress(this.progress + progress);
        }

        /**
         * 增加进度
         * @param progressDelta 需要增加的进度
         * @param upperBound 最大进度上限
         */
        public void incrementProgress(double progressDelta, double upperBound) {
            updateProgress(Math.min(this.progress + progressDelta, upperBound));
        }

        public void updateProgress(double progress) {
            this.progress = progress;
            this.indeterminate = false;
            if (progress >= 1.0) {
                finish();
            } else {
                refreshUI();
            }
        }

        public void setIndeterminate(boolean indeterminate) {
            this.indeterminate = indeterminate;
            refreshUI();
        }

        /**
         * 结束时在ui线程回调，只会触发一次
         * @param callback
         */
        public void onFinished(Runnable callback) {
            onFinished(true, callback);
        }

        /**
         * 结束时在ui线程回调，只会触发一次
         * @param runIfAlreadyFinished 已经结束是否回调
         * @param callback
         */
        public void onFinished(boolean runIfAlreadyFinished, Runnable callback) {
            if (callback == null) return;

            if (finished) {
                if (runIfAlreadyFinished) {
                    runOnFxThread(callback);
                }
                return;
            }

            finishCallbacks.add(callback);
        }

        public void finish() {
            if (finished) return;

            finished = true;
            activeContexts.remove(this);
            refreshUI();

            List<Runnable> callbacks = new ArrayList<>(finishCallbacks);
            finishCallbacks.clear();

            for (Runnable callback : callbacks) {
                runOnFxThread(callback);
            }
        }

        public boolean isIndeterminate() {
            return indeterminate;
        }

        public boolean isFinished() {
            return finished;
        }

        public double getProgress() {
            return progress;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

}
