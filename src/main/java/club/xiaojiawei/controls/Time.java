package club.xiaojiawei.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威
 * @date 2023/7/3 12:21
 * @msg 时间组件
 */
@SuppressWarnings("unused")
public class Time extends AnchorPane{

    private final StringProperty time = new SimpleStringProperty("00:00");
    private boolean showSelector = true;

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setShowSelector(boolean showSelector) {
        this.showSelector = showSelector;
        clock.setVisible(showSelector);
    }

    public boolean isShowSelector() {
        return showSelector;
    }

    public void setTime(String timeProperty) throws ParseException {
        if (timeProperty == null ||timeProperty.matches("^\\d{2}:\\d{2}")){
            throw new ParseException(timeProperty, -1);
        }
        String[] time = timeProperty.split(":");
        if (time[0].compareTo("24") > 0 ||time[1].compareTo("59") > 0){
            throw new ParseException(timeProperty, -1);
        }
        hour.setText(time[0]);
        min.setText(time[1]);
        this.time.set(timeProperty);
    }

    @FXML
    private TextField hour;
    @FXML
    private TextField min;
    @FXML
    private AnchorPane clock;
    @FXML
    private AnchorPane selector;
    @FXML
    private VBox hourSelector;
    @FXML
    private VBox minSelector;
    @FXML
    private ScrollPane hourSelectorOuter;
    @FXML
    private ScrollPane minSelectorOuter;
    public Time() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("time.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            AnchorPane ap = fxmlLoader.load();
            hour.setTextFormatter(new TextFormatter<>(this::onInputHour));
            min.setTextFormatter(new TextFormatter<>(this::onInputMin));
            hour.focusedProperty().addListener(onBlur(hour));
            min.focusedProperty().addListener(onBlur(min));
            hour.textProperty().addListener((observableValue, s, t1) -> {
                timeProperty().setValue(timeProperty().getValue().replaceAll("\\d{0,2}:", t1 + ":"));
            });
            min.textProperty().addListener((observableValue, s, t1) -> {
                timeProperty().setValue(timeProperty().getValue().replaceAll(":\\d{0,2}", ":" + t1));
            });
//            为时间选择器填充时和分元素
            ObservableList<Node> hourChildren = hourSelector.getChildren();
            for (int i = 0; i <= 24; i++) {
                Label label = new Label();
                if (i < 10){
                    label.setText("0" + i);
                }else {
                    label.setText(String.valueOf(i));
                }
                label.setCache(false);
                label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    for (Node child : hourChildren) {
                        child.getStyleClass().remove("selectLabel");
                    }
                    label.getStyleClass().add("selectLabel");
                    hour.setText(label.getText());
                });
                hourChildren.add(label);
            }
            ObservableList<Node> minChildren = minSelector.getChildren();
            for (int i = 0; i <= 59; i++) {
                Label label = new Label();
                if (i < 10){
                    label.setText("0" + i);
                }else {
                    label.setText(String.valueOf(i));
                }
                label.setCache(false);
                label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    for (Node child : minChildren) {
                        child.getStyleClass().remove("selectLabel");
                    }
                    label.getStyleClass().add("selectLabel");
                    min.setText(label.getText());
                });
                minChildren.add(label);
            }
//            点击打开/隐藏时间选择器
            clock.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (selector.isVisible()){
                    selector.setVisible(false);
                }else {
                    hourSelectorOuter.setVvalue(hour.getText().isBlank()? 0 : (double) Integer.parseInt(hour.getText()) / 24);
                    minSelectorOuter.setVvalue(min.getText().isBlank()? 0 : (double) Integer.parseInt(min.getText()) / 59);
                    for (Node hourChild : hourChildren) {
                        Label label = (Label) hourChild;
                        if (Objects.equals(label.getText(), hour.getText())){
                            label.getStyleClass().add("selectLabel");
                        }
                    }
                    for (Node hourChild : minChildren) {
                        Label label = (Label) hourChild;
                        if (Objects.equals(label.getText(), min.getText())){
                            label.getStyleClass().add("selectLabel");
                        }
                    }
                    selector.setVisible(true);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 失焦后将时间规范成两位数字
     * @param textField
     * @return
     */
    private ChangeListener<Boolean> onBlur(TextField textField) {
        return (observableValue, aBoolean, t1) -> {
            if (!t1 && textField.getText().length() == 1){
                textField.setText("0" + textField.getText());
            }
        };
    }
    private TextFormatter.Change onInputHour(TextFormatter.Change change) {
        return onInputHour(change, hour, "24");
    }
    private TextFormatter.Change onInputMin(TextFormatter.Change change) {
        return onInputHour(change, min, "59");
    }

    /**
     * 拦截输入
     * @param change
     * @param textField
     * @return
     */
    private TextFormatter.Change onInputHour(TextFormatter.Change change, TextField textField, String max) {
        if (change.getText().matches("^\\d{0,2}") && (textField.getText().substring(0, change.getRangeStart()) + change.getText()).compareTo(max) <= 0 && (change.getText().length() + textField.getText().length() - (change.getRangeEnd() - change.getRangeStart()) <= 2)){
            return change;
        }
        return null;
    }

}