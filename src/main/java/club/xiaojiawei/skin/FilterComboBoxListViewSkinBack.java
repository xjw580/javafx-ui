package club.xiaojiawei.skin;

import club.xiaojiawei.controls.FilterComboBox;
import club.xiaojiawei.controls.FilterField;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Group;
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
public class FilterComboBoxListViewSkinBack<T> extends ComboBoxListViewSkin<T> {

    private final FilterComboBox<T> filterComboBox;

    @Getter
    private final FilterField filterField = new FilterField();


    @SuppressWarnings("all")
    public FilterComboBoxListViewSkinBack(FilterComboBox<T> control) {
        super(control);
        filterField.setMaxWidth(100);
        filterField.getStyleClass().addAll("text-field-ui", "text-field-ui-normal");
//        filterField.setTranslateY(1);
//        setHideOnClick(false);
        this.filterComboBox = control;
    }
    private Group group =  new Group(filterField);
    @Override
    public void show() {
        super.show();
//        filterField.requestFocus();
//        if (getChildren().get(0) instanceof StackPane stackPane){
//            stackPane.getChildren().add(filterField);
//            System.out.println(stackPane.getChildren());
//        }
        getChildren().add(group);

    }

    @Override
    public void hide() {
        super.hide();
        getChildren().remove(group);
    }
}
