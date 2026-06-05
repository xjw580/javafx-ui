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
 * 测试 ProgressModal.bindTask()
 */
public class ProgressBindTaskTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ProgressModal progressModal = new ProgressModal();
        Button click = new Button("Run Task");
        
        click.setOnAction(event -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    updateTitle("正在执行任务...");
                    for (int i = 0; i < 100; i++) {
                        updateMessage("已处理 " + i + "%");
                        updateProgress(i, 100);
                        Thread.sleep(20);
                        if (isCancelled()) break;
                    }
                    updateMessage("任务完成！");
                    return null;
                }
            };
            
            progressModal.bindTask(task);
            new Thread(task).start();
        });

        Scene scene = new Scene(new VBox(click, progressModal), 400, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
