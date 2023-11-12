package club.xiaojiawei.controls;

import club.xiaojiawei.controls.ico.FailIco;
import club.xiaojiawei.enums.MimeEnum;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static club.xiaojiawei.enums.MimeEnum.IMAGE_JPEG;
import static club.xiaojiawei.enums.MimeEnum.IMAGE_PNG;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/9 15:58
 */
public class FileUploader extends AnchorPane {
    private final IntegerProperty columns = new SimpleIntegerProperty(1);
    private ObservableList<MimeEnum> fileTypes = FXCollections.observableArrayList(MimeEnum.ALL);
    private int maxFileSize = Integer.MAX_VALUE;
    private final ObservableList<String> fileURLs = FXCollections.observableArrayList();
    private final ObservableList<ProgressIndicator> indicators = FXCollections.observableArrayList();

    public ObservableList<String> getFileURLs() {
        return fileURLs;
    }

    public ObservableList<ProgressIndicator> getIndicators() {
        return indicators;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getColumns() {
        return columns.get();
    }

    public IntegerProperty columnsProperty() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns.set(columns);
    }

    public ObservableList<MimeEnum> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(ObservableList<MimeEnum> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public FileUploader() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();

            addListener();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML private Text tip;
    @FXML private TilePane content;
    private final Tika tika = new Tika();
    private void addListener(){
        content.setOnDragOver(event -> {
            if (event.getGestureSource() != content) {
                event.acceptTransferModes(TransferMode.ANY);
            }
        });
        content.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                try {
                    for (File file : db.getFiles()) {
                        if (content.getChildren().size() > maxFileSize){
                            return;
                        }
                        String mimeType = tika.detect(file);
                        if (containsFileType(mimeType)){
                            content.getChildren().add(buildFilePane(file.getPath(), mimeType));
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            event.setDropCompleted(true);
        });
        content.getChildren().addListener((ListChangeListener<Node>) c -> {
            if (content.getChildren().size() == 1){
                tip.setManaged(true);
                tip.setVisible(true);
            }else {
                tip.setVisible(false);
                tip.setManaged(false);
            }
        });
        content.setOnMouseClicked(event -> {
            if (event.getTarget() == content){
                FileChooser fileChooser = new FileChooser();
                File chooseFile = fileChooser.showOpenDialog(new Stage());
                if (chooseFile != null){
                    String mimeType;
                    try {
                        mimeType = tika.detect(chooseFile);
                        if (containsFileType(mimeType)){
                            content.getChildren().add(buildFilePane(chooseFile.getPath(), mimeType));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        content.setOnDragEntered(event -> content.setStyle("-fx-border-style: solid"));
        content.setOnDragExited(event -> content.setStyle("-fx-border-style: dashed"));
        columns.addListener((observable, oldValue, newValue) -> content.setPrefWidth(30 + newValue.intValue() * 115));
    }
    private boolean containsFileType(String fileType){
        for (MimeEnum type : fileTypes) {
            if (type == MimeEnum.ALL || Objects.equals(fileType, type.getMimeType())){
                return true;
            }
        }
        return false;
    }
    @SuppressWarnings("all")
    private AnchorPane buildFilePane(String url, String fileType){
        FailIco failIco = new FailIco();
        failIco.setTranslateX(93);
        failIco.setTranslateY(-5);
        failIco.setScaleY(1.5D);
        failIco.setScaleX(1.5D);
        failIco.getStyleClass().add("close");
        ProgressIndicator progressIndicator = new ProgressIndicator(0) {{
            getStyleClass().add("progress-indicator-ui");
        }};
        progressIndicator.progressProperty().addListener((observable, oldValue, newValue) -> {
            progressIndicator.setVisible(newValue.doubleValue() != 0D);
        });
        progressIndicator.setTranslateX(40);
        progressIndicator.setTranslateY(40);
        progressIndicator.setVisible(false);
        indicators.add(progressIndicator);
        AnchorPane imagePane = new AnchorPane();
        imagePane.setPrefHeight(100);
        imagePane.setPrefWidth(100);
        imagePane.getStyleClass().add("image");
        fileURLs.add(url);
        url = encodePath(url.replace("\\", "/"));
        if (Objects.equals(fileType, IMAGE_JPEG.getMimeType()) || Objects.equals(fileType, IMAGE_PNG.getMimeType())){
            imagePane.setStyle("-fx-background-image: url(\"file:/"+ url +"\");");
        }else {
            imagePane.setStyle("-fx-background-color: #e0e0e0");
        }
        imagePane.getChildren().addAll(
                progressIndicator,
                failIco
        );
        failIco.setOnMouseClicked(event -> {
            ObservableList<Node> children = content.getChildren();
            for (int i = 1; i < children.size(); i++) {
                if (children.get(i) == imagePane){
                    children.remove(i);
                    fileURLs.remove(i - 1);
                    indicators.remove(i - 1);
                    return;
                }
            }
        });
        return imagePane;
    }

    private String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8).replace("+", "%20");
    }

}