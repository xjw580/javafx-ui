package club.xiaojiawei.controls;

import club.xiaojiawei.enums.BaseTransitionEnum;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.config.JavaFXUIThreadPoolConfig.SCHEDULED_POOL;

/**
 * 轮播图
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/6 23:39
 */
@SuppressWarnings("unused")
public class Carousel extends AnchorPane {

    private final IntegerProperty currentIndex = new SimpleIntegerProperty();
    /**
     * 图片url
     * 支持格式：网络http url、本地路径
     * 例：https://zergqueen.gitee.io/images/javafx-ui/carousel7.jpg 或 /club/xiaojiawei/readme/tab/images/carousel2.jpg 或 C:\Users\Administrator\Downloads\carousel7.jpg
     */
    @Getter
    private ObservableList<String> imagesURL;
    /**
     * 是否自动播放
     */
    private final BooleanProperty autoPlay = new SimpleBooleanProperty(false);
    /**
     * 两侧图片裸露程度
     */
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
        return imagesPane.getChildren();
    }
    /**
     * @param imagesURL 图片最少3张
     */
    public void setImagesURL(ObservableList<String> imagesURL) {
        if (imagesURL.size() < 3){
            throw new IllegalArgumentException("imagesURL.size()必须处于[3,+∞)区间");
        }

        imageChildren.clear();
        refreshImagesPaneWidth();
        this.imagesURL = imagesURL;
        for (int i = imagesURL.size() - 2; i > 1; i--) {
            AnchorPane image = buildImage(imagesURL.get(i), i);
            image.setTranslateX(calcMiddleImageTranslateX());
            image.setScaleX(DOWN_SCALE);
            image.setScaleY(DOWN_SCALE);
            imageChildren.add(image);
        }
//        left image
        AnchorPane leftImage = buildImage(imagesURL.get(imagesURL.size() - 1), imagesURL.size() - 1);
        leftImage.setTranslateX(calcLeftImageTranslateX());
        leftImage.setScaleX(DOWN_SCALE);
        leftImage.setScaleY(DOWN_SCALE);
        imageChildren.add(leftImage);
//        right image
        AnchorPane rightImage = buildImage(imagesURL.get(1), 1);
        rightImage.setTranslateX(calcRightImageTranslateX());
        rightImage.setScaleX(DOWN_SCALE);
        rightImage.setScaleY(DOWN_SCALE);
        imageChildren.add(rightImage);
//        middle image
        AnchorPane middleImage = buildImage(imagesURL.get(0), 0);
        middleImage.setTranslateX(calcMiddleImageTranslateX());
        imageChildren.add(middleImage);

        double dotRadius = 3D;
        for (int i = 0; i < imagesURL.size(); i++) {
            Circle dot;
            if (i == 0){
                dots.getChildren().add(dot = new Circle(){{setRadius(dotRadius);getStyleClass().addAll("dot", "currentDot");}});
            }else {
                dots.getChildren().add(dot = new Circle(){{setRadius(dotRadius);getStyleClass().add("dot");}});
            }
            int finalI = i;
            dot.setOnMouseClicked(event -> currentIndex.set(finalI));
        }
    }

    @SuppressWarnings("all")
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
        double imageArc = 15D;
        image.setClip(new Rectangle(){{
            setArcHeight(imageArc);
            setArcWidth(imageArc);
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
    private AnchorPane rootPane;
    @FXML
    private HBox left;
    @FXML
    private HBox right;
    @FXML
    private AnchorPane imagesPane;
    @FXML
    private HBox dots;
    private final ObservableList<Node> imageChildren;
    private static final long DURATION_TIME = 400L;
    private static final Duration DURATION = Duration.millis(DURATION_TIME);
    private ScheduledFuture<?> autoPlaySchedule;
    private double imageNudeWidth = calcImageNudeWidth(this.nudeScale.get());
    private boolean isHoverImage;
    private boolean isPlaying;
    private int skipCount;
    private static final double DOWN_SCALE = 0.8D;
    private static final double IMAGE_WIDTH = 500D;
    private static final double IMAGE_HEIGHT = 250D;
    private static final double SCALE_GAP = (1 - DOWN_SCALE) * IMAGE_WIDTH / 2;
    private static final String CURRENT_DOT_STYLE_CLASS = "currentDot";

    public Carousel() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            imageChildren = imagesPane.getChildren();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void afterFXMLLoaded(){
        addListener();
        initIco();
        addBind();
    }

    private void initIco(){
        double top = IMAGE_HEIGHT / 2 - 15;
        AnchorPane.setTopAnchor(left, top);
        AnchorPane.setTopAnchor(right, top);
    }

    private void addBind(){
        dots.prefWidthProperty().bind(rootPane.prefWidthProperty());
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
            imageNudeWidth = calcImageNudeWidth(t1.doubleValue());
            refreshImagesPaneWidth();
            for (int i = 0; i < imageChildren.size() - 3; i++) {
                imageChildren.get(i).setTranslateX(calcMiddleImageTranslateX());
            }
//            left
            imageChildren.get(imageChildren.size() - 3).setTranslateX(calcLeftImageTranslateX());
//            right
            imageChildren.get(imageChildren.size() - 2).setTranslateX(calcRightImageTranslateX());
//            middle
            imageChildren.get(imageChildren.size() - 1).setTranslateX(calcMiddleImageTranslateX());
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
                BaseTransitionEnum.SLIDE_X.get(imageChildren.get(imageChildren.size() - 1), calcLeftImageTranslateX(), DURATION),
                BaseTransitionEnum.SCALE.get(imageChildren.get(imageChildren.size() - 1), DOWN_SCALE, DURATION),
                BaseTransitionEnum.SLIDE_X.get(imageChildren.get(imageChildren.size() - 2), calcMiddleImageTranslateX(), DURATION),
                BaseTransitionEnum.SCALE.get(imageChildren.get(imageChildren.size() - 2), 1, DURATION)
        );
        if (imageChildren.size() == 3){
            parallelTransition.getChildren().add(BaseTransitionEnum.SLIDE_X.get(imageChildren.get(0), calcMiddleImageTranslateX() * 2, DURATION));
        }else {
            parallelTransition.getChildren().addAll(
                    BaseTransitionEnum.SLIDE_X.get(imageChildren.get(imageChildren.size() - 3), calcMiddleImageTranslateX(), DURATION),
                    BaseTransitionEnum.SLIDE_X.get(imageChildren.get(imageChildren.size() - 4), calcRightImageTranslateX(), DURATION)
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
        }), DURATION_TIME >> 1, TimeUnit.MILLISECONDS);
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
                BaseTransitionEnum.SLIDE_X.get(imageChildren.get(imageChildren.size() - 1), calcRightImageTranslateX(), DURATION),
                BaseTransitionEnum.SCALE.get(imageChildren.get(imageChildren.size() - 1), DOWN_SCALE, DURATION),
                BaseTransitionEnum.SLIDE_X.get(imageChildren.get(imageChildren.size() - 3), calcMiddleImageTranslateX(), DURATION),
                BaseTransitionEnum.SCALE.get(imageChildren.get(imageChildren.size() - 3), 1, DURATION)

        );
        if (imageChildren.size() == 3){
            parallelTransition.getChildren().add(BaseTransitionEnum.SLIDE_X.get(imageChildren.get(imageChildren.size() - 2), calcLeftImageTranslateX(), DURATION));
        }else {
            parallelTransition.getChildren().addAll(
                    BaseTransitionEnum.SLIDE_X.get(imageChildren.get(0), calcLeftImageTranslateX(), DURATION),
                    BaseTransitionEnum.SLIDE_X.get(imageChildren.get(imageChildren.size() - 2), calcMiddleImageTranslateX(), DURATION)
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
        }), DURATION_TIME >> 1, TimeUnit.MILLISECONDS);
        parallelTransition.play();
    }

    private int formatIndex(int index){
        return (index + imagesURL.size()) % imagesURL.size();
    }

    private double calcImageNudeWidth(double nudeScale){
        return IMAGE_WIDTH * DOWN_SCALE * nudeScale;
    }

    private void refreshImagesPaneWidth(){
        rootPane.setPrefWidth((imageNudeWidth + SCALE_GAP) * 2 + IMAGE_WIDTH * DOWN_SCALE);
    }

    private double calcLeftImageTranslateX(){
        return -SCALE_GAP;
    }

    private double calcMiddleImageTranslateX(){
        return imageNudeWidth;
    }

    private double calcRightImageTranslateX(){
        return imageNudeWidth * 2 + SCALE_GAP;
    }

}