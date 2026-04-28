package club.xiaojiawei.controls;

import club.xiaojiawei.func.DateTimeInterceptor;
import club.xiaojiawei.utils.ScrollUtil;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.function.Predicate;

/**
 * 数值选择器
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/05/23 10:00
 */
@SuppressWarnings("unused")
public class NumberSelector extends FlowPane implements DateTimeInterceptor<Integer> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 当前值
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);
    /**
     * 最小值
     */
    private final IntegerProperty min = new SimpleIntegerProperty(0);
    /**
     * 最大值
     */
    private final IntegerProperty max = new SimpleIntegerProperty(100);
    /**
     * 展示行数
     */
    private double showRowCount = 6;
    /**
     * 拦截器
     */
    private final ObjectProperty<Predicate<Integer>> interceptor = new SimpleObjectProperty<>();

    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        if (value >= getMin() && value <= getMax() && test(value)) {
            this.value.set(value);
        }
    }

    public int getMin() {
        return min.get();
    }

    public IntegerProperty minProperty() {
        return min;
    }

    public void setMin(int min) {
        this.min.set(min);
        buildAndSync();
    }

    public int getMax() {
        return max.get();
    }

    public IntegerProperty maxProperty() {
        return max;
    }

    public void setMax(int max) {
        this.max.set(max);
        buildAndSync();
    }

    public double getShowRowCount() {
        return showRowCount;
    }

    public void setShowRowCount(double showRowCount) {
        this.showRowCount = showRowCount;
        double height = showRowCount * ROW_HEIGHT;
        numberSelector.setMaxHeight(height);
        this.setMaxHeight(height);
    }

    @Override
    public Predicate<Integer> getInterceptor() {
        return interceptor.get();
    }

    @Override
    public ObjectProperty<Predicate<Integer>> interceptorProperty() {
        return interceptor;
    }

    @Override
    public void setInterceptor(Predicate<Integer> dateInterceptor) {
        this.interceptor.set(dateInterceptor);
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public NumberSelector() {
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

    @FXML private ScrollPane numberSelector;
    @FXML private VBox numberVbox;

    private static final String SELECTED_NUMBER_LABEL_STYLE_CLASS = "selectedNumberLabel";
    private static final String NUMBER_LABEL_STYLE_CLASS = "numberLabel";
    private static final double ROW_HEIGHT = 30D;

    private final ToggleGroup numberGroup = new ToggleGroup();

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * 滚动到指定数值的位置，但不改变当前值
     * @param val
     */
    public void scrollTo(int val) {
        int totalSize = getMax() - getMin() + 1;
        int index = val - getMin();
        if (index < 0 || index >= numberVbox.getChildren().size()) {
            return;
        }
        ScrollUtil.buildSlideTimeLine(index, totalSize, showRowCount, numberSelector).play();
    }

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded(){
        addValuePropertyChangedListener();
        buildAndSync();
        setShowRowCount(showRowCount);
        addNumberSelectorKeyPressedListener();
    }

    private void buildAndSync() {
        buildNumberSelector(numberVbox.getChildren(), getMin(), getMax(), numberGroup);
        syncNumberSelector(numberVbox.getChildren(), getValue(), getMin(), getMax(), showRowCount, numberSelector);
    }

    /**
     * 添加数值监听器
     */
    private void addValuePropertyChangedListener(){
        value.addListener((observable, oldValue, newValue) -> {
            if (newValue == null){
                numberGroup.selectToggle(null);
            } else {
                for (Toggle toggle : numberGroup.getToggles()) {
                    if (toggle instanceof ToggleButton toggleButton && Integer.parseInt(toggleButton.getText()) == newValue.intValue()){
                        numberGroup.selectToggle(toggle);
                        break;
                    }
                }
                syncNumberSelector(numberVbox.getChildren(), newValue.intValue(), getMin(), getMax(), showRowCount, numberSelector);
            }
        });
    }

    /**
     * 同步数值选择器
     */
    private void syncNumberSelector(ObservableList<Node> children, int pointValue, int min, int max, double showRowCount, ScrollPane scrollPane){
        int totalSize = max - min + 1;
        int index = pointValue - min;
        if (index < 0 || index >= children.size()) return;

        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            child.getStyleClass().remove(SELECTED_NUMBER_LABEL_STYLE_CLASS);
            if (i == index){
                child.getStyleClass().add(SELECTED_NUMBER_LABEL_STYLE_CLASS);
                ScrollUtil.buildSlideTimeLine(index, totalSize, showRowCount, scrollPane).play();
            }
        }
    }

    /**
     * 构建数值选择器
     */
    private void buildNumberSelector(ObservableList<Node> children, int min, int max, ToggleGroup toggleGroup){
        children.clear();
        for (int i = min; i <= max ; i++) {
            ToggleButton label = new ToggleButton();
            label.setToggleGroup(toggleGroup);
            label.getStyleClass().add(NUMBER_LABEL_STYLE_CLASS);
            label.setText(String.valueOf(i));
            children.add(label);
        }
        toggleGroup.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            if (oldToggle != null){
                ((ToggleButton)oldToggle).getStyleClass().remove(SELECTED_NUMBER_LABEL_STYLE_CLASS);
            }
            if (newToggle != null){
                ((ToggleButton)newToggle).getStyleClass().add(SELECTED_NUMBER_LABEL_STYLE_CLASS);
                setValue(Integer.parseInt(((ToggleButton)newToggle).getText()));
            }
        });
    }

    /**
     * 为选择器添加按键监听器
     */
    private void addNumberSelectorKeyPressedListener(){
        numberSelector.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> setValue(getValue() + 1);
                case DOWN -> setValue(getValue() - 1);
            }
        });
    }
}
