package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.Carousel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/6 23:43
 */
public class CarouselTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @SneakyThrows
    @Override
    public void start(Stage primaryStage) {
        HBox hBox = new HBox(){{setAlignment(Pos.CENTER);}};
        VBox vBox = new VBox(){{setAlignment(Pos.CENTER);}};
        vBox.getChildren().add(hBox);
        Carousel carousel = new Carousel();
        carousel.setAutoPlay(true);
        carousel.setImagesURL(FXCollections.observableArrayList(
//                "https://zergqueen.gitee.io/images/javafx-ui/carousel1.jpg",
//                "https://zergqueen.gitee.io/images/javafx-ui/carousel2.jpg",
//                "https://zergqueen.gitee.io/images/javafx-ui/carousel3.jpg",
//                "https://zergqueen.gitee.io/images/javafx-ui/carousel4.jpg",
//                "https://zergqueen.gitee.io/images/javafx-ui/carousel7.jpg",
//                "C:\\Users\\Administrator\\Downloads\\carousel7.jpg",
//                "C:\\Users\\Administrator\\Downloads\\carousel7.jpg",
//                "C:\\Users\\Administrator\\Downloads\\carousel7.jpg",
//                "C:\\Users\\Administrator\\Downloads\\carousel7.jpg"
                "/club/xiaojiawei/demo/tab/images/carousel6.jpg",
                "/club/xiaojiawei/demo/tab/images/carousel7.jpg",
                "/club/xiaojiawei/demo/tab/images/carousel2.jpg",
                "/club/xiaojiawei/demo/tab/images/carousel4.jpg"
//                "https://zergqueen.gitee.io/images/javafx-ui/carousel6.jpg"
        ));
        System.out.println(carousel.getImageChildren());
        hBox.getChildren().add(carousel);
        Scene scene = new Scene(vBox, 1500, 800);
        primaryStage.setScene(scene);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.show();
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Platform.runLater(() -> {
//                    carousel.setNudeScale(0D);
//                });
//            }
//        }, 2000);
    }
}
