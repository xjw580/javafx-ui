package club.xiaojiawei.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/3/7 12:30
 */
@Getter
public class FileUploaderItem extends AnchorPane{

    @FXML
    private AnchorPane fileUploaderItem;
    @FXML
    private VBox msgOuter;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label fileSizeLabel;
    @FXML
    private ProgressIndicator uploadProgressIndicator;
    @FXML
    private StackPane closeIcoOuter;
    @FXML
    private Group closeIcoGroup;

    public FileUploaderItem() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置文件大小
     * @param fileLength Byte
     */
    public void setFileSize(long fileLength){
        String size;
        if (fileLength > 1024 * 1024 * 1024){
            size = String.format("%.2f%s", fileLength / (1024D * 1024 * 1024), "GB");
        }else if (fileLength > 1024 * 1024){
            size = String.format("%.2f%s", fileLength / (1024D * 1024), "MB");
        }else if (fileLength > 1024){
            size = String.format("%.2f%s", fileLength / 1024D, "KB");
        }else {
            size = String.format("%d%s", fileLength, "B");
        }
        fileSizeLabel.setText(size);
        fileSizeLabel.setTooltip(new Tooltip(size));
    }

    public void setFileName(String fileName){
        fileNameLabel.setText(fileName);
        fileNameLabel.setTooltip(new Tooltip(fileName));
    }

    @SuppressWarnings("all")
    public void setBackgroundImage(String filePath){
        filePath = encodePath(filePath.replace("\\", "/"));
        setStyle("-fx-background-image: url(\"file:/"+ filePath +"\");");
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
