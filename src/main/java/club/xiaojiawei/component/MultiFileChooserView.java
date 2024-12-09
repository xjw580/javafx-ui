package club.xiaojiawei.component;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.annotations.Nullable;
import club.xiaojiawei.bean.FileChooserFilter;
import club.xiaojiawei.controls.Modal;
import club.xiaojiawei.controls.MultiFileChooser;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.ProgressModal;
import club.xiaojiawei.controls.ico.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author 肖嘉威
 * @date 2024/12/2 16:09
 */
@Slf4j
public class MultiFileChooserView extends StackPane {

    @FXML
    private Label title;
    @FXML
    private TreeView<File> fileTreeView;
    @FXML
    private ComboBox<File> url;
    @FXML
    private ImageView icon;
    @FXML
    private NotificationManager<Object> notificationManager;
    @FXML
    private InvisibleIco hideHiddenFileIco;
    @FXML
    private VisibleIco showHiddenFileIco;
    @FXML
    private ProgressModal progressModal;
    @FXML
    private FlowPane selectedFilePane;
    @FXML
    private Label selectedCount;

    private final ObjectProperty<SelectionMode> selectionMode;
    /**
     * 文件过滤器
     */
    private final ObservableList<FileChooserFilter> fileFilters = FXCollections.observableArrayList();

    public SelectionMode getSelectionMode() {
        return selectionMode.get();
    }

