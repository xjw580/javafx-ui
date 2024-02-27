package club.xiaojiawei.test;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/26 17:43
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SimpleGraphics extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建 Canvas
        Canvas canvas = new Canvas(400, 300);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 绘制一个矩形
        gc.setFill(Color.GREEN);
        gc.fillRect(50, 50, 200, 100);

        // 绘制一个红色边框的矩形
        gc.setStroke(Color.RED);
        gc.strokeRect(100, 100, 200, 100);

        // 绘制一个文本
        gc.setFill(Color.BLUE);
        gc.fillText("Hello, JavaFX!", 50, 200);

        // 绘制一个椭圆
        gc.setFill(Color.ORANGE);
        gc.fillOval(250, 150, 100, 100);

        // 绘制一个图像
//        Image image = new Image("example.png");
//        gc.drawImage(image, 50, 50);

        // 创建场景
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Simple Graphics Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

