package club.xiaojiawei.skin;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.controls.FilterComboBox;
import club.xiaojiawei.controls.FilterField;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:54
 */
public class FilterComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {

    private final FilterComboBox<T> filterComboBox;

    private final FilterField filterField;

    private final StackPane filterPane;

    private final VBox vBox;

    private final ListView<T> rawListView;

    private final ListView<T> showListView;

    private final ObservableList<T> showItems;

    @SuppressWarnings("all")
    public FilterComboBoxListViewSkin(FilterComboBox<T> control) {
        super(control);

        this.filterField = new FilterField();
        this.filterField.setRealTime(true);
        this.filterPane = new StackPane(filterField);
        filterPane.setStyle("-fx-padding: 5");

        rawListView = (ListView) super.getPopupContent();
//        rawListView.setManaged(true);

        showListView = new ListView<>(){
            @Override
            protected double computePrefWidth(double height) {
                return rawListView.prefWidth(height);
            }

            @Override
            protected double computePrefHeight(double width) {
                return rawListView.prefHeight(width) * Math.min(showItems.size(), filterComboBox.getVisibleRowCount()) / Math.min(rawListView.getItems().size(), filterComboBox.getVisibleRowCount()) + 4;
            }
        };
        showItems = showListView.getItems();
        showItems.setAll(rawListView.getItems());
        showListView.getSelectionModel().select(rawListView.getSelectionModel().getSelectedItem());
        showListView.cellFactoryProperty().bind(rawListView.cellFactoryProperty());
        showListView.onEditCancelProperty().bind(rawListView.onEditCancelProperty());
        showListView.onEditCommitProperty().bind(rawListView.onEditCommitProperty());
        showListView.onEditStartProperty().bind(rawListView.onEditStartProperty());
        showListView.onScrollToProperty().bind(rawListView.onScrollToProperty());
        showListView.onScrollProperty().bind(rawListView.onScrollProperty());

        vBox = new VBox(){
            @Override
            protected double computePrefWidth(double height) {
                return showListView.prefWidth(height);
            }

            @Override
            protected double computePrefHeight(double width) {
                return showListView.prefHeight(width) + filterPane.prefHeight(width);
            }
        };
        vBox.getStyleClass().add("filter-v-box");
        vBox.setStyle("-fx-background-color: white");
        vBox.getChildren().addAll(filterPane, showListView);

        this.filterComboBox = control;
        if (filterComboBox.getStyleClass().contains("combo-box-ui")){
            filterField.getStyleClass().add("text-field-ui");
            if (filterComboBox.getStyleClass().contains("combo-box-ui-small")) {
                filterField.getStyleClass().addAll("text-field-ui-tiny");
            } else if (!filterComboBox.getStyleClass().contains("combo-box-ui-big") && !filterComboBox.getStyleClass().contains("combo-box-ui-tiny")){
                filterField.getStyleClass().addAll("text-field-ui-small");
            }
        }

        rawListView.itemsProperty().addListener((observable, oldValue, newValue) -> {
            activeRawFilter();
            addRawItemsListener(newValue);
        });
        addRawItemsListener(rawListView.getItems());

        rawListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showListView.getSelectionModel().select(newValue);
        });

        showListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!Objects.equals(newValue, rawListView.getSelectionModel().getSelectedItem())) {
                    rawListView.getSelectionModel().select(newValue);
                    filterComboBox.hide();
                }
            }
        });

        filterField.setOnFilterAction(text -> activeRawFilter());
    }

    private void addRawItemsListener(ObservableList<T> items){
        items.addListener((ListChangeListener<? super T>) observable -> activeRawFilter());
    }

    private void activeRawFilter(){
        T selectedItem = rawListView.getSelectionModel().getSelectedItem();
        ObservableList<T> items = rawListView.getItems();
        List<T> filter = filter(items);
        showItems.setAll(filter);
        showListView.getSelectionModel().select(selectedItem);
    }

    private List<T> filter(List<T> data){
        String key = filterField.getText();
        boolean ignoreCase = filterComboBox.isIgnoreCase();
        if (key == null || key.isBlank()) {
            return data.stream().toList();
        }else {
            String newKey = key.strip();
            return data
                    .stream()
                    .filter(item -> getShowText(item).contains(newKey) || (ignoreCase && getShowText(item).toLowerCase().contains(newKey.toLowerCase())))
                    .toList();
        }
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
        Platform.runLater(filterField::requestFocus);
    }

    @Override
    public void hide() {
        super.hide();
        filterField.setText("");
    }

    @Override
    public Node getPopupContent() {
        return vBox;
    }

}
