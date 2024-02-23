package club.xiaojiawei.skin;

import club.xiaojiawei.controls.FilterComboBox;
import club.xiaojiawei.controls.FilterField;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:54
 */
public class FilterComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {

    private final FilterComboBox<T> filterComboBox;

    @Getter
    private final FilterField filterField = new FilterField();

    private final StackPane filterStackPane = new StackPane(filterField);

    private final ListView<Object> rootListView = new ListView<>(){
        @Override
        protected double computePrefWidth(double height) {
            return filterComboBox.getWidth();
        }

        @Override
        protected double computePrefHeight(double width) {
            return 5 + popupHeight * (Math.min(FilterComboBoxListViewSkin.this.filterComboBox.getItems().size() + 1, FilterComboBoxListViewSkin.this.filterComboBox.getVisibleRowCount()));
        }
    };

    private final ListView<T> realListView;

    private boolean isManual = true;

    private double popupHeight;

    @SuppressWarnings("all")
    public FilterComboBoxListViewSkin(FilterComboBox<T> control) {
        super(control);

        this.filterComboBox = control;

        if (control.getStyleClass().contains("combo-box-ui")){
            if (control.getStyleClass().contains("combo-box-ui-small")){
                double height = 20D;
                filterField.setPrefHeight(height);
                filterField.setMaxHeight(height);
                filterField.setMinHeight(height);
            }
            filterField.getStyleClass().addAll("text-field-ui", "text-field-ui-small");
        }

        realListView = (ListView) super.getPopupContent();
        realListView.getItems().addListener((ListChangeListener<? super T>) change -> {
            rootListView.getItems().remove(1, rootListView.getItems().size());
            rootListView.getItems().addAll(realListView.getItems());
        });
        realListView.getSelectionModel().selectedItemProperty().addListener((observableValue, t, t1) -> {
            for (Object item : rootListView.getItems()) {
                if (Objects.equals(item, t1)){
                    rootListView.getSelectionModel().select(t1);
                }
            }
        });

        filterField.setOnFilterAction(text -> {
            isManual = false;
            ObservableList<Object> rootListViewItems = rootListView.getItems();
            if (text == null || text.isBlank()){
                if (rootListViewItems.size() != realListView.getItems().size() + 1){
                    Object selectedItem = realListView.getSelectionModel().getSelectedItem();
                    rootListViewItems.remove(1, rootListViewItems.size());
                    rootListViewItems.addAll(realListView.getItems());
                    rootListView.getSelectionModel().select(selectedItem);
                }
            }else {
                Object selectedItem = realListView.getSelectionModel().getSelectedItem();
                rootListViewItems.remove(1, rootListViewItems.size());
                rootListViewItems.addAll(realListView.getItems());
                for (int i = rootListViewItems.size() - 1; i > 0; i--) {
                    if (!rootListViewItems.get(i).toString().contains(text)){
                        rootListViewItems.remove(i);
                    }
                }
                rootListView.getSelectionModel().select(selectedItem);
            }
            isManual = true;
        });

        rootListView.getSelectionModel().selectedItemProperty().addListener((observableValue, o, t1) -> {
            if (isManual){
                if (t1 == null){
                    realListView.getSelectionModel().select(null);
                }else if (t1 != filterStackPane){
                    this.filterComboBox.hide();
                    realListView.getSelectionModel().select((T) t1);
                }
            }
        });
//        覆盖item在hover或者选中时的背景色
        filterStackPane.setStyle("-fx-background-color: white");
//        防止搜索框所在item被选中
        filterStackPane.addEventHandler(MouseEvent.MOUSE_PRESSED, Event::consume);
        rootListView.getItems().add(filterStackPane);
        rootListView.getItems().addAll(realListView.getItems());
    }

    @Override
    public void show() {
        super.show();
        double insect = 25D;
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
