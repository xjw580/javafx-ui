package club.xiaojiawei.controls;

import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.config.JavaFXUIThreadPoolConfig.SCHEDULED_POOL;
import static club.xiaojiawei.enums.BaseTransitionEnum.SCALE;
import static club.xiaojiawei.enums.BaseTransitionEnum.SLIDE_X;

/**
 * 轮播图
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/6 23:39
 */
@SuppressWarnings("unused")
public class Carousel extends AnchorPane {

    private final IntegerProperty currentIndex = new SimpleIntegerProperty(0);
    /**
     * 图片url
     * 支持格式：网络http url、本地路径
     * 例：https://zergqueen.gitee.io/images/javafx-ui/carousel7.jpg 或 /club/xiaojiawei/readme/tab/images/carousel2.jpg 或 C:\Users\Administrator\Downloads\carousel7.jpg
     */
    @Getter
    private ObservableList<String> imagesURL;
    private final BooleanProperty autoPlay = new SimpleBooleanProperty(true);
    private final DoubleProperty nudeScale = new SimpleDoubleProperty(0.375D);

    public int getCurrentIndex() {
        return currentIndex.get();
    }
    public IntegerProperty currentIndexProperty() {
        return currentIndex;
    }
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex.set(currentIndex);
    }

    /**
     * @return ObservableList<AnchorPane> AnchorPane的背景为图片
     */
    public ObservableList<Node> getImageChildren() {
        return images.getChildren();
    }
    /**
     * @param imagesURL 图片最少3张
     */
    public void setImagesURL(ObservableList<String> imagesURL) {
        if (imagesURL.size() < 3){
            throw new IllegalArgumentException("imagesURL.size()必须处于[3,+∞)区间");
        }
        imageChildren.clear();
        images.setPrefWidth(translateX * 2 + IMAGE_WIDTH);
        this.imagesURL = imagesURL;
        for (int i = imagesURL.size() - 2; i > 1; i--) {
            AnchorPane image = buildImage(imagesURL.get(i), i);
            image.setTranslateX(translateX);
            imageChildren.add(image);
        }
//        left image
        imageChildren.add(buildImage(imagesURL.get(imagesURL.size() - 1), imagesURL.size() - 1));
//        right image
        AnchorPane image = buildImage(imagesURL.get(1), 1);
        image.setTranslateX(translateX * 2);
        imageChildren.add(image);
//        middle image
        image = buildImage(imagesURL.get(0), 0);
        image.setTranslateX(translateX);
        image.setScaleX(SCALE_UP);
        image.setScaleY(SCALE_UP);
        imageChildren.add(image);
        for (int i = 0; i < imagesURL.size(); i++) {
            Circle dot;
            if (i == 0){
                dots.getChildren().add(dot = new Circle(){{setRadius(DOT_RADIUS);getStyleClass().addAll("dot", "currentDot");}});
            }else {
                dots.getChildren().add(dot = new Circle(){{setRadius(DOT_RADIUS);getStyleClass().add("dot");}});
            }
            dot.setStyle("-fx-cursor: hand");
            int finalI = i;
            dot.setOnMouseClicked(event -> currentIndex.set(finalI));
        }
    }
    private AnchorPane buildImage(String url, int index){
        AnchorPane image = new AnchorPane(){{
            getStyleClass().add("image");
            setPrefWidth(IMAGE_WIDTH);
            setPrefHeight(IMAGE_HEIGHT);
            File tempFile;
            if (url.startsWith("http")){
                setStyle("-fx-background-image: url(" + url + ");");
            }else if ((tempFile = new File(url)).exists()){
                setStyle("-fx-background-image: url(file:/" + tempFile.getPath().replace("\\", "/") + ");");
            }else {
                setStyle("-fx-background-image: url(" + Objects.requireNonNull(getClass().getResource(url)).toExternalForm() + ");");
            }
        }};
        image.setClip(new Rectangle(){{
            setArcHeight(IMAGE_ARC);
            setArcWidth(IMAGE_ARC);
            setWidth(IMAGE_WIDTH);
            setHeight(IMAGE_HEIGHT);
        }});
        image.setOnMouseClicked(event -> currentIndex.set(index));
        return image;
    }

    public boolean isAutoPlay() {
        return autoPlay.get();
    }
    public BooleanProperty autoPlayProperty() {
        return autoPlay;
    }
    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay.set(autoPlay);
    }

    public double getNudeScale() {
        return nudeScale.get();
    }
    public DoubleProperty nudeScaleProperty() {
        return nudeScale;
    }
    public void setNudeScale(double nudeScale) {
        this.nudeScale.set(nudeScale);
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
    private ScheduledFuture<?> autoPlaySchedule;
    private double translateX = (this.nudeScale.get() + (SCALE_UP - 1) / 2) * IMAGE_WIDTH;
    private boolean isHoverImage;
    private boolean isPlaying;
    private int skipCount;
    private static final double SCALE_UP = 1.25D;
    private static final double IMAGE_WIDTH = 400D;
    private static final double IMAGE_HEIGHT = 200D;
    private static final double IMAGE_ARC = 15D;
    private static final String CURRENT_DOT_STYLE_CLASS = "currentDot";
    private static final double DOT_RADIUS = 3D;

    public Carousel() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            imageChildren = images.getChildren();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void afterFXMLLoaded(){
        addListener();
    }

    private void addListener(){
        left.setOnMouseClicked(event -> currentIndex.set(formatIndex(currentIndex.get() - 1)));
        right.setOnMouseClicked(event -> currentIndex.set(formatIndex(currentIndex.get() + 1)));
        this.hoverProperty().addListener((observable, oldValue, newValue) -> isHoverImage = newValue);
        currentIndex.addListener((observable, oldValue, newValue) -> {
            int newInt = newValue.intValue(), oldInt = oldValue.intValue();
            dots.getChildren().get(oldInt).getStyleClass().remove(CURRENT_DOT_STYLE_CLASS);
            dots.getChildren().get(newInt).getStyleClass().add(CURRENT_DOT_STYLE_CLASS);
            if (newInt > oldInt){
                if (newInt - oldInt > oldInt + imagesURL.size() - newInt){
                    skipCount -= oldInt + imagesURL.size() - newInt;
                    lastImage(true);
                }else {
                    skipCount += newInt - oldInt;
                    nextImage(true);
                }
            }else {
                if (oldInt - newInt < imagesURL.size() - oldInt + newInt){
                    skipCount -= oldInt - newInt;
                    lastImage(true);
                }else {
                    skipCount += imagesURL.size() - oldInt + newInt;
                    nextImage(true);
                }
            }
        });
        this.autoPlay.addListener((observableValue, aBoolean, t1) -> {
            if (t1){
                if (autoPlaySchedule != null){
                    autoPlaySchedule.cancel(true);
                    autoPlaySchedule = null;
                }
                autoPlaySchedule = SCHEDULED_POOL.scheduleAtFixedRate(() -> {
                    if (!isHoverImage){
                        currentIndex.set(formatIndex(currentIndex.get() + 1));
                    }
                }, 5, 5, TimeUnit.SECONDS);
            }
        });
        this.nudeScale.addListener((observableValue, number, t1) -> {
            isHoverImage = true;
            double translateXScale = t1.doubleValue() + (SCALE_UP - 1) / 2 ;
            translateX = translateXScale * IMAGE_WIDTH;
            images.setPrefWidth(translateX * 2 + IMAGE_WIDTH);
            for (int i = 0; i < imageChildren.size() - 3; i++) {
                imageChildren.get(i).setTranslateX(translateX);
            }
            imageChildren.get(imageChildren.size() - 2).setTranslateX(translateX * 2);
            imageChildren.get(imageChildren.size() - 1).setTranslateX(translateX);
            isHoverImage = false;
        });
    }
    private void nextImage(boolean isNew){
        if ((isPlaying && isNew) || skipCount <= 0 || imagesURL == null || imagesURL.size() < 3){
            if (skipCount == 0){
                isPlaying = false;
            }
            return;
        }
        isPlaying = true;
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(
                SLIDE_X.get(imageChildren.get(imageChildren.size() - 1), 0, DURATION),
                SCALE.get(imageChildren.get(imageChildren.size() - 1), 1, DURATION),
                SLIDE_X.get(imageChildren.get(imageChildren.size() - 2), translateX, DURATION),
                SCALE.get(imageChildren.get(imageChildren.size() - 2), SCALE_UP, DURATION)
        );
        if (imageChildren.size() == 3){
            parallelTransition.getChildren().add(SLIDE_X.get(imageChildren.get(0), translateX * 2, DURATION));
        }else {
            parallelTransition.getChildren().addAll(
                    SLIDE_X.get(imageChildren.get(imageChildren.size() - 3), translateX, DURATION),
                    SLIDE_X.get(imageChildren.get(imageChildren.size() - 4), translateX * 2, DURATION)
            );
        }
        SCHEDULED_POOL.schedule(() -> Platform.runLater(() -> {
            Node removedNode = imageChildren.remove(imageChildren.size() - 1);
            imageChildren.add(Math.max(imageChildren.size() - 3, 0), removedNode);
            if (imageChildren.size() != 3){
                Node removedNode1 = imageChildren.remove(imageChildren.size() - 2);
                imageChildren.add(0, removedNode1);
            }
            skipCount--;
            nextImage(false);
        }), TIME >> 1, TimeUnit.MILLISECONDS);
        parallelTransition.play();
    }
    private void lastImage(boolean isNew){
        if ((isPlaying && isNew) || skipCount >= 0 || imagesURL == null || imagesURL.size() < 3){
            if (skipCount == 0){
                isPlaying = false;
            }
            return;
        }
        isPlaying = true;
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(
                SLIDE_X.get(imageChildren.get(imageChildren.size() - 1), translateX * 2, DURATION),
                SCALE.get(imageChildren.get(imageChildren.size() - 1), 1, DURATION),
                SLIDE_X.get(imageChildren.get(imageChildren.size() - 3), translateX, DURATION),
                SCALE.get(imageChildren.get(imageChildren.size() - 3), SCALE_UP, DURATION)

        );
        if (imageChildren.size() == 3){
            parallelTransition.getChildren().add(SLIDE_X.get(imageChildren.get(imageChildren.size() - 2), 0, DURATION));
        }else {
            parallelTransition.getChildren().addAll(
                    SLIDE_X.get(imageChildren.get(0), 0, DURATION),
                    SLIDE_X.get(imageChildren.get(imageChildren.size() - 2), translateX, DURATION)
            );
        }
        SCHEDULED_POOL.schedule(() -> Platform.runLater(() -> {
            imageChildren.add(imageChildren.remove(imageChildren.size() - 3));
            if (imageChildren.size() != 3){
                Node removedNode = imageChildren.remove(0);
                imageChildren.add(imageChildren.size() - 2, removedNode);
            }
            skipCount++;
            lastImage(false);
        }), TIME >> 1, TimeUnit.MILLISECONDS);
        parallelTransition.play();
    }

    private int formatIndex(int index){
        return (index + imagesURL.size()) % imagesURL.size();
    }
}