package club.xiaojiawei.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/22 16:37
 */
public class ColorTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        // 创建圆形并应用径向渐变色
        Path gradientPath = createGradientPath();
        root.getChildren().add(gradientPath);

        Scene scene = new Scene(root, 800, 800);
        primaryStage.setTitle("Radial Gradient Path Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private RadialGradient createRadialGradient() {
        Stop[] stops = {
                new Stop(0, Color.GREEN),
                new Stop(0.5, javafx.scene.paint.Color.YELLOW),
                new Stop(1, Color.RED)
        };

        return new RadialGradient(
                0, 0,
                0.5, 0.5,
                0.6,
                true,
                CycleMethod.NO_CYCLE,
                stops
        );
    }
    private Path createGradientPath() {
        Path path = new Path();
        /*起点*/
        path.getElements().add(new MoveTo(200, 200));
        drawOuterCircle(path);
        return path;
    }
    private void drawOuterCircle(Path path){
        ArcTo arcTo1 = new ArcTo();
        arcTo1.setX(600);
        arcTo1.setY(600);
        arcTo1.setRadiusX(200);
        arcTo1.setRadiusY(200);
        ArcTo arcTo2 = new ArcTo();
        arcTo2.setX(200);
        arcTo2.setY(200);
        arcTo2.setRadiusX(200);
        arcTo2.setRadiusY(200);
        path.getElements().addAll(arcTo1, arcTo2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
