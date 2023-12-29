package club.xiaojiawei.test;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.stage.Stage;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/28 9:01
 */
public class CurveTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private static double t = 0.001;
    @Override
    public void start(Stage primaryStage) {
// 控制点坐标
        double[] xValues = {0, 100, 200, 300};
        double[] yValues = {0, 300, -100, 200};

        // 创建插值器
        SplineInterpolator splineInterpolator = new SplineInterpolator();
        PolynomialSplineFunction splineFunction = splineInterpolator.interpolate(xValues, yValues);

        Path path = new Path();
        ObservableList<PathElement> elements = path.getElements();
        elements.add(new MoveTo(xValues[0], yValues[0]));

        // 将曲线离散成多个点
        for (double t = 0; t <= 1; t += 0.01) {
            double x = splineFunction.value(t);
            double y = splineFunction.derivative().value(t);
            elements.add(new LineTo(x, y));
        }
        double x, y;
        for (double k = t; k <= 1 + t; k += t) {
            double r = 1 - k;
//            x = Math.pow(r, 3) * keyPointP[0].getX() + 3 * k * Math.pow(r, 2) * keyPointP[1].getX()
//                    + 3 * Math.pow(k, 2) * (1 - k) * keyPointP[2].getX() + Math.pow(k, 3) * keyPointP[3].getX();
//            y = Math.pow(r, 3) * keyPointP[0].getY() + 3 * k * Math.pow(r, 2) * keyPointP[1].getY()
//                    + 3 * Math.pow(k, 2) * (1 - k) * keyPointP[2].getY() + Math.pow(k, 3) * keyPointP[3].getY();
        }
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(path);
        Scene scene = new Scene(stackPane, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
