package club.xiaojiawei.test;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class PropertiesTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static Stage stage;

    @FXML void initialize(){
        System.out.println("initialize");
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        PropertiesTest.stage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("club.xiaojiawei.test.application"));
        Parent root = loader.load(getClass().getResourceAsStream("PropertiesTest.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void changeEnglish(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        //        properties文件名：application.properties_us，处于club.xiaojiawei.test包下
        loader.setResources(ResourceBundle.getBundle("club.xiaojiawei.test.application_us"));
        Parent root = loader.load(getClass().getResourceAsStream("PropertiesTest.fxml"));
        stage.getScene().setRoot(root);
    }

    public void changeChinese(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        //        properties文件名：application.properties，处于club.xiaojiawei.test包下
        loader.setResources(ResourceBundle.getBundle("club.xiaojiawei.test.application"));
        Parent root = loader.load(getClass().getResourceAsStream("PropertiesTest.fxml"));
        stage.getScene().setRoot(root);
    }

}
