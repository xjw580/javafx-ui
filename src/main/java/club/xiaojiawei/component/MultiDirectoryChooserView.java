package club.xiaojiawei.component;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.annotations.Nullable;
import club.xiaojiawei.controls.FilterField;
import club.xiaojiawei.controls.Modal;
import club.xiaojiawei.controls.MultiDirectoryChooser;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.ico.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author 肖嘉威
 * @date 2024/12/2 16:09
 */
@Slf4j
public class MultiDirectoryChooserView extends StackPane {

    @FXML
    private Label title;
    @FXML
    private TreeView<File> fileTreeView;
    @FXML
    private FilterField url;
    @FXML
    private ImageView icon;
    @FXML
    private NotificationManager<Object> notificationManager;
    @FXML
    private InvisibleIco hideHiddenFile;
    @FXML
    private VisibleIco showHiddenFile;

    private final Consumer<List<File>> callback;

    private final MultiDirectoryChooser multiDirectoryChooser;

    public MultiDirectoryChooserView(MultiDirectoryChooser multiDirectoryChooser, Consumer<List<File>> callback) {
        this.callback = callback;
        this.multiDirectoryChooser = multiDirectoryChooser;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh() {
        title.setText(multiDirectoryChooser.getTitle());
        File initialDirectory = multiDirectoryChooser.getInitialDirectory();
        List<TreeItem<File>> treeItems = loadFiles(null, true);
        TreeItem<File> root = fileTreeView.getRoot();
        if (root == null) {
            root = new TreeItem<>();
            fileTreeView.setRoot(root);
            fileTreeView.setShowRoot(false);
        }
        root.setExpanded(true);
        root.getChildren().setAll(treeItems);
        if (initialDirectory != null) {
            selectFileItem(initialDirectory);
        }
        fileTreeView.refresh();
    }

    private List<TreeItem<File>> loadFiles(File dir, boolean loadChildren) {
        File[] files;
        if (dir == null) {
            files = File.listRoots();
        } else {
            files = dir.listFiles();
        }
        if (files == null) {
            return Collections.emptyList();
        } else {
            ArrayList<TreeItem<File>> result = new ArrayList<>();
            for (File file : files) {
                if (hideHiddenFile.isVisible() && file.isHidden() && !file.getName().isEmpty()) continue;
                TreeItem<File> treeItem = new TreeItem<>(file);
                if (loadChildren) {
                    treeItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            treeItem.getChildren().setAll(loadFiles(file, true));
                        }
                    });
                    treeItem.getChildren().setAll(loadFiles(file, false));
                }
                result.add(treeItem);
            }
            return result;
        }
    }

    private void selectFileItem(File targetFile) {
        if (targetFile == null || !targetFile.exists()) return;
        TreeItem<File> treeItem = searchFileItem(targetFile, fileTreeView.getRoot(), 0);
        if (treeItem != null) {
            if (fileTreeView.getSelectionModel().getSelectedItem() != treeItem) {
                selectFileItem(targetFile);
            }
        }
    }

    @Nullable
    private TreeItem<File> searchFileItem(@NotNull File targetFile, @NotNull TreeItem<File> fileTreeItem, int index) {
        if (Objects.equals(targetFile, fileTreeItem.getValue())) {
            fileTreeItem.setExpanded(true);
            fileTreeView.getSelectionModel().clearSelection();
            int i = index - 1;
            fileTreeView.getSelectionModel().select(i);
            fileTreeView.scrollTo(i);
            return fileTreeItem;
        }
        ObservableList<TreeItem<File>> files = fileTreeItem.getChildren();
        if (files.isEmpty()) {
            files.setAll(loadFiles(fileTreeItem.getValue(), false));
        }
        for (int i = 0; i < files.size(); i++) {
            TreeItem<File> file = files.get(i);
            if (targetFile.toPath().startsWith(file.getValue().toPath())) {
                fileTreeItem.setExpanded(true);
                return searchFileItem(targetFile, file, index + i + 1);
            }
        }
        return null;
    }

    private boolean ctrlDown;

    private void afterFXMLLoaded() {
        url.setOnFilterAction(text -> {
            if (text == null || text.isBlank()) return;
            selectFileItem(new File(text));
        });
        fileTreeView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                ctrlDown = true;
                System.out.println("control");
            } else if (ctrlDown && event.getCode() == KeyCode.C) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putFiles(getSelectedFiles());
                clipboard.setContent(content);
                System.out.println("fuzhi");
            }
        });
        fileTreeView.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                ctrlDown = false;
            }
        });
        fileTreeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<File> call(TreeView<File> param) {
                return new TreeCell<>() {

                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            String name = item.getName();
                            if (name.isBlank()) {
                                name = item.getAbsolutePath();
                            }
                            setText(name);
                            double scale = 0.9;
                            if (item.isDirectory()) {
                                AbstractIco dirIcon = new DirIco();
                                if (item.isHidden() && !item.getName().isBlank()) {
                                    dirIcon.setColor("gray");
                                    setStyle("-fx-text-fill: rgb(113,113,165)");
                                }else {
                                    dirIcon.setColor("black");
                                    setStyle("-fx-text-fill: black");
                                }
                                dirIcon.setScaleX(scale);
                                dirIcon.setScaleY(scale);
                                setGraphic(dirIcon);
                            } else {
                                AbstractIco fileIcon = new UnknowFileIco();
                                if (item.isHidden() && !item.getName().isBlank()) {
                                    fileIcon.setColor("gray");
                                    setStyle("-fx-text-fill: rgb(113,113,165)");
                                }else {
                                    fileIcon.setColor("black");
                                    setStyle("-fx-text-fill: black");
                                }
                                fileIcon.setScaleX(scale);
                                fileIcon.setScaleY(scale);
                                setGraphic(fileIcon);
                            }
                        }
                    }
                };
            }
        });
        fileTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                url.setText("");
            } else {
                url.setText(newValue.getValue().getAbsolutePath());
            }
        });

    }

    private List<File> getSelectedFiles() {
        ObservableList<TreeItem<File>> selectedItems = fileTreeView.getSelectionModel().getSelectedItems();
        ArrayList<File> files = new ArrayList<>();
        selectedItems.forEach((item) -> {
            if (item.getValue() != null) {
                files.add(item.getValue());
            }
        });
        return files;
    }

    @FXML
    protected void ok() {
        List<File> files = getSelectedFiles();
        if (callback != null) {
            callback.accept(files);
        }
    }

    @FXML
    protected void cancel() {
        if (callback != null) {
            callback.accept(null);
        }
    }

    @FXML
    protected void homeDir() {
        selectFileItem(new File(System.getProperty("user.home")));
    }

    @FXML
    protected void desktopDir() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        selectFileItem(fsv.getHomeDirectory());
    }

    @FXML
    protected void newDir() {
        TreeItem<File> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;
        File file = selectedItem.getValue();
        if (file == null) return;

        File dir;
        TreeItem<File> dirItem;
        if (file.isDirectory()) {
            dir = file;
            dirItem = selectedItem;
        } else {
            dir = file.getParentFile();
            dirItem = selectedItem.getParent();
        }

        TextField textField = new TextField();
        textField.getStyleClass().addAll("text-field-ui", "text-field-ui-small");
        Button ok = new Button("确定");
        ok.getStyleClass().addAll("btn-ui", "btn-ui-success", "btn-ui-small");
        Button cancel = new Button("取消");
        cancel.getStyleClass().addAll("btn-ui", "btn-ui-small");

        VBox root = new VBox(
                new Label("输入新文件夹名:"),
                textField,
                new HBox(ok, cancel) {{
                    setStyle("-fx-spacing: 20;-fx-alignment: center_right");
                }}
        );
        root.setStyle("-fx-spacing: 15;-fx-padding: 15");
        Modal modal = new Modal(this.getScene().getRoot(), root);
        modal.setMaskClosable(true);
        ok.setOnAction(event -> {
            String newDirName = textField.getText();
            if (newDirName != null && !newDirName.isBlank()) {
                File newDir = dir.toPath().resolve(newDirName).toFile();
                if (newDir.mkdir()) {
                    TreeItem<File> newFileItem = new TreeItem<>(newDir);
                    dirItem.getChildren().add(newFileItem);
                    selectFileItem(newDir);
                }
            }
            modal.close();
        });
        cancel.setOnAction(event -> {
            modal.close();
        });
        modal.show();
    }

    @FXML
    protected void delFile() {
        List<TreeItem<File>> selectedItems = fileTreeView.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) return;

        StringBuilder tip = new StringBuilder("是否确认删除 ");

        for (TreeItem<File> selectedItem : selectedItems) {
            File value = selectedItem.getValue();
            if (value == null) continue;
            tip.append("'").append(value.getName()).append("', ");
        }
        tip.delete(tip.length() - 2, tip.length());

        Modal modal = new Modal(this.getScene().getRoot(), "删除", tip.toString(), () -> {
            ArrayList<TreeItem<File>> treeItems = new ArrayList<>(selectedItems);
            for (TreeItem<File> treeItem : treeItems) {
                try {
                    if (treeItem.getValue().isFile()) {
                        FileUtils.delete(treeItem.getValue());
                    } else {
                        FileUtils.deleteDirectory(treeItem.getValue());
                    }
                    TreeItem<File> parent = treeItem.getParent();
                    parent.getChildren().remove(treeItem);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, () -> {
        });
        modal.show();
    }

    @FXML
    protected void changeHiddenFileStatus() {
        if (hideHiddenFile.isVisible()) {
            hideHiddenFile.setVisible(false);
            showHiddenFile.setVisible(true);
        } else {
            hideHiddenFile.setVisible(true);
            showHiddenFile.setVisible(false);
        }
        ObservableList<TreeItem<File>> selectedItems = fileTreeView.getSelectionModel().getSelectedItems();
        refresh();

    }
}
