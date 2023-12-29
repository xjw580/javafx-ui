package club.xiaojiawei.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.Data;
import netscape.javascript.JSObject;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/25 10:49
 */
public class D3Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private static WebEngine webEngine;
    private int[] subs = {0, 10, 20, 30, 40, 50, 60};
    @Override
    public void start(Stage primaryStage) throws URISyntaxException, MalformedURLException {
        WebView webView = new WebView();
        webView.setMaxHeight(500);
        webView.setMaxWidth(500);
        webEngine = webView.getEngine();
        String string = new File(getClass().getResource("/club/xiaojiawei/test/test.html").toURI()).toURL().toString();
        webEngine.load(string);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(webView);
        Scene scene = new Scene(stackPane, 1000, 600);
        primaryStage.setTitle("D3.js in JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
        webEngine.getLoadWorker().stateProperty().addListener((observableValue, state, t1) -> {
            if (t1 == Worker.State.SUCCEEDED){
                webEngine.setJavaScriptEnabled(true);
                double step = 10D;
                double[][] values = new double[40][40];
                values[20][20] = 60;
                for (int y = 0; y < values.length; y++) {
                    for (int x = 0; x < values[y].length; x++) {
                        int insideCircle = isInsideCircle(x, y, 20, 20);
                        if (insideCircle < 7){
                            values[x][y] = 60 - subs[insideCircle];
                        }
                    }
                }
                D3Data d3Data = new D3Data(values);
                long start = System.currentTimeMillis();
                webEngine.executeScript(String.format("draw(%s)", d3Data));
                System.out.println(System.currentTimeMillis() - start + "ms");
            }
        });
    }
    private int isInsideCircle(int x, int y, double centerX, double centerY) {
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        return (int) distance;
    }
}
@Data
class D3Data{
    private double[][] values;

    public D3Data(double[][] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        if (values == null || values.length == 0 || values[0].length == 0){
            return "";
        }
        StringBuilder builder = new StringBuilder("{width:");
        builder.append(values[0].length);
        builder.append(",height:");
        builder.append(values.length);
        builder.append(",values:[");
        for (double[] value : values) {
            for (double v : value) {
                builder.append(v);
                builder.append(",");
            }
        }
        builder.append("]}");
        return builder.toString();
    }

}