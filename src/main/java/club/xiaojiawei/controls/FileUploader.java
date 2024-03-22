package club.xiaojiawei.controls;

import club.xiaojiawei.component.FileUploaderItem;
import club.xiaojiawei.enums.MimeEnum;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static club.xiaojiawei.enums.MimeEnum.IMAGE_ALL;

/**
 * 文件上传
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/9 15:58
 */
public class FileUploader extends TilePane {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 每行最多展示几个文件
     */
    @Getter
    @Setter
    private int maxColumns = 3;
    /**
     * 允许的文件类型，通过后缀名判断。自行通过文件头等方式判断真实文件类型
     */
    @Setter
    @Getter
    private ObservableList<MimeEnum> fileTypes = FXCollections.observableArrayList(MimeEnum.ALL);
    /**
     * 允许的最大文件数量
     */
    @Setter
    @Getter
    private int maxFileQuantity = Integer.MAX_VALUE;
    /**
     * 文件添加失败事件处理器
     */
    private final ObjectProperty<Consumer<FailEvent>> onAddFileFailEventHandler = new SimpleObjectProperty<>();
    /**
     * 提示词
     */
    private final StringProperty tip;

    public String getTip() {
        return tip.get();
    }

    public StringProperty tipProperty() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip.set(tip);
    }

    public Consumer<FailEvent> getOnAddFileFailEventHandler() {
        return onAddFileFailEventHandler.get();
    }

    public ObjectProperty<Consumer<FailEvent>> onAddFileFailEventHandlerProperty() {
        return onAddFileFailEventHandler;
    }

    public void setOnAddFileFailEventHandler(Consumer<FailEvent> onAddFileFailEventHandler) {
        this.onAddFileFailEventHandler.set(onAddFileFailEventHandler);
    }

    /* *************************************************************************
     *                                                                         *
     * 数据                                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * 存储上传文件的路径
     */
    @Getter
    private final ObservableList<String> fileURLs = FXCollections.observableArrayList();
    /**
     * 每个文件上传进度指示器，进度大于0时自动显示
     */
    @Getter
    private final ObservableList<ProgressIndicator> indicators = FXCollections.observableArrayList();

    @Getter
    private String lastChooseDir;

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public FileUploader() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            addListener();
            tip = tipLabel.textProperty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML private Label tipLabel;

    private static final Tika tika = new Tika();

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void addListener(){
        this.setOnDragOver(event -> {
            if (event.getGestureSource() != this) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });
        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    if (!addFile(file)){
                        break;
                    }
                }
            }
            event.setDropCompleted(true);
        });
        this.getChildren().addListener((ListChangeListener<Node>) c -> {
            if (this.getChildren().size() == 1){
                tipLabel.setManaged(true);
                tipLabel.setVisible(true);
            }else {
                tipLabel.setVisible(false);
                tipLabel.setManaged(false);
            }
            int size = Math.min(this.getChildren().size() - 1, maxColumns);
            this.setMaxWidth(30 + 2 + size * 100 + (size == 0? 0 : (size - 1) * 17));
        });
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("target:" + event.getTarget());
            if (this.getChildren().size() <= maxFileQuantity){
                FileChooser fileChooser = new FileChooser();
                if (lastChooseDir != null){
                    fileChooser.setInitialDirectory(new File(lastChooseDir));
                }
                addFile(fileChooser.showOpenDialog(new Stage()));
            }
        });
        this.setOnDragEntered(event -> this.setStyle("-fx-border-style: solid"));
        this.setOnDragExited(event -> this.setStyle("-fx-border-style: dashed"));
    }

    private boolean addFile(File file){
        if (file == null){
            return false;
        }
        if (this.getChildren().size() > maxFileQuantity){
            if (this.onAddFileFailEventHandler.get() != null){
                this.onAddFileFailEventHandler.get().accept(FailEvent.MAX_QUANTITY_LIMIT);
            }
            return false;
        }
        lastChooseDir = file.getParent();
        String mimeType = tika.detect(file.getName());
        if (isAllowFileType(mimeType)){
            this.getChildren().add(buildFilePane(file.getPath(), mimeType));
        }else {
            if (this.onAddFileFailEventHandler.get() != null){
                this.onAddFileFailEventHandler.get().accept(FailEvent.FILE_TYPE_LIMIT);
            }
            return false;
        }
        return true;
    }

    private boolean isAllowFileType(String fileType){
        if (fileType == null || fileType.isBlank()){
            return false;
        }
        for (MimeEnum type : fileTypes) {
            if (type == MimeEnum.ALL || fileType.startsWith(type.getMimeType())){
                return true;
            }
        }
        return false;
    }

    private AnchorPane buildFilePane(String filePath, String fileType){
        FileUploaderItem fileUploaderItem = new FileUploaderItem();
        ProgressIndicator uploadProgressIndicator = fileUploaderItem.getUploadProgressIndicator();
        uploadProgressIndicator.progressProperty().addListener((observable, oldValue, newValue) -> {
            uploadProgressIndicator.setVisible(newValue.doubleValue() != 0D);
        });
        indicators.add(uploadProgressIndicator);

        fileURLs.add(filePath);
        File file = new File(filePath);
        fileUploaderItem.setFileSize(file.length());
        fileUploaderItem.setFileName(file.getName());

        if (fileType != null && fileType.startsWith(IMAGE_ALL.getMimeType())){
            fileUploaderItem.setBackgroundImage(filePath);
        }

        fileUploaderItem.getCloseIcoGroup().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            event.consume();
            ObservableList<Node> children = this.getChildren();
            for (int i = 1; i < children.size(); i++) {
                if (children.get(i) == fileUploaderItem){
                    children.remove(i);
                    fileURLs.remove(i - 1);
                    indicators.remove(i - 1);
                    return;
                }
            }
        });

        return fileUploaderItem;
    }

    public enum FailEvent{

        /**
         * 超过最大文件数量
         */
        MAX_QUANTITY_LIMIT,

        /**
         * 非法文件类型
         */
        FILE_TYPE_LIMIT

    }

}