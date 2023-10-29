package club.xiaojiawei.readme;

import club.xiaojiawei.controls.Selection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/28 9:13
 */
public class ReadmeController implements Initializable {

    @FXML private TabPane tabPane;
    @FXML private Selection styleGroup;
    @FXML private TitledPane style;
    @FXML private Accordion accordion;
    @FXML private TitledPane component;
    @FXML private Selection componentGroup;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null){
                componentGroup.clearSelected();
                styleGroup.clearSelected();
                return;
            }
            String text = newValue.getText();
            for (Node node : styleGroup.getSelectGroup().getChildrenUnmodifiable()) {
                Label label = (Label) node;
                if (Objects.equals(label.getText(), text)){
                    componentGroup.clearSelected();
                    styleGroup.select(node);
                    accordion.setExpandedPane(style);
                    return;
                }
            }
            for (Node node : componentGroup.getSelectGroup().getChildrenUnmodifiable()) {
                Label label = (Label) node;
                if (Objects.equals(label.getText(), text)){
                    styleGroup.clearSelected();
                    componentGroup.select(node);
                    accordion.setExpandedPane(component);
                }
            }
        });
    }

    @FXML
    protected void styleClick(MouseEvent event) throws IOException {
        componentGroup.clearSelected();
        String text = getText(event);
        for (Tab tab : tabPane.getTabs()) {
            if (Objects.equals(tab.getText(), text)){
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        loadTab(text, FXMLLoader.load(Objects.requireNonNull(getClass().getResource("tab/" + text + ".fxml"))));
    }
    @FXML
    protected void componentClick(MouseEvent event) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        styleGroup.clearSelected();
        String text = getText(event);
        for (Tab tab : tabPane.getTabs()) {
            if (Objects.equals(tab.getText(), text)){
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }
        Class<?> aClass = Class.forName("club.xiaojiawei.controls." + text);
        Node object = (Node) aClass.getConstructor().newInstance();
        HBox hBox = new HBox(){{setAlignment(Pos.CENTER);}};
        VBox vBox = new VBox() {{setAlignment(Pos.CENTER);}};
        hBox.getChildren().add(vBox);
        if (object instanceof Selection selection){
            VBox vBoxTemp = new VBox(){{setSpacing(20);setAlignment(Pos.CENTER);}};
            vBoxTemp.getChildren().add(new Label("设置Selection中Parent类型selectGroup属性，将会为selectGroup的每个子节点添加bg-hover-ui类和鼠标点击监听器（点击后添加bg-ui类）"));
            HBox hBoxTemp = new HBox(){{setSpacing(20);}};
            vBoxTemp.getChildren().add(selection);
            for (int i = 0; i < 3; i++) {
                hBoxTemp.getChildren().add(new Label("Label" + i));
            }
            selection.setSelectGroup(hBoxTemp);
            selection.select(0);
            vBox.getChildren().add(vBoxTemp);
        }else {
            vBox.getChildren().add(object);
        }
        loadTab(text, hBox);
    }

    private void loadTab(String text, Node node){
        Tab tab = new Tab(text, node);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private String getText(MouseEvent event){
        String text = "";
        Node node = event.getPickResult().getIntersectedNode();
        if (node instanceof Text){
            text = ((Text) node).getText();
        }else if (node instanceof Label){
            text = ((Label) node).getText();
        }
        return text;
    }
}
