package club.xiaojiawei.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/12/25 10:49
 */
public class D3Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws URISyntaxException, MalformedURLException {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        String string = new File(getClass().getResource("/club/xiaojiawei/test/test.html").toURI()).toURL().toString();
        webEngine.load(string);
        Scene scene = new Scene(webView, 1000, 600);

        primaryStage.setTitle("D3.js in JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
