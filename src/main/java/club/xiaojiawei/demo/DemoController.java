package club.xiaojiawei.demo;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.FilterField;
import club.xiaojiawei.controls.ico.AbstractIco;
import club.xiaojiawei.utils.SystemUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 9:13
 */
@Slf4j
public class DemoController implements Initializable {

    @FXML private Label resultCount;

    @FXML private SplitPane rootPane;

    @FXML private FilterField search;

    @FXML private TabPane rightTabPane;

    @FXML private TitledPane style;

    @FXML private TitledPane controls;

    @FXML private ScrollPane leftScrollPane;

    @FXML private Accordion accordion;
    /**
     * 初始选择tab的名字
     */
    private final static String INIT_SELECTED_TAB_NAME = "";
//    private final static String INIT_SELECTED_TAB_NAME = "Notification";

    private final ToggleGroup toggleGroup = new ToggleGroup();

    private ObservableList<Node> styleItems;

    private ObservableList<Node> controlsItems;

    private List<Node> styleRawItems;

    private List<Node> controlsRawItems;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        search.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER && search.getText() != null && !search.getText().isBlank() && styleItems.size() + controlsItems.size() == 1) {
                ObservableList<Node> list = styleItems.isEmpty()? controlsItems : styleItems;
                for (Node node : list) {
                    if (node instanceof ToggleButton toggleButton) {
                        toggleGroup.selectToggle(toggleButton);
                    }
                }
            } else if (event.getCode() == KeyCode.DOWN) {
                if (toggleGroup.getSelectedToggle() == null) {
                    if (!styleItems.isEmpty()){
                        toggleGroup.selectToggle((Toggle) styleItems.get(0));
                    }else if (!controlsItems.isEmpty()){
                        toggleGroup.selectToggle((Toggle) controlsItems.get(0));
                    }
                    return;
                }
                for (int i = 0; i < styleItems.size(); i++) {
                    Node node = styleItems.get(i);
                    if (node instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), ((ToggleButton)toggleGroup.getSelectedToggle()).getText())) {
                        if (i + 1 < styleItems.size()) {
                            toggleGroup.selectToggle((Toggle) styleItems.get(i + 1));
                        }else {
                            if (!controlsItems.isEmpty()){
                                toggleGroup.selectToggle((Toggle) controlsItems.get(0));
                            }
                        }
                        return;
                    }
                }
                for (int i = 0; i < controlsItems.size(); i++) {
                    Node node = controlsItems.get(i);
                    if (node instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), ((ToggleButton)toggleGroup.getSelectedToggle()).getText())) {
                        if (i + 1 < controlsItems.size()) {
                            toggleGroup.selectToggle((Toggle) controlsItems.get(i + 1));
                        }else {
                            if (!styleItems.isEmpty()){
                                toggleGroup.selectToggle((Toggle) styleItems.get(0));
                            }
                        }
                        return;
                    }
                }
            } else if (event.getCode() == KeyCode.UP) {
                if (toggleGroup.getSelectedToggle() == null) {
                    if (!controlsItems.isEmpty()){
                        toggleGroup.selectToggle((Toggle) controlsItems.get(controlsItems.size() - 1));
                    }else if (!styleItems.isEmpty()){
                        toggleGroup.selectToggle((Toggle) styleItems.get(styleItems.size() - 1));
                    }
                    return;
                }
                for (int i = 0; i < styleItems.size(); i++) {
                    Node node = styleItems.get(i);
                    if (node instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), ((ToggleButton)toggleGroup.getSelectedToggle()).getText())) {
                        if (i - 1 >= 0) {
                            toggleGroup.selectToggle((Toggle) styleItems.get(i - 1));
                        }else {
                            if (!controlsItems.isEmpty()){
                                toggleGroup.selectToggle((Toggle) controlsItems.get(controlsItems.size() - 1));
                            }
                        }
                        return;
                    }
                }
                for (int i = 0; i < controlsItems.size(); i++) {
                    Node node = controlsItems.get(i);
                    if (node instanceof ToggleButton toggleButton && Objects.equals(toggleButton.getText(), ((ToggleButton)toggleGroup.getSelectedToggle()).getText())) {
                        if (i - 1 >= 0) {
                            toggleGroup.selectToggle((Toggle) controlsItems.get(i - 1));
                        }else {
                            if (!styleItems.isEmpty()){
                                toggleGroup.selectToggle((Toggle) styleItems.get(styleItems.size() - 1));
                            }
                        }
                        return;
                    }
                }
            }
        });
        styleItems = initTab(style, "style");
        controlsItems = initTab(controls, "controls");
        styleRawItems = new ArrayList<>(styleItems);
        controlsRawItems = new ArrayList<>(controlsItems);
        rightTabPane.getSelectionModel().selectedItemProperty().addListener(selectionModelListener());
        toggleGroup.selectedToggleProperty().addListener(selectedToggleListener());
        if (toggleGroup.getSelectedToggle() != null){
            selectedNewToggle(toggleGroup.getSelectedToggle());
        }
        search.requestFocus();
        search.setOnFilterAction(text -> {
            List<Node> styleList, controlsList;
            if (text == null || text.isBlank()) {
                styleList = styleRawItems;
                controlsList = controlsRawItems;
            }else {
                styleList = styleRawItems
                        .stream()
                        .filter(node -> node instanceof ToggleButton toggleButton && toggleButton.getText().toLowerCase().contains(text.toLowerCase()))
                        .toList();
                controlsList = controlsRawItems
                        .stream()
                        .filter(node -> node instanceof ToggleButton toggleButton && toggleButton.getText().toLowerCase().contains(text.toLowerCase()))
                        .toList();
            }
            if (styleList.size() > controlsList.size()){
                accordion.setExpandedPane(style);
            }else {
                accordion.setExpandedPane(controls);
            }
            styleItems.setAll(styleList);
            controlsItems.setAll(controlsList);
            resultCount.setText(String.valueOf(styleList.size() + controlsList.size()));
        });
        Platform.runLater(() -> {
            rootPane.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), () -> {
                search.requestFocus();
            });
        });
    }

    private ObservableList<Node> initTab(TitledPane tab, String suffixPath){
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
        return vBox.getChildren();
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
            for (Node styleItem : styleItems) {
                if (styleItem instanceof ToggleButton toggleButton && Objects.equals(toggleButton, newValue)){
                    accordion.setExpandedPane(style);
                    return;
                }
            }
            for (Node styleItem : controlsItems) {
                if (styleItem instanceof ToggleButton toggleButton && Objects.equals(toggleButton, newValue)){
                    accordion.setExpandedPane(controls);
                    return;
                }
            }
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

    @FXML
    private void gotoWeb() {
        // 判断桌面是否支持浏览器调用
        if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            // 调用默认浏览器打开网页
            try {
                Desktop.getDesktop().browse(new URI("http://192.168.1.100:4012/club.xiaojiawei.javafx_ui/module-summary.html"));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