    public ObjectProperty<SelectionMode> selectionModeProperty() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode.set(selectionMode);
    }

    public void addFileFilters(@NotNull List<@NotNull FileChooserFilter> fileFilters) {
        if (fileFilters != null) {
            this.fileFilters.addAll(fileFilters);
        }
    }

    public void addFileFilter(@NotNull FileChooserFilter fileFilter) {
        fileFilters.add(fileFilter);
    }

    public void removeFileFilter(@NotNull FileChooserFilter fileFilter) {
        fileFilters.remove(fileFilter);
    }

    public void clearFileFilter() {
        fileFilters.clear();
        if (hideHiddenFileIco.isVisible()) {
            fileFilters.addFirst(HIDDEN_FILE_FILTER);
        }
    }

    public MultiFileChooserView(MultiFileChooser multiFileChooser) {
        this(multiFileChooser, null);
    }

    public MultiFileChooserView(MultiFileChooser multiFileChooser, @Nullable Consumer<List<File>> callback) {
        this.callback = callback;
        this.multiFileChooser = multiFileChooser;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            selectionMode = fileTreeView.getSelectionModel().selectionModeProperty();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final FileChooserFilter HIDDEN_FILE_FILTER = new FileChooserFilter(file -> file.getName().isEmpty() || !file.isHidden(), null);

    private File lastSelectedFile = null;

    @Setter
    @Getter
    private Consumer<@NotNull List<File>> callback;

    private final MultiFileChooser multiFileChooser;

    private boolean ctrlDown;

    private static LinkedHashSet<File> historyQueue;

    private final static int MAX_HISTORY_COUNT = 20;

    private boolean isSelecting = false;

    private void afterFXMLLoaded() {
        if (hideHiddenFileIco.isVisible()) {
            fileFilters.addFirst(HIDDEN_FILE_FILTER);
        }
        hideHiddenFileIco.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                fileFilters.addFirst(HIDDEN_FILE_FILTER);
            } else {
                fileFilters.remove(HIDDEN_FILE_FILTER);
            }
        });
        title.textProperty().bind(multiFileChooser.titleProperty());
        url.valueProperty().addListener((observableValue, file, t1) -> {
            if (url.isFocused() && url.isShowing()) {
                selectFileItem(t1, true);
            }
        });
        url.setConverter(new StringConverter<File>() {
            @Override
            public String toString(File file) {
                return file == null ? "" : file.getAbsolutePath();
            }

            @Override
            public File fromString(String s) {
                return s == null ? null : new File(s);
            }
        });
        url.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String text = url.getEditor().getText();
                File file = new File(text);
                TreeItem<File> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && Objects.equals(selectedItem.getValue(), file)) {
                    selectedItem.setExpanded(!selectedItem.isExpanded());
                    fileTreeView.scrollTo(fileTreeView.getRow(selectedItem));
                } else if (!selectFileItem(file, true)) {
                    notificationManager.showWarn("未找到" + file.getAbsolutePath(), 2);
                }
                if (historyQueue == null) {
                    historyQueue = new LinkedHashSet<>(MAX_HISTORY_COUNT);
                }
                historyQueue.remove(file);
                historyQueue.addFirst(file);
                if (historyQueue.size() > MAX_HISTORY_COUNT) {
                    historyQueue.removeLast();
                }
                url.getItems().setAll(historyQueue);
            }
        });
        url.setCellFactory(new Callback<>() {
            @Override
            public ListCell<File> call(ListView<File> fileListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(File file, boolean b) {
                        super.updateItem(file, b);
                        if (b || file == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setText(file.getAbsolutePath());
                            if (file.exists()) {
                                AbstractIco abstractIco = file.isFile() ? new UnknowFileIco() : new DirIco();
                                double scale = 0.8;
                                abstractIco.setScaleX(scale);
                                abstractIco.setScaleY(scale);
                                setGraphic(abstractIco);
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        });
        fileTreeView.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.CONTROL) {
                ctrlDown = true;
            } else if (ctrlDown && code == KeyCode.C) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putFiles(getSelectedFiles());
                clipboard.setContent(content);
            } else if (code == KeyCode.DELETE) {
                delFile();
            } else if (ctrlDown && code == KeyCode.F) {
                url.requestFocus();
            } else if (!ctrlDown && (code.getCode() >= KeyCode.A.getCode() && code.getCode() <= KeyCode.Z.getCode()
                       || code.getCode() >= KeyCode.DIGIT0.getCode() && code.getCode() <= KeyCode.DIGIT9.getCode())
            ) {
                int expandedItemCount = fileTreeView.getExpandedItemCount();
                int selectIndex = 0;
                TreeItem<File> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    selectIndex = fileTreeView.getRow(selectedItem) + 1;
                }
                boolean needLoop = true;
                do {
                    if (selectIndex == -1) {
                        needLoop = false;
                        selectIndex = 0;
                    }
                    for (int i = selectIndex; i < expandedItemCount; i++) {
                        TreeItem<File> treeItem = fileTreeView.getTreeItem(i);
                        String fileName = getFileName(treeItem.getValue());
                        if (fileName.toUpperCase().startsWith(String.valueOf((char) code.getCode()))) {
                            fileTreeView.getSelectionModel().clearSelection();
                            fileTreeView.getSelectionModel().select(treeItem);
                            fileTreeView.scrollTo(i);
                            return;
                        }
                    }
                    selectIndex = -1;
                } while (needLoop);
            } else if (code == KeyCode.ENTER) {
                TreeItem<File> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    selectedItem.setExpanded(!selectedItem.isExpanded());
                }
            }
        });
        fileTreeView.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                ctrlDown = false;
            }
            updateSelectedFile();
        });
        fileTreeView.setOnMouseClicked(event -> {
            updateSelectedFile();
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
                            setFileItemStyle(item);
                        }
                    }

                    private void setFileItemStyle(File item) {
                        AbstractIco fileIcon;
                        if (item.isDirectory()) {
                            fileIcon = new DirIco();
                        } else {
                            fileIcon = new UnknowFileIco();
                        }
                        if (testFileResultFilter(item)) {
                            fileIcon.getStyleClass().add("filter-ico");
                        }
                        fileIcon.getStyleClass().add("file-icon");
                        if (isHideFile(item)) {
                            fileIcon.setColor("gray");
                            setStyle("-fx-text-fill: rgb(113,113,165)");
                        } else {
                            fileIcon.setColor("black");
                            setStyle("-fx-text-fill: black");
                        }
                        double scale = 0.85;
                        fileIcon.setScaleX(scale);
                        fileIcon.setScaleY(scale);
                        setGraphic(fileIcon);
                    }
                };
            }
        });
        fileTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                url.setValue(null);
            } else {
                lastSelectedFile = newValue.getValue();
                url.setValue(newValue.getValue());
            }
        });
    }

    private void updateSelectedFile() {
        selectedFilePane.getChildren().clear();
        List<File> list = getSelectedFiles().stream().filter(this::testFileResultFilter).toList();
        for (int i = 0; i < list.size(); i++) {
            File file = list.get(i);
            selectedFilePane.getChildren().add(new Label(getFileName(file)));
            if (i < list.size() - 1) {
                selectedFilePane.getChildren().add(new Separator(Orientation.VERTICAL));
            }
        }
        selectedCount.setText(list.size() + "");
    }

    private String getFileName(File file) {
        String fileName = file.getName();
        return fileName.isEmpty() ? file.getAbsolutePath() : fileName;
    }

    private boolean isHideFile(@NotNull File file) {
        return file.isHidden() && !file.getName().isBlank();
    }

    private boolean testFileShowFilter(@NotNull File file) {
        boolean filter = true;
        for (FileChooserFilter fileFilter : fileFilters) {
            Predicate<@NotNull File> showFilter = fileFilter.getShowFilter();
            if (showFilter != null && !showFilter.test(file)) {
                filter = false;
                break;
            }
        }
        return filter;
    }

    private boolean testFileResultFilter(@NotNull File file) {
        boolean filter = true;
        for (FileChooserFilter fileFilter : fileFilters) {
            Predicate<@NotNull File> resultFilter = fileFilter.getResultFilter();
            if (resultFilter != null && !resultFilter.test(file)) {
                filter = false;
                break;
            }
        }
        return filter;
    }

    private void invokeCallback(boolean valid) {
        if (callback != null) {
            if (valid) {
                callback.accept(getSelectedFiles().stream().filter(this::testFileResultFilter).toList());
            } else {
                callback.accept(Collections.emptyList());
            }
        }
    }

    @NotNull
    private List<TreeItem<File>> loadFiles(@Nullable File dir, boolean loadChildren) {
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
                if (!testFileShowFilter(file)) continue;

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

    private boolean selectFileItem(File targetFile) {
        return selectFileItem(targetFile, true);
    }

    private boolean selectFileItem(File targetFile, boolean clearPrevSelect) {
        if (isSelecting || targetFile == null || !targetFile.exists()) return false;
        try {
            isSelecting = true;
            Stack<File> filesStack = new Stack<>();
            File parent = targetFile;
            do {
                filesStack.push(parent);
            } while ((parent = parent.getParentFile()) != null);
            File pop;
            TreeItem<File> lastTreeItem = null;
            for (TreeItem<File> child : fileTreeView.getRoot().getChildren()) {
                child.setExpanded(false);
            }
            while (!filesStack.isEmpty() && (pop = filesStack.pop()) != null) {
                TreeItem<File> fileTreeItem = searchFileItem(pop, fileTreeView.getRoot());
                if (fileTreeItem == null) {
                    break;
                } else {
                    fileTreeItem.setExpanded(true);
                    lastTreeItem = fileTreeItem;
                }
            }
            if (lastTreeItem == null) {
                return false;
            } else {
                if (clearPrevSelect) {
                    fileTreeView.getSelectionModel().clearSelection();
                }
                lastTreeItem.setExpanded(false);
                fileTreeView.getSelectionModel().select(lastTreeItem);
                int row = fileTreeView.getRow(lastTreeItem);
                fileTreeView.scrollTo(row);
                return true;
            }
        } finally {
            isSelecting = false;
        }
    }

    @Nullable
    private TreeItem<File> searchFileItem(@NotNull File targetFile, @NotNull TreeItem<File> fileTreeItem) {
        if (Objects.equals(targetFile, fileTreeItem.getValue())) {
            return fileTreeItem;
        }
        ObservableList<TreeItem<File>> files = fileTreeItem.getChildren();
        if (files.isEmpty()) {
            files.setAll(loadFiles(fileTreeItem.getValue(), false));
        }
        for (TreeItem<File> file : files) {
            if (targetFile.toPath().startsWith(file.getValue().toPath())) {
                return searchFileItem(targetFile, file);
            }
        }
        return null;
    }

    @NotNull
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

    @NotNull
    private File getDesktopFile() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        return fsv.getHomeDirectory().getAbsoluteFile();
    }

    @NotNull
    private File getHomeFile() {
        return new File(System.getProperty("user.home"));
    }

    @FXML
    protected void ok() {
        multiFileChooser.hideDialog();
        invokeCallback(true);
    }

    @FXML
    protected void cancel() {
        multiFileChooser.hideDialog();
        invokeCallback(false);
    }

    @FXML
    protected void closePage() {
        multiFileChooser.hideDialog();
        invokeCallback(false);
    }


    @FXML
    protected void homeDir() {
        selectFileItem(getHomeFile());
    }

    @FXML
    protected void desktopDir() {
        selectFileItem(getDesktopFile());
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
            do {
                if (newDirName != null && !newDirName.isBlank()) {
                    File newDir = dir.toPath().resolve(newDirName).toFile();
                    if (newDir.exists()) {
                        notificationManager.showWarn(newDirName + "文件夹已经存在", 1);
                        break;
                    }
                    if (newDir.mkdir()) {
                        TreeItem<File> newFileItem = new TreeItem<>(newDir);
                        dirItem.getChildren().add(newFileItem);
                        selectFileItem(newDir);
                        notificationManager.showSuccess("新建" + newDirName + "成功", 1);
                        break;
                    }
                }
                notificationManager.showError("新建失败", 2);
            } while (false);
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

        StringBuilder tip = new StringBuilder("确认删除 ");

        for (TreeItem<File> selectedItem : selectedItems) {
            File value = selectedItem.getValue();
            if (value == null) continue;
            tip.append("'").append(getFileName(value)).append("', ");
        }
        tip.delete(tip.length() - 2, tip.length());

        Modal modal = new Modal(this.getScene().getRoot(), "删除", tip.toString(), () -> {
            ArrayList<TreeItem<File>> treeItems = new ArrayList<>(selectedItems);
            for (TreeItem<File> treeItem : treeItems) {
                if (treeItem.getValue().getName().isEmpty()) {
                    notificationManager.showWarn("不支持删除磁盘根目录", 3);
                    return;
                }
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
            notificationManager.showSuccess("删除成功", 1);
        }, () -> {
        });
        modal.show();
    }

    @FXML
    protected void changeHiddenFileStatus() {
        if (hideHiddenFileIco.isVisible()) {
            hideHiddenFileIco.setVisible(false);
            showHiddenFileIco.setVisible(true);
        } else {
            hideHiddenFileIco.setVisible(true);
            showHiddenFileIco.setVisible(false);
        }
        refresh();
    }

    public void refresh() {
        File initialDirectory = multiFileChooser.getInitialDirectory();
        DoubleProperty progress = progressModal.show("加载文件中...");
        fileTreeView.getSelectionModel().clearSelection();
        Thread.ofVirtual().start(() -> {
            List<TreeItem<File>> treeItems = loadFiles(null, true);
            Platform.runLater(() -> {
                TreeItem<File> root = fileTreeView.getRoot();
                if (root == null) {
                    root = new TreeItem<>();
                    fileTreeView.setRoot(root);
                    fileTreeView.setShowRoot(false);
                }
                root.setExpanded(true);
                root.getChildren().setAll(treeItems);
                fileTreeView.refresh();
                if (lastSelectedFile == null) {
                    if (initialDirectory != null) {
                        selectFileItem(initialDirectory);
                    }
                } else {
                    selectFileItem(lastSelectedFile);
                }
                updateSelectedFile();
                progressModal.hide(progress);
            });
        });
    }

    public static void clearHistory() {
        historyQueue.clear();
    }
}
