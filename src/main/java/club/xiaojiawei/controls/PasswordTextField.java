package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.InvisibleIco;
import club.xiaojiawei.controls.ico.VisibleIco;
import club.xiaojiawei.proxy.ProxyAnchorPaneRegion;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.io.IOException;

/**
 * 密码输入框
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/31 14:25
 */
public class PasswordTextField extends ProxyAnchorPaneRegion<TextField> {

    /**
     * 密码框文本
     */
    private final StringProperty text = new SimpleStringProperty("");
    @Getter
    private boolean hideForever;
    private final ObjectProperty<TextFormatter<?>> textFormatter = new SimpleObjectProperty<>();
    public String getText() {
        return text.get();
    }
    public StringProperty textProperty() {
        return text;
    }
    public void setText(String text) {
        this.pswTextField.setText(text);
    }

    public void setHideForever(boolean hideForever) {
        icoPane.setVisible(!hideForever);
        icoPane.setManaged(!hideForever);
        Insets padding = pswTextField.getPadding();
        if (hideForever){
            pswTextField.setPadding(new Insets(padding.getTop(), padding.getLeft(), padding.getBottom(), padding.getLeft()));
        }else {
            pswTextField.setPadding(new Insets(padding.getTop(), 27D, padding.getBottom(), padding.getLeft()));
        }
        this.hideForever = hideForever;
    }

    public Tooltip getTooltip() {
        return pswTextField.getTooltip();
    }
    public void setTooltip(Tooltip tooltip) {
        pswTextField.setTooltip(tooltip);
    }
    public String getPromptText() {
        return pswTextField.getPromptText();
    }
    public StringProperty promptTextProperty() {
        return pswTextField.promptTextProperty();
    }
    public void setPromptText(String promptText) {
        pswTextField.setPromptText(promptText);
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
    @FXML private TextField pswTextField;

    @Override
    public TextField getRegion() {
        return pswTextField;
    }

    public PasswordTextField() {
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
                pswTextField.setText(text.get());
            }else {
                visibleIco.setVisible(false);
                inVisibleIco.setVisible(true);
                pswTextField.setText(text.get());
            }
        });
        pswTextField.setTextFormatter(new TextFormatter<>(change -> {
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
        icoPane.translateXProperty().bind(pswTextField.widthProperty().subtract(22));
        icoPane.translateYProperty().bind(pswTextField.heightProperty().subtract(20).divide(2));
    }

}