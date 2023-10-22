package club.xiaojiawei.controls;

import club.xiaojiawei.utils.TransitionUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.girod.javafx.svgimage.SVGLoader;

import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

/**
 * @author 肖嘉威
 * @date 2023/7/3 12:21
 * @msg 时间组件
 */
@SuppressWarnings("unused")
public class Time extends FlowPane {
    /**
     * 默认时间00:00
     */
    private final StringProperty time = new SimpleStringProperty("00:00");
    /**
     * 默认显示时间选择器图标
     */
    private boolean showSelector = true;
    /**
     * 展示行数
     */
    private int showRowCount = 6;

    private final Popup timeSelectorPopup = new Popup();
    private static final String SELECTED_TIME_LABEL_XJW = "selectedTimeLabelXJW";

    public int getShowRowCount() {
        return showRowCount;
    }

    public void setShowRowCount(int showRowCount) {
        this.showRowCount = showRowCount;
        hourSelector.setMaxHeight(showRowCount * 30);
        minSelector.setMaxHeight(showRowCount * 30);
    }

    public String getTime() {
        return time.get();
    }

    public StringProperty timeProperty() {
        return time;
    }

    public void setShowSelector(boolean showSelector) {
        this.showSelector = showSelector;
        clockIco.setVisible(showSelector);
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
    private AnchorPane clockIco;

    public Time() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("time.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            initTimeTextField(hour, 24, (observableValue, s, t1) -> timeProperty().setValue(timeProperty().getValue().replaceAll("\\d{0,2}:", t1 + ":")));
            initTimeTextField(min, 59, (observableValue, s, t1) -> timeProperty().setValue(timeProperty().getValue().replaceAll(":\\d{0,2}", ":" + t1)));
            initClockIco();
            initTimeSelectorPopup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void initClockIco(){
        clockIco.getChildren().add(SVGLoader.load(getClass().getResource("/club/xiaojiawei/controls/images/clock.svg")));
        clockIco.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            sync(hour, hourSelector, MAX_HOUR);
            sync(min, minSelector, MAX_MIN);
            Bounds bounds = this.localToScreen(this.getBoundsInLocal());
            timeSelectorPopup.setAnchorX(bounds.getMaxX() - bounds.getWidth() + 40);
            timeSelectorPopup.setAnchorY(bounds.getMaxY() - 10);
            for (String stylesheet : this.getScene().getStylesheets()) {
                if (stylesheet.contains("time.css")){
                    timeSelectorPopup.show(this.getScene().getWindow());
                    TransitionUtil.playScaleYTransition(timeSelectorHBox, 0.5D, 1D, Duration.millis(200));
                    return;
                }
            }
            this.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/time.css")).toString());
            timeSelectorPopup.show(this.getScene().getWindow());
            TransitionUtil.playScaleYTransition(timeSelectorHBox, 0.5D, 1D, Duration.millis(200));
        });
    }
    private void sync(TextField timeTextField, ScrollPane timeScrollPane, int maxValue){
        if (!timeTextField.getText().isBlank()){
            if (timeTextField.isFocused()){
                standardizationText(timeTextField, timeTextField.getText());
            }
            double hourPos = (maxValue - Integer.parseInt(timeTextField.getText())) * (1D / (maxValue + 1 - showRowCount));
            timeScrollPane.setVvalue(hourPos);
        }
        setStyleForTimeLabel((VBox) (timeScrollPane.getContent()), timeTextField);
    }

    private void setStyleForTimeLabel(VBox vBox, TextField textField){
        for (Node child : vBox.getChildren()) {
            Label label = (Label) child;
            if (Objects.equals(label.getText(), textField.getText())){
                if (!child.getStyleClass().contains(SELECTED_TIME_LABEL_XJW)){
                    child.getStyleClass().add(SELECTED_TIME_LABEL_XJW);
                }
            }else {
                child.getStyleClass().remove(SELECTED_TIME_LABEL_XJW);
            }
        }
    }
    private void initTimeTextField(TextField textField, int maxText,  ChangeListener<String> textChangeListener){
        textField.setTextFormatter(interceptInput(textField, maxText));
        textField.focusedProperty().addListener(standardizationTextListener(textField));
        textField.setOnKeyPressed(directionKeyChangeTextHandler(textField));
        textField.textProperty().addListener(textChangeListener);
    }

