package club.xiaojiawei.controls;

import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.controls.config.ThreadPoolConfig.SCHEDULED_POOL;
import static club.xiaojiawei.enums.BaseTransitionEnum.SCALE;
import static club.xiaojiawei.enums.BaseTransitionEnum.SLIDE_X;

/**
 * 轮播图
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/6 23:39
 */
public class Carousel extends AnchorPane {
    private int currentIndex = 0;
    private ObservableList<String> imagesURL;
    private boolean autoPlay;

    public boolean isAutoPlay() {
        return autoPlay;
    }
    public void setAutoPlay(boolean autoPlay) {
        if (this.autoPlay = autoPlay){
            if (autoPlaySchedule != null){
                autoPlaySchedule.cancel(true);
            }
            autoPlaySchedule = SCHEDULED_POOL.scheduleAtFixedRate(() -> {
                if (!isHoverImage){
                    next();
                }
            }, 5, 5, TimeUnit.SECONDS);
        }else {
            if (autoPlaySchedule != null){
                autoPlaySchedule.cancel(true);
                autoPlaySchedule = null;
            }
        }
    }

    /**
     * @return ObservableList<AnchorPane> AnchorPane的背景为图片
     */
    public ObservableList<Node> getImageChildren() {
        return images.getChildren();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public ObservableList<String> getImagesURL() {
        return imagesURL;
    }
    public void setImagesURL(ObservableList<String> imagesURL) {
        if (imagesURL.size() < 3){
            throw new RuntimeException("图片个数最少3个");
        }
        this.imagesURL = imagesURL;
        for (int i = imagesURL.size() - 2; i >= 2; i--) {
            images.getChildren().add(buildImage(imagesURL.get(i), i));
        }
        AnchorPane leftImage = buildImage(imagesURL.get(imagesURL.size() - 1), imagesURL.size() - 1);
        leftImage.setTranslateX(0D);
        images.getChildren().add(leftImage);
        AnchorPane middleImage = buildImage(imagesURL.get(1), 1);
        middleImage.setTranslateX(400D);
        images.getChildren().add(middleImage);
        AnchorPane rightImage = buildImage(imagesURL.get(0), 0);
        rightImage.setScaleX(1.25D);
        rightImage.setScaleY(1.25D);
        images.getChildren().add(rightImage);
        for (int i = 0; i < imagesURL.size(); i++) {
            Circle dot;
            if (i == 0){
                dots.getChildren().add(dot = new Circle(){{setRadius(3D);getStyleClass().addAll("dot", "currentDot");}});
            }else {
                dots.getChildren().add(dot = new Circle(){{setRadius(3D);getStyleClass().add("dot");}});
            }
            int finalI = i;
            dot.setOnMouseClicked(event -> {
                if (finalI > currentIndex){
                    next(finalI - currentIndex);
                }else if (finalI < currentIndex){
                    last(currentIndex - finalI);
                }
            });
        }
    }
    private AnchorPane buildImage(String url, int index){
        AnchorPane image = new AnchorPane(){{
            getStyleClass().add("image");
            setPrefWidth(400D);
            setPrefHeight(200D);
            setTranslateX(200D);
            if (url.startsWith("http")){
                setStyle("-fx-background-image: url(" + url + ");");
            }else {
                setStyle("-fx-background-image: url(" + Objects.requireNonNull(getClass().getResource(url)).toExternalForm() + ");");
            }
        }};
        image.setClip(new Rectangle(){{
            setArcHeight(15D);
            setArcWidth(15D);
            setWidth(400D);
            setHeight(200D);
        }});
        image.setOnMouseClicked(event -> {
            if (index == (currentIndex + 1) % imagesURL.size()){
                next();
            }else if (index == (currentIndex - 1 + imagesURL.size()) % imagesURL.size()){
                last();
            }
        });
        return image;
    }
    @FXML
    private HBox left;
    @FXML
    private HBox right;
    @FXML
    private AnchorPane images;
    @FXML
    private HBox dots;
    private final ObservableList<Node> imageChildren;
    private static final long TIME = 400L;
    private static final Duration DURATION = Duration.millis(TIME);
    private boolean isPlaying;
    private ScheduledFuture<?> autoPlaySchedule;
    private boolean isHoverImage;
    public Carousel() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            imageChildren = images.getChildren();
            addListener();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void addListener(){
        left.setOnMouseClicked(event -> last());
        right.setOnMouseClicked(event -> next());
        images.hoverProperty().addListener((observable, oldValue, newValue) -> {
            isHoverImage = newValue;
        });
    }
    private void next(){
        next(1);
    }
    private void next(int count){
        if (isPlaying || imagesURL == null || imagesURL.size() < 3 || count == 0){
            return;
        }
        isPlaying = true;
        ParallelTransition transition = new ParallelTransition();
        transition.getChildren().addAll(
                SLIDE_X.get(imageChildren.get(imageChildren.size() - 1), 0, DURATION),
                SCALE.get(imageChildren.get(imageChildren.size() - 1), 1, DURATION),
                SLIDE_X.get(imageChildren.get(imageChildren.size() - 2), 200, DURATION),
                SCALE.get(imageChildren.get(imageChildren.size() - 2), 1.25, DURATION)
        );
        if (imageChildren.size() == 3){
            transition.getChildren().add(SLIDE_X.get(imageChildren.get(0), 400, DURATION));
        }else {
            transition.getChildren().addAll(
                    SLIDE_X.get(imageChildren.get(imageChildren.size() - 3), 200, DURATION),
                    SLIDE_X.get(imageChildren.get(imageChildren.size() - 4), 400, DURATION)
            );
        }
        SCHEDULED_POOL.schedule(() -> Platform.runLater(() -> {
            Node removed = imageChildren.remove(imageChildren.size() - 1);
            imageChildren.add(Math.max(imageChildren.size() - 3, 0), removed);
            if (imageChildren.size() != 3){
                Node remove = imageChildren.remove(imageChildren.size() - 2);
                imageChildren.add(0, remove);
            }
            changeCurrentIndex((currentIndex + 1) % imagesURL.size());
            isPlaying = false;
            next(count - 1);
        }), TIME >> 1, TimeUnit.MILLISECONDS);
        transition.play();
    }
    private void last(){
        last(1);
    }
    private void last(int count){
        if (isPlaying || imagesURL == null || imagesURL.size() < 3 || count == 0){
            return;
        }
        isPlaying = true;
        ParallelTransition transition = new ParallelTransition();
        transition.getChildren().addAll(
                SLIDE_X.get(imageChildren.get(imageChildren.size() - 1), 400, DURATION),
                SCALE.get(imageChildren.get(imageChildren.size() - 1), 1, DURATION),
                SLIDE_X.get(imageChildren.get(imageChildren.size() - 3), 200, DURATION),
                SCALE.get(imageChildren.get(imageChildren.size() - 3), 1.25, DURATION)

        );
        if (imageChildren.size() == 3){
            transition.getChildren().add(SLIDE_X.get(imageChildren.get(imageChildren.size() - 2), 0, DURATION));
        }else {
            transition.getChildren().addAll(
                    SLIDE_X.get(imageChildren.get(0), 0, DURATION),
                    SLIDE_X.get(imageChildren.get(imageChildren.size() - 2), 200, DURATION)
            );
        }
        SCHEDULED_POOL.schedule(() -> Platform.runLater(() -> {
            imageChildren.add(imageChildren.remove(imageChildren.size() - 3));
            if (imageChildren.size() != 3){
                Node node = imageChildren.remove(0);
                imageChildren.add(imageChildren.size() - 2, node);
            }
            changeCurrentIndex((currentIndex - 1 + imagesURL.size()) % imagesURL.size());
            isPlaying = false;
            last(count - 1);
        }), TIME >> 1, TimeUnit.MILLISECONDS);
        transition.play();
    }

    private void changeCurrentIndex(int newIndex){
        dots.getChildren().get(currentIndex).getStyleClass().remove("currentDot");
        dots.getChildren().get(newIndex).getStyleClass().add("currentDot");
        currentIndex = newIndex;
    }
}