package club.xiaojiawei.controls.ico;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 23:18
 */
public class DownIco extends StackPane {

    public DownIco() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
