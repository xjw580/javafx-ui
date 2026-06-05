package club.xiaojiawei.demo.tab.controls;

import club.xiaojiawei.controls.ProgressModal;
import club.xiaojiawei.controls.Switch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/7/11 12:08
 */
public class ProgressModalController {

    @FXML
    private Switch showProgress;
    @FXML
    private TextArea textArea;
    @FXML
    private ProgressModal progressModal;

    @FXML
    void initialize() {
        textArea.setText("""
                        ProgressModal.ProgressContext progress;
                        if (showProgress.getStatus()) {
                            // 显示（带进度）
                            progress = progressModal.show("加载中", "内容", 0.0);
                        } else {
                            // 显示（不带进度）
                            progress = progressModal.show("加载中", "内容", true);
                        }
                        new Thread(() -> {
                            for (int i = 0; i < 100; i++) {
                                try {
                                    Thread.sleep(15);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                progress.updateProgress(progress.getProgress() + 0.01);
                            }
                            // 结束任务
                            progress.finish();
                        }).start();
                """);
    }

    @FXML
    protected void show(ActionEvent actionEvent) throws InterruptedException {
        ProgressModal.ProgressContext progress;
        if (showProgress.getStatus()) {
            // 显示（带进度）
            progress = progressModal.show("加载中", "内容", 0.0);
        } else {
            // 显示（不带进度）
            progress = progressModal.show("加载中", "内容", true);
        }
        new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                progress.updateProgress(progress.getProgress() + 0.001);
            }
            progress.finish();
        }).start();
    }

}
