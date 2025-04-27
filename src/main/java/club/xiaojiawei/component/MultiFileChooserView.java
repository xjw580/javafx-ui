package club.xiaojiawei.component;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.annotations.Nullable;
import club.xiaojiawei.bean.FileChooserFilter;
import club.xiaojiawei.controls.*;
import club.xiaojiawei.controls.ico.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author 肖嘉威
 * @date 2024/12/2 16:09
 */
@Slf4j
public class MultiFileChooserView extends StackPane {

    @FXML
    private Label title;
    @FXML
    private VisibleTreeView<File> fileTreeView;
    @FXML
    private ComboBox<File> url;
    @FXML
    private ImageView icon;
    @FXML
    private NotificationManager<Object> notificationManager;
    @FXML
    private VisibleIco hideHiddenFileIco;
    @FXML
    private VisibleIco showHiddenFileIco;
    @FXML
    private NetIco hideNetIco;
    @FXML
    private NetIco showNetIco;
    @FXML
    private ProgressModal progressModal;
    @FXML
    private FlowPane selectedFilePane;
    @FXML
    private Label selectedCount;
    @FXML
    private ComboBox<String> saveFileName;
    @FXML
    private ComboBox<FileChooser.ExtensionFilter> saveFileType;
    @FXML
    private Pane chooseFilePane;
    @FXML
    private Pane saveFilePane;

    /**
     * 文件注释处理器
     */
    @Getter
    private Function<@NotNull File, @Nullable Node> fileCommentHandler;

    private final ObservableList<FileChooser.ExtensionFilter> fileTypeFilter = FXCollections.observableArrayList();

    public ObservableList<FileChooser.ExtensionFilter> getFileTypeFilter() {
        return fileTypeFilter;
    }

    public void setFileTypeFilter(List<FileChooser.ExtensionFilter> fileTypeFilter) {
        if (fileTypeFilter == null) {
            this.fileTypeFilter.clear();
        } else {
            this.fileTypeFilter.setAll(fileTypeFilter);
        }
    }

    public ObservableList<FileChooser.ExtensionFilter> fileTypeFilter() {
        return fileTypeFilter;
    }

    private final ObjectProperty<SelectionMode> selectionMode;
    /**
     * 文件过滤器
     */
    private final ObservableList<FileChooserFilter> fileFilters = FXCollections.observableArrayList();

