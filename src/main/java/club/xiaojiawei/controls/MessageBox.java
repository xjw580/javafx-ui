package club.xiaojiawei.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Polygon;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 日历
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 22:54
 */
@Slf4j
public class MessageBox extends Group {

    private final StringProperty text;

    private final StringProperty color;

    private final Insets padding;

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public String getColor() {
        return color.get();
    }

    public StringProperty colorProperty() {
        return color;
    }

    public void setColor(String color) {
        this.color.set(color);
    }

    {
        color = new SimpleStringProperty();
        padding = new Insets(8, 8, 8, 8);
    }

    public MessageBox() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            text = msg.textProperty();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private Polygon triangle;
    @FXML
    private Label msg;

    private void afterFXMLLoaded(){
        color.addListener((observableValue, s, t1) -> {
            msg.setStyle(String.format("-fx-background-color: %s", t1));
            triangle.setStyle(String.format("-fx-fill: %s", t1));
        });
    }

}
