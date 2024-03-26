package club.xiaojiawei.demo.tab.controls;

import club.xiaojiawei.controls.FilterField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/26 14:50
 */
public class IconsController {

    @FXML
    private FilterField filterField;

    @FXML
    private TilePane iconPane;

    @FXML void initialize(){
        filterField.setOnFilterAction(text -> {
            for (Node child : iconPane.getChildren()) {
                if (child instanceof VBox vBox){
                    ObservableList<Node> children = vBox.getChildren();
                    if (children.get(children.size() - 1) instanceof Text text1){
                        if (text1.getText().toLowerCase().contains(text.toLowerCase())){
                            vBox.setManaged(true);
                            vBox.setVisible(true);
                        }else {
                            vBox.setManaged(false);
                            vBox.setVisible(false);
                        }
                    }
                }
            }
        });
    }

}
