package club.xiaojiawei.skin;

import club.xiaojiawei.controls.FilterComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:54
 */
public class FilterComboBoxListViewSkin extends ComboBoxListViewSkin {

    public FilterComboBoxListViewSkin(FilterComboBox control) {
        super(control);
//        ListView listView = (ListView) getChildren().remove(1);
//        StackPane stackPane = new StackPane(new Button("hello"));
//        VBox vBox = new VBox(stackPane, listView);
//        getChildren().add(vBox);
        ListView o = (ListView) getChildren().get(1);
        o.getItems().add(0, "1111");
    }


}
