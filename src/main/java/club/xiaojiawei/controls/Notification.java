package club.xiaojiawei.controls;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.PrimitiveIterator;

/**
 * 日历
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 22:54
 */
@Slf4j
public class Notification extends Group {
    private final DoubleProperty contentMaxWidth;
    private final StringProperty title;
    private final StringProperty content;
    private final StringProperty type = new SimpleStringProperty();
    private final BooleanProperty show = new SimpleBooleanProperty(true);

    public double getContentMaxWidth() {
        return contentMaxWidth.get();
    }

    public DoubleProperty contentMaxWidthProperty() {
        return contentMaxWidth;
    }

    public void setContentMaxWidth(double contentMaxWidth) {
        this.contentMaxWidth.set(contentMaxWidth);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getContent() {
        return content.get();
    }

    public StringProperty contentProperty() {
        return content;
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    @FXML
    private Label titleLabel;
    @FXML
    private Label contentLabel;
    @FXML
    private HBox bottomHBox;
    @FXML
    private StackPane closeIcoPane;
    public Notification() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            contentMaxWidth = bottomHBox.maxWidthProperty();
            title = titleLabel.textProperty();
            content = contentLabel.textProperty();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void afterFXMLLoaded(){
        addListener();
    }
    private void addListener(){
        show.addListener((observableValue, aBoolean, t1) -> {
            ParallelTransition parallelTransition = new ParallelTransition();
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(300D), this);
            parallelTransition.getChildren().add(fadeTransition);
            if (t1){
                this.setVisible(true);
                this.setManaged(true);
                fadeTransition.setToValue(1D);
            }else {
                parallelTransition.setOnFinished(actionEvent -> {
                    this.setVisible(false);
                    this.setManaged(false);
                });
                fadeTransition.setToValue(0D);
            }
            parallelTransition.play();
        });
        closeIcoPane.setOnMouseClicked(mouseEvent -> {
            show.set(false);
        });
    }

    public void show(){
        show.set(true);
    }
}
