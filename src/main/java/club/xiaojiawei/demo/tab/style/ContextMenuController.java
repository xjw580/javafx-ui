package club.xiaojiawei.demo.tab.style;

import club.xiaojiawei.controls.ico.ClearIco;
import club.xiaojiawei.controls.ico.HelpIco;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/6 16:00
 */
public class ContextMenuController {

    @FXML
    private ScrollPane rootPane;

    @FXML void initialize(){
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getStyleClass().add("context-menu-ui");
        contextMenu.getItems().addAll(
                new MenuItem(){{setGraphic(new HBox(new HelpIco(), new Text("帮助")){{setStyle("-fx-spacing: 5;-fx-alignment: CENTER");}});}},
                new MenuItem(){{setGraphic(new HBox(new ClearIco(), new Text("删除")){{setStyle("-fx-spacing: 5;-fx-alignment: CENTER");}});}}
        );
        rootPane.setContextMenu(contextMenu);
    }
}
