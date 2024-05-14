package club.xiaojiawei.demo;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.ico.AbstractIco;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 9:13
 */
@Slf4j
public class DemoController implements Initializable {

    @FXML private TabPane rightTabPane;

    @FXML private TitledPane style;

    @FXML private TitledPane controls;

    @FXML private ScrollPane leftScrollPane;

    @FXML private Accordion accordion;
    /**
     * 初始选择tab的名字
     */
    private final static String INIT_SELECTED_TAB_NAME = "TableView";

    private final ToggleGroup toggleGroup = new ToggleGroup();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTab(style, "style");
        initTab(controls, "controls");
        rightTabPane.getSelectionModel().selectedItemProperty().addListener(selectionModelListener());
        toggleGroup.selectedToggleProperty().addListener(selectedToggleListener());
        if (toggleGroup.getSelectedToggle() != null){
            selectedNewToggle(toggleGroup.getSelectedToggle());
        }
    }

    private void initTab(TitledPane tab, String suffixPath){
        String fxmlPath = "club/xiaojiawei/demo/tab/" + suffixPath;
        ArrayList<String> controlClassNameList = new ArrayList<>();
        if (Objects.equals(Objects.requireNonNull(this.getClass().getResource("")).getProtocol(), "jar")){
            try {
                Enumeration<JarEntry> entries = SystemUtil.getJarFile().entries();
                while (entries.hasMoreElements()){
                    String name = entries.nextElement().getName();
                    if (name.startsWith(fxmlPath) && name.endsWith("Tab.fxml")){
                        int startIndex = name.lastIndexOf("/") + 1;
                        int endIndex = name.lastIndexOf("Tab.fxml");
                        controlClassNameList.add(name.substring(startIndex, endIndex));
                    }
                }
            } catch (URISyntaxException | IOException e) {
                log.error("", e);
            }
        }else {
            File files = new File(Objects.requireNonNull(JavaFXUI.class.getResource("/" + fxmlPath), "/" + fxmlPath).getPath());
            for (File file : Objects.requireNonNull(files.listFiles(), "tabFileList")) {
                String name = file.getName();
                if (name.endsWith("Tab.fxml")){
                    controlClassNameList.add(name.split("Tab.fxml")[0]);
                }
            }
        }
        VBox vBox = new VBox();
        for (String name : controlClassNameList) {
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
        if (newValue == null){
            return;
        }
        String name = ((ToggleButton) newValue).getText();
        for (int i = 0; i < rightTabPane.getTabs().size(); i++) {
            Tab tab = rightTabPane.getTabs().get(i);
            if (Objects.equals(tab.getText(), name)){
//            有新按钮对应的tab
                rightTabPane.getSelectionModel().select(i);
                return;
            }
        }
        try {
            String fxmlPath = "tab/" + newValue.getUserData() + "/" + name + "Tab.fxml";
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath), fxmlPath));
            /*Icons特殊处理：填充图标*/
            if (Objects.equals(name, "Icons")
                    && node instanceof ScrollPane scrollPane
                    && scrollPane.getContent() instanceof VBox vBox
                    && vBox.getChildren().get(1) instanceof TilePane tilePane
            ){
                List<String> classes;
                if (Objects.equals(Objects.requireNonNull(this.getClass().getResource("")).getProtocol(), "jar")){
                    classes = new ArrayList<>();
                    try {
                        Enumeration<JarEntry> entries = SystemUtil.getJarFile().entries();
                        while (entries.hasMoreElements()){
                            JarEntry jarEntry = entries.nextElement();
                            String className = jarEntry.getName();
                            if (!jarEntry.isDirectory() && className.matches("^club/xiaojiawei/controls/ico/.*\\.class$")){
                                String replace = className.replace("/", ".").replace(".class", "");
                                classes.add(replace);
                            }
                        }
                    } catch (URISyntaxException e) {
                        log.error("", e);
                    }
                }else {
                    classes = SystemUtil.getClassesNameInPackage("club.xiaojiawei.controls.ico");
                }
                for (String aClass : classes) {
                    Class<?> aClass1 = Class.forName(aClass);
                    if (!Modifier.isAbstract(aClass1.getModifiers()) && aClass1.getConstructor().newInstance() instanceof AbstractIco ico) {
                        double scale = 1.5D;
                        ico.setScaleX(scale);
                        ico.setScaleY(scale);
                        VBox group = new VBox();
                        group.setStyle("""
                                -fx-background-color: white;
                                -fx-padding: 15 5 5 5;
                                -fx-alignment: CENTER;
                                -fx-background-radius: 5;
                                -fx-min-height: 58;
                                -fx-min-width: 123;
                                """);
                        VBox box = new VBox();
                        VBox.setVgrow(box, Priority.ALWAYS);
                        group.getChildren().addAll(ico, box, new Text(aClass.substring(aClass.lastIndexOf(".") + 1)));
                        tilePane.getChildren().add(group);
                    }
                }
            }
//            没有新按钮对应的tab，创建对应tab
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

    public void closeCurrentTab(){
        if (rightTabPane.getSelectionModel().getSelectedItem() != null){
            rightTabPane.getTabs().remove(rightTabPane.getSelectionModel().getSelectedItem());
        }
    }
}
