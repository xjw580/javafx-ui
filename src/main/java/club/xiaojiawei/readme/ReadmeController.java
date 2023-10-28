package club.xiaojiawei.readme;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/27 19:29
 */
public class ReadmeController implements Initializable {

    @FXML
    private ProgressBar progressBarUI;
    @FXML
    private ProgressIndicator progressIndicatorUI;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleWithFixedDelay(() -> Platform.runLater(() -> {
            progressBarUI.setProgress(progressBarUI.getProgress() % 1 + 0.001);
            progressIndicatorUI.setProgress(progressIndicatorUI.getProgress() % 1 + 0.001);
        }), 100, 2, TimeUnit.MILLISECONDS);
    }
}
