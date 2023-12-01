package club.xiaojiawei.readme;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 9:13
 */
public class ReadmeController implements Initializable {

    @FXML private TabPane rightTabPane;
    @FXML private TitledPane style;
    @FXML private TitledPane component;
    @FXML private ScrollPane leftScrollPane;
    @FXML private Accordion accordion;
    private final ToggleGroup toggleGroup = new ToggleGroup();
    /**
     * 初始选择tab的名字
     */
    private final static String INIT_SELECTED_TAB_NAME = "TableView";
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTab(style, "style");
        initTab(component, "component");
        rightTabPane.getSelectionModel().selectedItemProperty().addListener(selectionModelListener());
        toggleGroup.selectedToggleProperty().addListener(selectedToggleListener());
        if (toggleGroup.getSelectedToggle() != null){
            selectedNewToggle(toggleGroup.getSelectedToggle());
        }
    }
    private void initTab(TitledPane tab, String suffixPath){
        VBox vBox = new VBox();
        File files = new File(Objects.requireNonNull(JavaFXUI.class.getResource("/club/xiaojiawei/readme/tab/" + suffixPath)).getPath());
        for (File file : Objects.requireNonNull(files.listFiles())) {
            if (file.getName().contains(".fxml")){
                String name = file.getName().split("Tab.fxml")[0];
                ToggleButton toggleButton = new ToggleButton();
                toggleButton.getStyleClass().add("toggle-btn-ui");
                toggleButton.setToggleGroup(toggleGroup);
                toggleButton.setPrefWidth(leftScrollPane.getPrefWidth());
                toggleButton.setText(name);
                toggleButton.setUserData(suffixPath);
                if (isSelected(name)){
                    accordion.setExpandedPane(tab);
                    toggleButton.setSelected(true);
                }
                vBox.getChildren().add(toggleButton);
            }
        }
        tab.setContent(vBox);
    }
    private boolean isSelected(String name){
        return Objects.equals(INIT_SELECTED_TAB_NAME, name);
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
                for (int i = 0; i < rightTabPane.getTabs().size(); i++) {
                    Tab tab = rightTabPane.getTabs().get(i);
                    if (Objects.equals(tab.getText(), text)){
                        rightTabPane.getTabs().remove(i);
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
        String name = ((ToggleButton) Objects.requireNonNull(newValue, "newValue")).getText();
        for (int i = 0; i < rightTabPane.getTabs().size(); i++) {
            Tab tab = rightTabPane.getTabs().get(i);
            if (Objects.equals(tab.getText(), name)){
//            有新按钮对应的tab
                rightTabPane.getSelectionModel().select(i);
                return;
            }
        }
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("tab/" + newValue.getUserData() + "/" + name + "Tab.fxml")));
            /*Icons特殊处理：填充图标*/
            if (Objects.equals(name, "Icons") && node instanceof ScrollPane scrollPane){
                if (scrollPane.getContent() instanceof VBox vBox && vBox.getChildren().get(1) instanceof TilePane tilePane){
                    List<String> classes = SystemUtil.getClassesInPackage("club.xiaojiawei.controls.ico");
                    for (String aClass : classes) {
                        if (Class.forName(aClass).getConstructor().newInstance() instanceof Node ico) {
                            ico.setScaleX(1.2D);
                            ico.setScaleY(1.2D);
                            VBox group = new VBox();
                            group.setStyle("""
                                    -fx-background-color: white;
                                    -fx-padding: 10 5 5 5;
                                    -fx-alignment: CENTER;
                                    -fx-pref-height: 50;
                                    -fx-background-radius: 5;
                                    """);
                            VBox box = new VBox();
                            VBox.setVgrow(box, Priority.ALWAYS);
                            group.getChildren().addAll(ico, box, new Text(aClass.substring(aClass.lastIndexOf(".") + 1)));
                            tilePane.getChildren().add(group);
                        }
                    }
                }
            }
//                没有新按钮对应的tab，创建对应tab
            createTab(name, node);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTab(String title, Node node){
        Tab tab = new Tab(title, node);
        rightTabPane.getTabs().add(tab);
        rightTabPane.getSelectionModel().select(tab);
    }
}
