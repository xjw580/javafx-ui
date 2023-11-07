package club.xiaojiawei.test;

import club.xiaojiawei.controls.Carousel;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/6 23:43
 */
public class CarouselTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        HBox hBox = new HBox(){{setAlignment(Pos.CENTER);}};
        VBox vBox = new VBox(){{setAlignment(Pos.CENTER);}};
        vBox.getChildren().add(hBox);
        Carousel carousel = new Carousel();
        carousel.setAutoPlay(true);
//        carousel.setImagesURL(FXCollections.observableArrayList("images/1.jpg", "images/2.jpg", "images/3.jpg", "images/4.jpg", "images/5.jpg", "images/6.png"));
        System.out.println(carousel.getImageChildren());
        hBox.getChildren().add(carousel);
        Scene scene = new Scene(vBox, 1500, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