    public void setFileCommentHandler(Function<File, Node> fileCommentHandler) {
        this.fileCommentHandler = fileCommentHandler;
        updateFileCellFactory();
    }

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
        this(multiFileChooser, null, null);
    }

    public MultiFileChooserView(MultiFileChooser multiFileChooser, @Nullable Consumer<List<File>> selectedCallback, @Nullable Function<File, Node> fileCommentHandler) {
        this.fileCommentHandler = fileCommentHandler;
        this.selectedCallback = selectedCallback;
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
    private Consumer<@NotNull List<File>> selectedCallback;

    private final MultiFileChooser multiFileChooser;

    private boolean ctrlDown;

    private static LinkedHashSet<File> historySearchQueue;

    private static LinkedHashSet<String> historySaveQueue;

    private final static int MAX_HISTORY_COUNT = 20;

    private boolean isSelecting = false;

    private String formatFileTypeList(List<String> fileTypes) {
        StringBuilder builder = new StringBuilder();
        for (String fileType : fileTypes) {
            builder.append("*.").append(fileType).append(",");
        }
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    private String formatFileType(FileChooser.ExtensionFilter extensionFilter) {
        return extensionFilter == null ? "" : String.format("%s (%s)", extensionFilter.getDescription(), formatFileTypeList(extensionFilter.getExtensions()));
    }

    private void afterFXMLLoaded() {
        saveFileName.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                ok();
            }
        });
        fileTypeFilter.addListener((ListChangeListener<FileChooser.ExtensionFilter>) change -> {
            if (fileTypeFilter.isEmpty()) {
                saveFilePane.setVisible(false);
                saveFilePane.setManaged(false);
                chooseFilePane.setVisible(true);
                chooseFilePane.setManaged(true);
            } else {
                chooseFilePane.setVisible(false);
                chooseFilePane.setManaged(false);
                saveFilePane.setVisible(true);
                saveFilePane.setManaged(true);
            }
            saveFileType.getItems().clear();
            saveFileType.getItems().addAll(fileTypeFilter);
            if (saveFileType.getValue() == null && !saveFileType.getItems().isEmpty()) {
                saveFileType.getSelectionModel().selectFirst();
            }
        });
        saveFileType.setConverter(new StringConverter<>() {

            @Override
            public String toString(FileChooser.ExtensionFilter extensionFilter) {
                return formatFileType(extensionFilter);
            }

            @Override
            public FileChooser.ExtensionFilter fromString(String s) {
                return null;
            }
        });
        if (historySearchQueue != null) {
            url.getItems().setAll(historySearchQueue);
        }
        if (historySaveQueue != null) {
            saveFileName.getItems().setAll(historySaveQueue);
        }
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
                if (text == null || text.isBlank()) return;
                File file = new File(text);
                TreeItem<File> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null && Objects.equals(selectedItem.getValue(), file)) {
                    selectedItem.setExpanded(!selectedItem.isExpanded());
                    scrollTo(fileTreeView.getRow(selectedItem));
                } else if (!selectFileItem(file, true)) {
                    notificationManager.showWarn("未找到" + file.getAbsolutePath(), 2);
                }
                updateSearchHistory(file);
                url.getEditor().setText(text);
                updateSelectedFile();
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
                            scrollTo(i);
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
        updateFileCellFactory();
        fileTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                url.setValue(null);
            } else {
                url.setValue(newValue.getValue());
                if (testFileSaveFilter(newValue.getValue())) {
                    saveFileName.setValue(newValue.getValue().getName());
                }
            }
        });
    }

    private void updateFileCellFactory() {
        fileTreeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<File> call(TreeView<File> param) {
                return new TreeCell<>() {

                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
//                            setText(null);
                            setGraphic(null);
                        } else {
                            String name = item.getName();
                            if (name.isBlank()) {
                                name = item.getAbsolutePath();
                            }
//                            setText(name);
                            setFileItemStyle(item, name);
                        }
                    }

                    private void setFileItemStyle(File item, String name) {
                        AbstractIco fileIcon;
                        if (item.isDirectory()) {
                            fileIcon = new DirIco();
                        } else {
                            fileIcon = new UnknowFileIco();
                        }
                        fileIcon.getStyleClass().add("file-icon");
                        if (isSelected() && testFileResultFilter(item)) {
                            fileIcon.getStyleClass().add("filter-ico");
                        }
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
                        HBox space = new HBox();
                        HBox.setHgrow(space, Priority.ALWAYS);
                        var itemRoot = new HBox(fileIcon, new Text(name), space) {{
                            setSpacing(2);
                        }};
                        if (fileCommentHandler != null) {
                            Node fileComment = fileCommentHandler.apply(item);
                            if (fileComment != null) {
                                itemRoot.getChildren().add(fileComment);
                            }
                        }
                        setGraphic(itemRoot);
                    }
                };
            }
        });
    }

    private void scrollTo(int index) {
        if (!fileTreeView.isIndexVisible(index)) {
            fileTreeView.scrollTo(Math.max(0, index - 1));
        }
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

    private boolean testFileSaveFilter(@NotNull File file) {
        boolean filter = true;
        FileChooser.ExtensionFilter value = saveFileType.getValue();
        if (value != null) {
            String name = file.getName();
            for (String extension : value.getExtensions()) {
                extension = ".*\\." + extension;
                if (!Pattern.matches(extension, name)) {
                    filter = false;
                    break;
                }
            }
        }
        return filter;
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
        if (filter && file.isFile() && !isDiskFile(file)) {
            filter = testFileSaveFilter(file);
        }
        return filter;
    }

    private boolean isDiskFile(@NotNull File file) {
        return file.getName().isEmpty();
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
        if (filter) {
            filter = testFileSaveFilter(file);
        }
        return filter;
    }

    private void updateSaveHistory(String fileName) {
        if (historySaveQueue == null) {
            historySaveQueue = new LinkedHashSet<>(MAX_HISTORY_COUNT);
        }
        historySaveQueue.remove(fileName);
        historySaveQueue.addFirst(fileName);
        if (historySaveQueue.size() > MAX_HISTORY_COUNT) {
            historySaveQueue.removeLast();
        }
        saveFileName.getItems().setAll(historySaveQueue);
    }

    private void updateSearchHistory(File file) {
        if (historySearchQueue == null) {
            historySearchQueue = new LinkedHashSet<>(MAX_HISTORY_COUNT);
        }
        historySearchQueue.remove(file);
        historySearchQueue.addFirst(file);
        if (historySearchQueue.size() > MAX_HISTORY_COUNT) {
            historySearchQueue.removeLast();
        }
        lastSelectedFile = file;
        url.getItems().setAll(historySearchQueue);
    }

    private void invokeCallback(boolean valid, Consumer<Boolean> callback) {
        if (selectedCallback != null) {
            if (valid) {
                List<File> res = Collections.emptyList();
                if (saveFileType.getValue() != null) {
                    if (!saveFileType.getValue().getExtensions().isEmpty()) {
                        String fileName = saveFileName.getValue();
                        List<String> suffix = saveFileType.getValue().getExtensions();
                        if (fileName != null && !fileName.isBlank()) {
                            if (!fileName.endsWith(suffix.getFirst())) {
                                fileName += "." + suffix.getFirst();
                            }
                            File file = url.getValue();
                            if (file.isFile()) {
                                file = file.getParentFile();
                            }
                            File saveFile = file.toPath().resolve(fileName).toFile();
                            res = List.of(saveFile);
                            updateSaveHistory(fileName);
                        }
                    }
                } else {
                    res = getSelectedFiles().stream().filter(this::testFileResultFilter).toList();
                }
                if (res.isEmpty()) {
                    callback.accept(false);
                } else {
                    File urlFile = url.getValue();
                    if (urlFile != null) {
                        updateSearchHistory(urlFile);
                    }
                    callback.accept(true);
                }
                selectedCallback.accept(res);
            } else {
                if (callback != null) {
                    callback.accept(true);
                }
            }
        }
    }

    public static boolean isNetworkDrive(String driveLetter) {
        try {
            // 规范化盘符格式，例如 "D:" 或 "D"
            String normalizedDrive = driveLetter.endsWith(":") ? driveLetter : driveLetter + ":";

            // 使用 PowerShell 获取驱动器类型
            String command = "powershell -Command \"(Get-PSDrive -Name '" + normalizedDrive.charAt(0) + "').Provider.Name -eq 'FileSystem' -and (Get-WmiObject -Class Win32_LogicalDisk -Filter \\\"DeviceID='" + normalizedDrive + "'\\\").DriveType -eq 4\"";
            Process process = Runtime.getRuntime().exec(command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));) {

                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                process.waitFor();

                return "True".equals(output.toString().trim());
            }
        } catch (Exception e) {
            System.out.println("无法检查盘符 " + driveLetter + ": " + e.getMessage());
            return false;
        }
    }

    @NotNull
    private List<TreeItem<File>> loadFiles(@Nullable File dir, boolean loadChildren) {
        File[] files = null;
        if (dir == null) {
            if (showNetIco.isVisible()) {
                files = File.listRoots();
            } else {
                File[] tempFiles = File.listRoots();
                ArrayList<File> noRemoteDrive = new ArrayList<>();
                if (tempFiles != null) {
                    for (File file : tempFiles) {
                        if (!isNetworkDrive(file.getAbsolutePath().replace(File.separator, ""))) {
                            noRemoteDrive.add(file);
                        }
                    }
                }
                files = noRemoteDrive.toArray(new File[0]);
            }
        } else {
            File[] tempFiles = dir.listFiles();
            if (tempFiles != null) {
                files = Arrays.stream(tempFiles).filter(this::testFileShowFilter).toArray(File[]::new);
            }
        }
        if (files == null) {
            return Collections.emptyList();
        } else {
            List<TreeItem<File>> result = new ArrayList<>();
//            为了性能考量
            if (!loadChildren && files.length > 0) {
                result.add(new TreeItem<>());
            } else {
                for (File file : files) {
                    TreeItem<File> treeItem = new TreeItem<>(file);
                    treeItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            treeItem.getChildren().setAll(loadFiles(file, true));
                        }
                    });
                    treeItem.getChildren().setAll(loadFiles(file, false));
                    result.add(treeItem);
                }
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
                scrollTo(fileTreeView.getRow(lastTreeItem));
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
        invokeCallback(true, res -> {
            if (res) {
                multiFileChooser.hideDialog();
            }
        });
    }

    @FXML
    protected void cancel() {
        invokeCallback(false, res -> {
            if (res) {
                multiFileChooser.hideDialog();
            }
        });
    }

    @FXML
    protected void closePage() {
        invokeCallback(false, res -> {
            if (res) {
                multiFileChooser.hideDialog();
            }
        });
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

    @FXML
    protected void changeRemoveDriverStatus() {
        if (hideNetIco.isVisible()) {
            hideNetIco.setVisible(false);
            showNetIco.setVisible(true);
        } else {
            hideNetIco.setVisible(true);
            showNetIco.setVisible(false);
        }
        refresh();
    }

    public void refresh() {
        File initialDirectory = multiFileChooser.getInitialDirectory();
        DoubleProperty progress = progressModal.show("加载文件中...");
        fileTreeView.getSelectionModel().clearSelection();
        new Thread(() -> {
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
                    if (initialDirectory == null) {
                        if (historySearchQueue != null && !historySearchQueue.isEmpty()) {
                            selectFileItem(historySearchQueue.getFirst());
                        }
                    }else {
                        selectFileItem(initialDirectory);
                    }
                } else {
                    selectFileItem(lastSelectedFile);
                }
                updateSelectedFile();
                progressModal.hide(progress);
            });
        }).start();
    }

    public static void clearHistory() {
        historySearchQueue.clear();
        historySaveQueue.clear();
    }
}