    private ScrollPane hourSelector;
    private ScrollPane minSelector;
    private static final int MAX_HOUR = 23;
    private static final int MAX_MIN = 59;
    private HBox timeSelectorHBox;
    /**
     * 初始化时间选择器弹窗
     */
    private void initTimeSelectorPopup(){
        hourSelector = initTimeSelector(hour, MAX_HOUR);
        minSelector = initTimeSelector(min, MAX_MIN);
        timeSelectorHBox = new HBox();
        ObservableList<Node> children = timeSelectorHBox.getChildren();
        children.add(hourSelector);
        children.add(minSelector);
        timeSelectorHBox.setSpacing(2);
        timeSelectorHBox.setPadding(new Insets(1));
        timeSelectorHBox.getStyleClass().add("timeSelectorOuterXJW");
        timeSelectorPopup.getContent().add(timeSelectorHBox);
        timeSelectorPopup.setAutoHide(true);
    }

    private ScrollPane initTimeSelector(TextField textField, int maxTime){
        VBox selector = new VBox();
        ObservableList<Node> children = selector.getChildren();
        for (int i = maxTime; i >= 0 ; i--) {
            Label label = new Label();
            label.getStyleClass().add("timeLabelXJW");
            if (i < 10){
                label.setText("0" + i);
            }else {
                label.setText(String.valueOf(i));
            }
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                for (Node child : children) {
                    child.getStyleClass().remove(SELECTED_TIME_LABEL_XJW);
                }
                if (!label.getStyleClass().contains(SELECTED_TIME_LABEL_XJW)){
                    label.getStyleClass().add(SELECTED_TIME_LABEL_XJW);
                }
                textField.setText(label.getText());
                if (textField == min){
                    timeSelectorPopup.hide();
                }
            });
            children.add(label);
        }
        ScrollPane scrollPane = new ScrollPane(selector);
        scrollPane.getStyleClass().add("timeSelectorXJW");
        scrollPane.setMaxHeight(30 * showRowCount);
        return scrollPane;
    }

    /**
     * 标准化时间字符串
     * @param textField
     * @return
     */
    private ChangeListener<Boolean> standardizationTextListener(TextField textField) {
        return (observableValue, aBoolean, isFocus) -> {
            if (!isFocus){
                standardizationText(textField, textField.getText());
            }else if (textField == min){
                minSelector.requestFocus();
            }else{
                hourSelector.requestFocus();
            }
        };
    }

    private void standardizationText(TextField textField, String time){
        if (time.length() == 1){
            textField.setText("0" + time);
        }else if (!Objects.equals(textField.getText(), time)){
            textField.setText(time);
        }
    }

    /**
     * 拦截输入
     * @param textField
     * @param max
     * @return
     */
    private TextFormatter<TextFormatter.Change> interceptInput(TextField textField, int max) {
        return new TextFormatter<>(change -> {
            String temp;
            if (change.getText().matches("^\\d{0,2}")
                    && (Integer.parseInt((temp = textField.getText().substring(0, change.getRangeStart()) + change.getText()).isBlank()? "0" : temp) <= max)
                    && (change.getText().length() + textField.getText().length() - (change.getRangeEnd() - change.getRangeStart()) <= 2)
            ){
                return change;
            }
            return null;
        });
    }

    /**
     * 通过方向键改变时间
     * @param textField
     * @return
     */
    private EventHandler<? super KeyEvent> directionKeyChangeTextHandler(TextField textField){
        return (EventHandler<KeyEvent>) event -> {
            if (!timeSelectorPopup.isShowing()){
                switch (event.getCode()){
                    case UP -> {
                        if (textField == hour){
                            standardizationText(textField, (Integer.parseInt(textField.getText()) + 1) % (MAX_HOUR + 1) + "");
                        }else {
                            standardizationText(textField, (Integer.parseInt(textField.getText()) + 1) % (MAX_MIN + 1) + "");
                        }
                    }
                    case DOWN -> {
                        if (textField == hour){
                            standardizationText(textField, (Integer.parseInt(textField.getText()) - 1 + (MAX_HOUR + 1)) % (MAX_HOUR + 1) + "");
                        }else {
                            standardizationText(textField, (Integer.parseInt(textField.getText()) - 1 + (MAX_MIN + 1)) % (MAX_MIN + 1) + "");
                        }
                    }
                }
            }
        };
    }

}