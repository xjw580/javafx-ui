package club.xiaojiawei.skin;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.controls.FilterComboBox;
import club.xiaojiawei.controls.FilterField;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:54
 */
public class FilterComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {

    private final FilterComboBox<T> filterComboBox;

    @Getter
    private final FilterField filterField = new FilterField();

    private final StackPane filterPane = new StackPane(filterField);

    private final ListView<T> rootListView = new ListView<>(){
        @Override
        protected double computePrefWidth(double width) {
            return filterComboBox.getWidth();
        }

        @Override
        protected double computePrefHeight(double height) {
            double h = popupHeight == -1? filterComboBox.getHeight() : popupHeight;
            return 5 + h * (Math.min(FilterComboBoxListViewSkin.this.filterComboBox.getItems().size() + 1, FilterComboBoxListViewSkin.this.filterComboBox.getVisibleRowCount()));
        }
    };

    private final ListView<T> realListView;

    private boolean isOuter = true;

    private double popupHeight = -1;

    @SuppressWarnings("all")
    public FilterComboBoxListViewSkin(FilterComboBox<T> control) {
        super(control);

        this.filterComboBox = control;
        this.filterField.setRealTime(true);

        if (control.getStyleClass().contains("combo-box-ui")){
            filterField.getStyleClass().add("text-field-ui");
            if (control.getStyleClass().contains("combo-box-ui-small")) {
                filterField.getStyleClass().addAll("text-field-ui-tiny");
            } else if (!control.getStyleClass().contains("combo-box-ui-big") && !control.getStyleClass().contains("combo-box-ui-tiny")){
                filterField.getStyleClass().addAll("text-field-ui-small");
            }
        }

        realListView = (ListView) super.getPopupContent();
        realListView.getItems().addListener((ListChangeListener<? super T>) change -> {
            rootListView.getItems().remove(1, rootListView.getItems().size());
            rootListView.getItems().addAll(realListView.getItems());
        });

        realListView.getSelectionModel().selectedItemProperty().addListener((observableValue, t, t1) -> {
            for (Object item : rootListView.getItems()) {
                if (Objects.equals(item, t1) && realListView.getSelectionModel().getSelectedIndex() != 0){
                    rootListView.getSelectionModel().select(t1);
                }
            }
        });

        rootListView.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<>(){

                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && getIndex() == 0 && item == null){
                            setText("");
                            setGraphic(filterPane);
                            filterPane.setMaxWidth(filterComboBox.getWidth() - 20);
                            setStyle("-fx-background-color: white!important;");
                        }else {
                            setGraphic(null);
                            setText(getShowText(getItem()));
                            setStyle("");
                        }
                    }

                };
            }
        });

        filterField.setOnFilterAction(text -> {
            isOuter = false;
            ObservableList<T> rootListViewItems = rootListView.getItems();
            if (text == null || text.isBlank()){
                if (rootListViewItems.size() != realListView.getItems().size() + 1){
                    T selectedItem = realListView.getSelectionModel().getSelectedItem();
                    rootListViewItems.remove(1, rootListViewItems.size());
                    rootListViewItems.addAll(realListView.getItems());
                    rootListView.getSelectionModel().select(selectedItem);
                }
            }else {
                T selectedItem = realListView.getSelectionModel().getSelectedItem();
                rootListViewItems.remove(1, rootListViewItems.size());
                List<T> resullt = realListView.getItems()
                        .stream()
                        .filter(item -> getShowText(item).contains(text) || (control.isIgnoreCase() && getShowText(item).toLowerCase().contains(text.toLowerCase())))
                        .toList();
                rootListViewItems.addAll(resullt);
                rootListView.getSelectionModel().select(selectedItem);
            }
            rootListView.refresh();
            isOuter = true;
        });

        rootListView.getSelectionModel().selectedItemProperty().addListener((observableValue, o, t1) -> {
            if (isOuter){
                if (rootListView.getSelectionModel().getSelectedIndex() != 0){
                    this.filterComboBox.hide();
                    realListView.getSelectionModel().select(t1);
                }
            }
        });
        filterPane.prefWidthProperty().bind(control.widthProperty().subtract(5));
//        防止搜索框所在item被选中
        filterPane.addEventHandler(MouseEvent.MOUSE_PRESSED, Event::consume);
        rootListView.getItems().add(null);
        rootListView.getItems().addAll(realListView.getItems());
        rootListView.getSelectionModel().select(filterComboBox.getValue());
    }

    @NotNull
    private String getShowText(T t){
        if (getConverter() == null) {
            return t == null? "" : Objects.requireNonNullElse(t.toString(), "");
        }else {
            return Objects.requireNonNullElse(getConverter().toString(t), "");
        }
    }

    @Override
    public void show() {
        super.show();
        double insect = 22D;
        filterField.setMaxWidth(filterComboBox.getWidth() - insect);
        filterField.setMinWidth(filterComboBox.getWidth() - insect);
        filterField.requestFocus();
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        popupHeight = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    public Node getPopupContent() {
//        只能返回ListView类型，否则popup布局会异常
        return rootListView;
    }

}
