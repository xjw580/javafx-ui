package club.xiaojiawei.skin;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.controls.FilterComboBox;
import club.xiaojiawei.controls.FilterField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:54
 */
public class FilterComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {

    private final FilterComboBox<T> filterComboBox;

    @Getter
    private final FilterField filterField = new FilterField();

    private final StackPane filterPane = new StackPane(filterField);

    private VBox vBox;

    private final ListView<T> realListView;

    private final ObservableList<T> rawItems;

    private boolean isOuter = true;


    @SuppressWarnings("all")
    public FilterComboBoxListViewSkin(FilterComboBox<T> control) {
        super(control);
        this.filterComboBox = control;
        this.filterField.setRealTime(true);
        realListView = (ListView) super.getPopupContent();
        vBox = new VBox(){
            @Override
            protected double computePrefWidth(double height) {
                return realListView.prefWidth(height);
            }

            @Override
            protected double computePrefHeight(double width) {
                return realListView.prefHeight(width) + filterPane.prefHeight(width);
            }
        };
        filterPane.setStyle("-fx-padding: 5");
        vBox.getChildren().addAll(filterPane, realListView);
        vBox.setStyle("-fx-background-color: white");
        realListView.setManaged(true);
        if (control.getStyleClass().contains("combo-box-ui")){
            filterField.getStyleClass().add("text-field-ui");
            if (control.getStyleClass().contains("combo-box-ui-small")) {
                filterField.getStyleClass().addAll("text-field-ui-tiny");
            } else if (!control.getStyleClass().contains("combo-box-ui-big") && !control.getStyleClass().contains("combo-box-ui-tiny")){
                filterField.getStyleClass().addAll("text-field-ui-small");
            }
        }
        rawItems = FXCollections.observableArrayList(realListView.getItems());

        realListView.itemsProperty().addListener((observable, oldValue, newValue) -> {
            rawItems.setAll(newValue);
            newValue.addListener((ListChangeListener<? super T>) observable1 -> {
                if (isOuter) {
                    rawItems.setAll(realListView.getItems());
                }
            });
        });
        realListView.getItems().addListener((ListChangeListener<? super T>) observable -> {
            if (isOuter) {
                rawItems.setAll(realListView.getItems());
            }
        });

        filterField.setOnFilterAction(text -> {
            isOuter = false;
            if (text == null || text.isBlank()){
                System.out.println("raw");
//                realListView.getItems().setAll(rawItems);
                T selectedItem = realListView.getSelectionModel().getSelectedItem();
                List<T> resullt = rawItems
                        .stream()
                        .filter(item -> !Objects.equals(selectedItem, item) && (getShowText(item).contains(text) || (control.isIgnoreCase() && getShowText(item).toLowerCase().contains(text.toLowerCase()))))
                        .toList();
                realListView.getItems().removeIf(item -> !Objects.equals(selectedItem, item));
                realListView.getItems().addAll(resullt);
            }else {
                System.out.println("filter:" + text);
                T selectedItem = realListView.getSelectionModel().getSelectedItem();
                Set<T> resullt = rawItems
                        .stream()
                        .filter(item -> (getShowText(item).contains(text) || (control.isIgnoreCase() && getShowText(item).toLowerCase().contains(text.toLowerCase()))))
                        .collect(Collectors.toSet());
                if (resullt.isEmpty()) {
                } else  if (resullt.contains(selectedItem)){
                    List<T> list = resullt.stream().filter(item -> !Objects.equals(selectedItem, item)).toList();
                    realListView.getItems().removeIf(item -> !Objects.equals(selectedItem, item));
                    realListView.getItems().addAll(list);
                }else {
                    realListView.getItems().setAll(resullt);
                }
            }

            isOuter = true;
        });
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
