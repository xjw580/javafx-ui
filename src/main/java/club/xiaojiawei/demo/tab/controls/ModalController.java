package club.xiaojiawei.demo.tab.controls;

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
        Modal modal = new Modal(rootPane, "卸载360安全卫士", "你确定要不卸载360安全助手吗？", () -> System.out.println("确认"), () -> {
            System.out.println("取消");
        });
        modal.setMaskClosable(true);
        modal.show();
    }

}
