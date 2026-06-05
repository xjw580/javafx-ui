package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.ProgressModal;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ProgressModal 全模式用法示例
 */
public class ProgressModalTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ProgressModal progressModal = new ProgressModal();
        
        // 模式 1：不确定进度模式 (Indeterminate)
        Button btn1 = new Button("1. 不确定进度模式");
        btn1.setOnAction(event -> {
            ProgressModal.ProgressContext context = progressModal.show("正在连接服务器", "请稍候...", true);
            new Thread(() -> {
                try { Thread.sleep(2000); } catch (InterruptedException e) {}
                context.finish();
            }).start();
        });

        // 模式 2：手动更新进度模式
        Button btn2 = new Button("2. 手动更新进度 (带内容更新)");
        btn2.setOnAction(event -> {
            ProgressModal.ProgressContext context = progressModal.show("资源下载", "准备开始...", 0.0);
            new Thread(() -> {
                for (int i = 0; i <= 100; i++) {
                    try { Thread.sleep(30); } catch (InterruptedException e) {}
                    double p = i / 100.0;
                    context.updateProgress(p);
                    context.updateContent("已下载: " + i + "MB / 100MB");
                    if (i == 50) context.updateTitle("下载过半，请保持网络连接");
                }
                context.finish();
            }).start();
        });

        // 模式 3：并发任务模式 (自动入栈)
        Button btn3 = new Button("3. 并发任务演示 (栈模型)");
        btn3.setOnAction(event -> {
            ProgressModal.ProgressContext contextA = progressModal.show("任务 A", "正在执行长任务...", true);
            new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                javafx.application.Platform.runLater(() -> {
                    ProgressModal.ProgressContext contextB = progressModal.show("任务 B (插队)", "我执行得很快", 0.0);
                    new Thread(() -> {
                        for (int i = 0; i <= 100; i += 10) {
                            try { Thread.sleep(100); } catch (InterruptedException e) {}
                            contextB.updateProgress(i / 100.0);
                        }
                        contextB.finish();
                    }).start();
                });
                try { Thread.sleep(3000); } catch (InterruptedException e) {}
                contextA.finish();
            }).start();
        });

        // 模式 4：JavaFX Task 绑定模式 (最推荐)
        Button btn4 = new Button("4. JavaFX Task 自动绑定");
        btn4.setOnAction(event -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    updateTitle("Task 模式演示");
                    for (int i = 0; i < 100; i++) {
                        updateMessage("Task 自动同步中: " + i + "%");
                        updateProgress(i, 100);
                        Thread.sleep(30);
                    }
                    return null;
                }
            };
            progressModal.bindTask(task);
            new Thread(task).start();
        });

        VBox root = new VBox(20, btn1, btn2, btn3, btn4, progressModal);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Scene scene = new Scene(root, 400, 550);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setTitle("ProgressModal Usage Modes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
