package club.xiaojiawei.controls;

import javafx.beans.DefaultProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 9:42
 */
@DefaultProperty("selectGroup")
public class Selection extends AnchorPane {
    private Parent selectGroup;
    private final ObjectProperty<Node> selectedItemProperty = new SimpleObjectProperty<>();
    private final IntegerProperty selectedIndexProperty = new SimpleIntegerProperty(-1);

    public Node getSelectedItemProperty() {
        return selectedItemProperty.get();
    }

    public ObjectProperty<Node> selectedItemPropertyProperty() {
        return selectedItemProperty;
    }

    public void setSelectedItemProperty(Node selectedItemProperty) {
        this.selectedItemProperty.set(selectedItemProperty);
    }

    public int getSelectedIndexProperty() {
        return selectedIndexProperty.get();
    }

    public IntegerProperty selectedIndexPropertyProperty() {
        return selectedIndexProperty;
    }

    public void setSelectedIndexProperty(int selectedIndexProperty) {
        this.selectedIndexProperty.set(selectedIndexProperty);
    }

    public Parent getSelectGroup() {
        return selectGroup;
    }
    public void setSelectGroup(Parent selectGroup) {
        this.selectGroup = selectGroup;
        ObservableList<Node> childrenUnmodifiable = selectGroup.getChildrenUnmodifiable();
        for (Node node : childrenUnmodifiable) {
            node.getStyleClass().add("bg-hover-ui");
            node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                event.consume();
                select(node);
                selectGroup.fireEvent(event);
            });
        }
        this.getChildren().add(selectGroup);
    }

    public Selection() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void select(Node node){
        ObservableList<Node> childrenUnmodifiable = selectGroup.getChildrenUnmodifiable();
        for (int i = 0; i < childrenUnmodifiable.size(); i++) {
            if (Objects.equals(node, childrenUnmodifiable.get(i))){
                selectNode(childrenUnmodifiable.get(i), i);
            }
        }
    }
    public void select(int index){
        ObservableList<Node> childrenUnmodifiable = selectGroup.getChildrenUnmodifiable();
        for (int i = 0; i < childrenUnmodifiable.size(); i++) {
            if (i == index){
                selectNode(childrenUnmodifiable.get(i), i);
            }
        }
    }
    private void selectNode(Node node, int index){
        clearStyle();
        selectedItemProperty.set(node);
        selectedIndexProperty.set(index);
        node.getStyleClass().add("bg-ui");
    }
    private void clearStyle(){
        if (selectedItemProperty.get() != null){
            selectedItemProperty.get().getStyleClass().remove("bg-ui");
        }
    }
    public void clearSelected(){
        clearStyle();
        selectedItemProperty.set(null);
        selectedIndexProperty.set(-1);
    }
}
