package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.InvisibleIco;
import club.xiaojiawei.controls.ico.VisibleIco;
import club.xiaojiawei.proxy.ProxyControls;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 14:25
 */
public class PasswordShowField extends AnchorPane implements ProxyControls<TextField> {
    /**
     * 密码框文本
     */
    private final StringProperty text = new SimpleStringProperty("");
    private final ObjectProperty<TextFormatter<?>> textFormatter = new SimpleObjectProperty<>();
    public String getText() {
        return text.get();
    }
    public StringProperty textProperty() {
        return text;
    }
    public void setText(String text) {
        this.textField.setText(text);
    }
    public Tooltip getTooltip() {
        return textField.getTooltip();
    }
    public void setTooltip(Tooltip tooltip) {
        textField.setTooltip(tooltip);
    }
    public String getPromptText() {
        return textField.getPromptText();
    }
    public StringProperty promptTextProperty() {
        return textField.promptTextProperty();
    }
    public void setPromptText(String promptText) {
        textField.setPromptText(promptText);
    }

    public TextFormatter<?> getTextFormatter() {
        return textFormatter.get();
    }

    public ObjectProperty<TextFormatter<?>> textFormatterProperty() {
        return textFormatter;
    }

    public void setTextFormatter(TextFormatter<?> textFormatter) {
        this.textFormatter.set(textFormatter);
    }


    @FXML private StackPane icoPane;
    @FXML private InvisibleIco inVisibleIco;
    @FXML private VisibleIco visibleIco;
    @FXML private TextField textField;

    @Override
    public TextField getRealControls() {
        return textField;
    }

    public PasswordShowField() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void afterFXMLLoaded(){
        icoPane.setOnMouseClicked(event -> {
            if (inVisibleIco.isVisible()){
                inVisibleIco.setVisible(false);
                visibleIco.setVisible(true);
                textField.setText(text.get());
            }else {
                visibleIco.setVisible(false);
                inVisibleIco.setVisible(true);
                textField.setText(text.get());
            }
        });
        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (textFormatter.get() != null){
                change = textFormatter.get().getFilter().apply(change);
            }
            if (change != null){
                text.set(text.get().substring(0, change.getRangeStart()) + change.getText() + text.get().substring(change.getRangeEnd()));
                if (inVisibleIco.isVisible()){
                    change.setText(change.getText().replaceAll(".", "●"));
                }
            }
            return change;
        }));
        icoPane.translateXProperty().bind(textField.widthProperty().subtract(22));
        icoPane.translateYProperty().bind(textField.heightProperty().subtract(20).divide(2));
    }

}