package club.xiaojiawei.controls;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
//    TODO ObjectProperty<Node>改为SingleSelectionModel<Node>
    private final ObjectProperty<Node> selectedNodeProperty = new SimpleObjectProperty<>();

    public Node getSelectedNodeProperty() {
        return selectedNodeProperty.get();
    }

    public ObjectProperty<Node> selectedNodePropertyProperty() {
        return selectedNodeProperty;
    }

    public void setSelectedNodeProperty(Node selectedNodeProperty) {
        this.selectedNodeProperty.set(selectedNodeProperty);
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
                selectNode(node);
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
        for (Node node1 : childrenUnmodifiable) {
            if (Objects.equals(node1, node)){
                selectNode(node);
            }
        }
    }
    public void select(int index){
        ObservableList<Node> childrenUnmodifiable = selectGroup.getChildrenUnmodifiable();
        for (int i = 0; i < childrenUnmodifiable.size(); i++) {
            if (i == index){
                selectNode(childrenUnmodifiable.get(i));
            }
        }
    }
    private void selectNode(Node node){
        clearSelected();
        selectedNodeProperty.set(node);
        node.getStyleClass().add("bg-ui");
    }
    public void clearSelected(){
        if (selectedNodeProperty.get() != null){
            selectedNodeProperty.get().getStyleClass().remove("bg-ui");
        }
    }
}
