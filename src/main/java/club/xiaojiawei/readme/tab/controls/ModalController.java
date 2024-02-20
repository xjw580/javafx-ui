package club.xiaojiawei.readme.tab.controls;

import club.xiaojiawei.controls.Modal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/2 14:23
 */
public class ModalController {

    @FXML
    private ScrollPane rootPane;

    @FXML protected void showModal(ActionEvent actionEvent) {
        new Modal(rootPane, "卸载360安全卫士", "你确定要不卸载360安全助手吗？", () -> System.out.println("卸载失败"), () -> {System.out.println("卸载失败");}).show();
    }

}
