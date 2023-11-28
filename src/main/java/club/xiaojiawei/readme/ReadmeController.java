package club.xiaojiawei.readme;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 9:13
 */
public class ReadmeController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private ToggleGroup toggleGroup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabPane.getSelectionModel().selectedItemProperty().addListener(selectionModelListener());
        toggleGroup.selectedToggleProperty().addListener(selectedToggleListener());
        if (toggleGroup.getSelectedToggle() != null){
            selectedNewToggle(toggleGroup.getSelectedToggle());
        }
    }
    private ChangeListener<Tab> selectionModelListener(){
        return (observable, oldValue, newValue) -> {
            if (newValue == null){
                toggleGroup.selectToggle(null);
                return;
            }
            String text = newValue.getText();
            for (Toggle toggle : toggleGroup.getToggles()) {
                ToggleButton toggleButton = (ToggleButton) toggle;
                if (Objects.equals(toggleButton.getText(), text)){
                    toggleGroup.selectToggle(toggle);
                    return;
                }
            }
        };
    }

    private ChangeListener<Toggle> selectedToggleListener(){
        return (observableValue, oldValue, newValue) -> {
            cancelOldToggle(oldValue, newValue);
            selectedNewToggle(newValue);
        };
    }

    /**
     * 取消旧按钮
     * @param oldValue
     * @param newValue
     */
    private void cancelOldToggle(Toggle oldValue, Toggle newValue){
        if (newValue == null){
            if (oldValue != null){
//                    取消选中旧button，删除对应的tab
                String text = ((ToggleButton) Objects.requireNonNull(oldValue, "oldValue")).getText();
                for (int i = 0; i < tabPane.getTabs().size(); i++) {
                    Tab tab = tabPane.getTabs().get(i);
                    if (Objects.equals(tab.getText(), text)){
                        tabPane.getTabs().remove(i);
                        return;
                    }
                }
            }
            return;
        }
    }
    /**
     * 选中新按钮
     * @param newValue
     */
    private void selectedNewToggle(Toggle newValue){
        String text = ((ToggleButton) Objects.requireNonNull(newValue, "newValue")).getText();
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            Tab tab = tabPane.getTabs().get(i);
            if (Objects.equals(tab.getText(), text)){
//            有新按钮对应的tab
                tabPane.getSelectionModel().select(i);
                return;
            }
        }
        try {
//                没有新按钮对应的tab，创建对应tab
            createTab(text, FXMLLoader.load(Objects.requireNonNull(getClass().getResource("tab/" + text + "Tab.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTab(String text, Node node){
        Tab tab = new Tab(text, node);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
