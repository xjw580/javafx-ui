package club.xiaojiawei.demo.tab.controls;

import club.xiaojiawei.controls.Modal;
import club.xiaojiawei.controls.ProgressModal;
import club.xiaojiawei.controls.Switch;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
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

    @FXML void initialize(){
        textArea.setText("""
        DoubleProperty progress;
        if (showProgress.getStatus()) {
//            显示（带进度）
            progress = progressModal.showByZero("加载中");
        }else {
//            显示（不带进度）
            progress = progressModal.show("加载中");
        }
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                progress.set(progress.getValue() + 0.01);
            }
        }).start();
//        隐藏方式1
//        progress.setValue(1);
//        隐藏方式2
//        progressModal.hide(progress);
        """);
    }

    @FXML
    protected void show(ActionEvent actionEvent) throws InterruptedException {
        DoubleProperty progress;
        if (showProgress.getStatus()) {
//            显示（带进度）
            progress = progressModal.showByZero("加载中");
        }else {
//            显示（不带进度）
            progress = progressModal.show("加载中");
        }
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                progress.set(progress.getValue() + 0.01);
            }
        }).start();
//        隐藏方式1
//        progress.setValue(1);
//        隐藏方式2
//        progressModal.hide(progress);
    }

}
