package club.xiaojiawei.component;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.annotations.Nullable;
import club.xiaojiawei.bean.FileChooserFilter;
import club.xiaojiawei.config.JavaFXUIThreadPoolConfig;
import club.xiaojiawei.controls.*;
import club.xiaojiawei.controls.ico.*;
import javafx.application.Platform;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
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

    private final FileTreeLoader fileTreeLoader = new FileTreeLoader();

    private final FileInfoCache fileInfoCache = new FileInfoCache();

    private final Map<TreeItem<File>, NodeLoadState> nodeLoadStates = new IdentityHashMap<>();

    private long refreshGeneration;

    private long navigationGeneration;

    private Long navigationExpansionOwner;

    private boolean updatingUrlFromSelection;

    private boolean internalNavigationSelection;

    private ProgressModal.ProgressContext refreshProgress;

    private static final class NodeLoadState {
        private long generation;
        private boolean loaded;
        private boolean userOwned;
        private boolean notifyOnFailure;
        private FileTreeLoader.Request request;
        private final Set<Long> navigationOwners = new HashSet<>();
        private final List<NodeLoadWaiter> waiters = new ArrayList<>();
    }

    private record NodeLoadWaiter(
            @Nullable Long navigationOwner,
            Consumer<FileTreeLoader.Outcome> consumer
    ) {
    }

    private enum CreateDirectoryOutcome {
        SUCCESS,
        ALREADY_EXISTS,
        IO_FAILURE
    }

    private record DeleteTarget(TreeItem<File> treeItem, File file, boolean directory) {
    }

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
            if (!updatingUrlFromSelection && url.isFocused() && url.isShowing()) {
                updatingUrlFromSelection = true;
                try {
                    url.setValue(file);
                } finally {
                    updatingUrlFromSelection = false;
                }
                navigateToFile(t1, true, true, null, null);
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
                    fileTreeView.requestFocus();
                    updateSearchHistory(file);
                    url.getEditor().setText(text);
                    updateSelectedFile();
                } else {
                    String previousText = url.getConverter().toString(url.getValue());
                    navigateToFile(file, true, true, () -> {
                        url.getEditor().setText(text);
                        updateSelectedFile();
                    }, () -> url.getEditor().setText(previousText));
                }
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
                            setGraphic(null);
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
                content.putFiles(getSelectedFiles().stream().map(it -> new File(it.getAbsolutePath())).toList());
                clipboard.setContent(content);
            } else if (code == KeyCode.DELETE) {
                delFile();
            } else if (ctrlDown && code == KeyCode.F) {
                url.requestFocus();
            } else if (!ctrlDown && (code.getCode() >= KeyCode.A.getCode() && code.getCode() <= KeyCode.Z.getCode() || code.getCode() >= KeyCode.DIGIT0.getCode() && code.getCode() <= KeyCode.DIGIT9.getCode())) {
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
            if (newValue != null && newValue.getValue() != null) {
                if (!internalNavigationSelection) {
                    beginNavigation();
                }
                updatingUrlFromSelection = true;
                try {
                    url.setValue(newValue.getValue());
                } finally {
                    updatingUrlFromSelection = false;
                }
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
                            setGraphic(null);
                        } else {
                            FileInfoCache.FileInfo fileInfo = fileInfoCache.getFileInfo(item);
                            setFileItemStyle(item, fileInfo);
                        }
                    }

                    private final DirIco dirIco = new DirIco();
                    private final UnknowFileIco unknowFileIco = new UnknowFileIco();
                    private final HBox itemRoot;
                    private final Text text = new Text();

                    {
                        HBox space = new HBox();
                        HBox.setHgrow(space, Priority.ALWAYS);
                        itemRoot = new HBox(dirIco, text, space) {{
                            setSpacing(2);
                        }};
                    }

                    private void setFileItemStyle(File item, FileInfoCache.FileInfo fileInfo) {
                        AbstractIco fileIcon;
                        if (fileInfo.isDirectory()) {
                            fileIcon = dirIco;
                        } else {
                            fileIcon = unknowFileIco;
                        }

                        // 如果信息还没加载完成，显示加载状态
                        if (!fileInfo.isLoaded()) {
                            fileIcon.setColor("lightgray");
                            text.setText(fileInfo.getName() + " (加载中...)");
                        } else {
                            // 使用缓存的信息更新UI
                            updateIconStyle(fileIcon, fileInfo, item);
                            text.setText(fileInfo.getName());
                        }

                        // 设置图标
                        if (itemRoot.getChildren().getFirst() instanceof AbstractIco) {
                            itemRoot.getChildren().removeFirst();
                        }
                        itemRoot.getChildren().addFirst(fileIcon);

                        // 处理文件注释
                        handleFileComment(item);

                        setGraphic(itemRoot);
                    }

                    private void updateIconStyle(AbstractIco fileIcon, FileInfoCache.FileInfo fileInfo, File item) {
                        if (!fileIcon.getStyleClass().contains("file-icon")) {
                            fileIcon.getStyleClass().add("file-icon");
                        }

                        if (isSelected() && testFileResultFilter(item)) {
                            fileIcon.getStyleClass().remove("filter-ico");
                            fileIcon.getStyleClass().add("filter-ico");
                        } else {
                            fileIcon.getStyleClass().remove("filter-ico");
                        }

                        if (fileInfo.isHidden()) {
                            fileIcon.setColor("gray");
                            setStyle("-fx-text-fill: rgb(113,113,165)");
                        } else {
                            fileIcon.setColor("black");
                            setStyle("-fx-text-fill: black");
                        }

                        double scale = 0.85;
                        fileIcon.setScaleX(scale);
                        fileIcon.setScaleY(scale);
                    }

                    private void handleFileComment(File item) {
                        if (itemRoot.getChildren().size() > 3) {
                            itemRoot.getChildren().removeLast();
                        }
                        if (fileCommentHandler == null || isUnloadedDisk(item)) {
                            return;
                        }
                        Node fileComment = fileCommentHandler.apply(item);
                        if (fileComment != null) {
                            itemRoot.getChildren().add(fileComment);
                        }
                    }
                };
            }
        });
    }

    public class FileInfoCache {
        private final Map<String, FileInfo> cache = new HashMap<>();

        public static class FileInfo {
            private final boolean isDirectory;
            private final boolean isHidden;
            private final String name;
            private final boolean loaded;

            public FileInfo(boolean isDirectory, boolean isHidden, String name, boolean loaded) {
                this.isDirectory = isDirectory;
                this.isHidden = isHidden;
                this.name = name;
                this.loaded = loaded;
            }

            public boolean isDirectory() {
                return isDirectory;
            }

            public boolean isHidden() {
                return isHidden;
            }

            public String getName() {
                return name;
            }

            public boolean isLoaded() {
                return loaded;
            }
        }

        public FileInfo getFileInfo(File file) {
            String path = file.getAbsolutePath();
            FileInfo fileInfo = cache.get(path);
            if (fileInfo != null) {
                return fileInfo;
            }
            String name = file.getName();
            return new FileInfo(false, false, name.isBlank() ? path : name, false);
        }

        private void put(FileTreeLoader.Entry entry) {
            cache.put(entry.file().getAbsolutePath(),
                    new FileInfo(entry.directory(), entry.hidden(), entry.name(), true));
        }

        private boolean isDirectory(File file) {
            FileInfo fileInfo = cache.get(file.getAbsolutePath());
            return fileInfo != null && fileInfo.isDirectory();
        }

        private void clear() {
            cache.clear();
        }
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

    private Predicate<File> snapshotFileShowFilter() {
        List<Predicate<@NotNull File>> showFilters = new ArrayList<>();
        for (FileChooserFilter fileFilter : fileFilters) {
            Predicate<@NotNull File> showFilter = fileFilter.getShowFilter();
            if (showFilter != null) {
                showFilters.add(showFilter);
            }
        }
        FileChooser.ExtensionFilter selectedType = saveFileType.getValue();
        List<String> extensions = selectedType == null ? List.of() : List.copyOf(selectedType.getExtensions());
        return file -> {
            for (Predicate<File> showFilter : showFilters) {
                if (!showFilter.test(file)) {
                    return false;
                }
            }
            if (!extensions.isEmpty() && file.isFile() && !isDiskFile(file)) {
                String name = file.getName();
                for (String extension : extensions) {
                    if (!Pattern.matches(".*\\." + extension, name)) {
                        return false;
                    }
                }
            }
            return true;
        };
    }

    private boolean isDiskFile(@NotNull File file) {
        return file.getName().isEmpty();
    }

    private boolean isUnloadedDisk(@NotNull File file) {
        if (!isDiskFile(file)) {
            return false;
        }
        TreeItem<File> root = fileTreeView.getRoot();
        TreeItem<File> diskItem = root == null ? null : findChild(root, file);
        NodeLoadState loadState = nodeLoadStates.get(diskItem);
        return loadState == null || !loadState.loaded;
    }

    private boolean testFileResultFilter(@NotNull File file) {
        if (isUnloadedDisk(file)) {
            return false;
        }
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
        updatingUrlFromSelection = true;
        try {
            url.setValue(file);
        } finally {
            updatingUrlFromSelection = false;
        }
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
                            if (!fileInfoCache.isDirectory(file)) {
                                file = file.getParentFile();
                            }
                            File saveFile = new File(file.toPath().resolve(fileName).toString());
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

    private TreeItem<File> createRootTreeItem(File rootFile) {
        return createFileTreeItem(new FileTreeLoader.Entry(
                rootFile, true, false, rootFile.getAbsolutePath()));
    }

    private TreeItem<File> createFileTreeItem(FileTreeLoader.Entry entry) {
        fileInfoCache.put(entry);
        TreeItem<File> treeItem = new TreeItem<>(entry.file());
        NodeLoadState state = new NodeLoadState();
        state.loaded = !entry.directory();
        nodeLoadStates.put(treeItem, state);
        if (entry.directory()) {
            restoreLoadingPlaceholder(treeItem);
            treeItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    Long expansionOwner = navigationExpansionOwner;
                    ensureNodeLoaded(treeItem, refreshGeneration,
                            expansionOwner == null, expansionOwner, null);
                } else {
                    NodeLoadState loadState = nodeLoadStates.get(treeItem);
                    if (loadState != null) {
                        loadState.userOwned = false;
                        loadState.notifyOnFailure = false;
                    }
                }
            });
        }
        return treeItem;
    }

    private void restoreLoadingPlaceholder(TreeItem<File> treeItem) {
        treeItem.getChildren().setAll(new TreeItem<>());
    }

    private void ensureNodeLoaded(
            TreeItem<File> treeItem,
            long expectedRefreshGeneration,
            boolean notifyOnFailure,
            @Nullable Long navigationOwner,
            @Nullable Consumer<FileTreeLoader.Outcome> waiter
    ) {
        if (expectedRefreshGeneration != refreshGeneration) {
            return;
        }
        NodeLoadState state = nodeLoadStates.get(treeItem);
        if (state == null) {
            return;
        }
        if (state.loaded) {
            if (waiter != null) {
                waiter.accept(FileTreeLoader.Outcome.SUCCESS);
            }
            return;
        }
        if (navigationOwner == null) {
            state.userOwned = true;
        } else {
            state.navigationOwners.add(navigationOwner);
        }
        if (waiter != null) {
            state.waiters.add(new NodeLoadWaiter(navigationOwner, waiter));
        }
        state.notifyOnFailure |= notifyOnFailure;
        if (state.request != null) {
            return;
        }

        long nodeGeneration = ++state.generation;
        FileTreeLoader.Request request = fileTreeLoader.load(treeItem.getValue(), snapshotFileShowFilter());
        state.request = request;
        request.completion().whenComplete((result, error) -> Platform.runLater(() ->
                applyNodeLoadResult(treeItem, state, request, nodeGeneration,
                        expectedRefreshGeneration, result, error)));
    }

    private void applyNodeLoadResult(
            TreeItem<File> treeItem,
            NodeLoadState state,
            FileTreeLoader.Request request,
            long nodeGeneration,
            long expectedRefreshGeneration,
            @Nullable FileTreeLoader.Result result,
            @Nullable Throwable error
    ) {
        if (expectedRefreshGeneration != refreshGeneration
            || nodeLoadStates.get(treeItem) != state
            || state.request != request
            || state.generation != nodeGeneration
            || !request.token().isValid()) {
            return;
        }

        state.request = null;
        boolean hasLiveOwner = state.userOwned || state.navigationOwners.contains(navigationGeneration);
        List<Consumer<FileTreeLoader.Outcome>> liveWaiters = state.waiters.stream()
                .filter(waiter -> waiter.navigationOwner() == null
                                  || waiter.navigationOwner() == navigationGeneration)
                .map(NodeLoadWaiter::consumer)
                .toList();
        state.userOwned = false;
        state.navigationOwners.clear();
        state.waiters.clear();
        if (!hasLiveOwner) {
            state.notifyOnFailure = false;
            return;
        }

        FileTreeLoader.Outcome outcome = error == null && result != null
                ? result.outcome()
                : FileTreeLoader.Outcome.IO_FAILURE;
        if (outcome == FileTreeLoader.Outcome.SUCCESS) {
            state.loaded = true;
            state.notifyOnFailure = false;
            treeItem.getChildren().setAll(result.entries().stream().map(this::createFileTreeItem).toList());
        } else {
            state.loaded = false;
            boolean shouldNotifyNodeFailure = state.notifyOnFailure && liveWaiters.isEmpty();
            restoreLoadingPlaceholder(treeItem);
            treeItem.setExpanded(false);
            if (shouldNotifyNodeFailure) {
                showDirectoryLoadFailure(treeItem.getValue(), outcome);
            }
            state.notifyOnFailure = false;
        }

        liveWaiters.forEach(waiter -> waiter.accept(outcome));
        fileTreeView.refresh();
    }

    private void showDirectoryLoadFailure(File directory, FileTreeLoader.Outcome outcome) {
        if (outcome == FileTreeLoader.Outcome.TIMEOUT) {
            notificationManager.showWarn("读取目录超时，可重新展开重试：" + directory.getAbsolutePath(), 3);
        } else {
            notificationManager.showWarn("无法读取目录，可重新展开重试：" + directory.getAbsolutePath(), 3);
        }
    }

    private void invalidateNodeLoads() {
        for (NodeLoadState state : nodeLoadStates.values()) {
            state.generation++;
            if (state.request != null) {
                state.request.cancel();
            }
            state.waiters.clear();
            state.navigationOwners.clear();
        }
        nodeLoadStates.clear();
    }

    private long beginNavigation() {
        long generation = ++navigationGeneration;
        for (NodeLoadState state : nodeLoadStates.values()) {
            state.navigationOwners.removeIf(owner -> owner != generation);
            state.waiters.removeIf(waiter ->
                    waiter.navigationOwner() != null && waiter.navigationOwner() != generation);
        }
        return generation;
    }

    private void resetNodeForReload(TreeItem<File> treeItem) {
        NodeLoadState state = nodeLoadStates.get(treeItem);
        if (state == null) {
            return;
        }
        state.generation++;
        if (state.request != null) {
            state.request.cancel();
        }
        state.request = null;
        state.loaded = false;
        state.userOwned = false;
        state.notifyOnFailure = false;
        state.waiters.clear();
        state.navigationOwners.clear();
        treeItem.setExpanded(false);
        restoreLoadingPlaceholder(treeItem);
    }

    private void navigateToFile(
            @Nullable File targetFile,
            boolean clearPreviousSelection,
            boolean updateHistory,
            @Nullable Runnable successHandler,
            @Nullable Runnable failureHandler
    ) {
        long navigationToken = beginNavigation();
        navigateToFile(targetFile, clearPreviousSelection, updateHistory,
                successHandler, failureHandler, navigationToken, refreshGeneration);
    }

    private void navigateToFile(
            @Nullable File targetFile,
            boolean clearPreviousSelection,
            boolean updateHistory,
            @Nullable Runnable successHandler,
            @Nullable Runnable failureHandler,
            long navigationToken,
            long expectedRefreshGeneration
    ) {
        if (targetFile == null
            || navigationToken != navigationGeneration
            || expectedRefreshGeneration != refreshGeneration) {
            if (failureHandler != null) {
                failureHandler.run();
            }
            return;
        }

        List<File> hierarchy = buildPathHierarchy(targetFile);
        TreeItem<File> root = fileTreeView.getRoot();
        if (hierarchy.isEmpty() || root == null) {
            failNavigation(targetFile, null, failureHandler, navigationToken, expectedRefreshGeneration);
            return;
        }
        internalNavigationSelection = true;
        try {
            for (TreeItem<File> child : root.getChildren()) {
                child.setExpanded(false);
            }
        } finally {
            internalNavigationSelection = false;
        }
        TreeItem<File> rootItem = findChild(root, hierarchy.getFirst());
        if (rootItem == null) {
            failNavigation(targetFile, null, failureHandler, navigationToken, expectedRefreshGeneration);
            return;
        }
        navigatePathStep(hierarchy, 1, rootItem, targetFile, clearPreviousSelection, updateHistory,
                successHandler, failureHandler, navigationToken, expectedRefreshGeneration);
    }

    private void navigatePathStep(
            List<File> hierarchy,
            int nextIndex,
            TreeItem<File> currentItem,
            File requestedFile,
            boolean clearPreviousSelection,
            boolean updateHistory,
            @Nullable Runnable successHandler,
            @Nullable Runnable failureHandler,
            long navigationToken,
            long expectedRefreshGeneration
    ) {
        if (navigationToken != navigationGeneration || expectedRefreshGeneration != refreshGeneration) {
            return;
        }
        if (nextIndex >= hierarchy.size()) {
            if (!fileInfoCache.isDirectory(currentItem.getValue())) {
                completeNavigation(currentItem, clearPreviousSelection, updateHistory, successHandler);
                return;
            }
            ensureNodeLoaded(currentItem, expectedRefreshGeneration, false, navigationToken, outcome -> {
                if (navigationToken != navigationGeneration
                    || expectedRefreshGeneration != refreshGeneration) {
                    return;
                }
                if (outcome == FileTreeLoader.Outcome.SUCCESS) {
                    completeNavigation(currentItem, clearPreviousSelection, updateHistory, successHandler);
                } else {
                    failNavigation(requestedFile, outcome, failureHandler,
                            navigationToken, expectedRefreshGeneration);
                }
            });
            return;
        }

        Long previousExpansionOwner = navigationExpansionOwner;
        navigationExpansionOwner = navigationToken;
        try {
            currentItem.setExpanded(true);
        } finally {
            navigationExpansionOwner = previousExpansionOwner;
        }
        ensureNodeLoaded(currentItem, expectedRefreshGeneration, false, navigationToken, outcome -> {
            if (navigationToken != navigationGeneration || expectedRefreshGeneration != refreshGeneration) {
                return;
            }
            if (outcome != FileTreeLoader.Outcome.SUCCESS) {
                failNavigation(requestedFile, outcome, failureHandler,
                        navigationToken, expectedRefreshGeneration);
                return;
            }
            TreeItem<File> nextItem = findChild(currentItem, hierarchy.get(nextIndex));
            if (nextItem == null) {
                failNavigation(requestedFile, null, failureHandler,
                        navigationToken, expectedRefreshGeneration);
                return;
            }
            navigatePathStep(hierarchy, nextIndex + 1, nextItem, requestedFile,
                    clearPreviousSelection, updateHistory, successHandler, failureHandler,
                    navigationToken, expectedRefreshGeneration);
        });
    }

    private void completeNavigation(
            TreeItem<File> targetItem,
            boolean clearPreviousSelection,
            boolean updateHistory,
            @Nullable Runnable successHandler
    ) {
        internalNavigationSelection = true;
        try {
            if (clearPreviousSelection) {
                fileTreeView.getSelectionModel().clearSelection();
            }
            fileTreeView.getSelectionModel().select(targetItem);
        } finally {
            internalNavigationSelection = false;
        }
        targetItem.setExpanded(false);
        scrollTo(fileTreeView.getRow(targetItem));
        fileTreeView.requestFocus();
        if (updateHistory) {
            updateSearchHistory(targetItem.getValue());
        }
        updateSelectedFile();
        if (successHandler != null) {
            successHandler.run();
        }
    }

    private void failNavigation(
            File targetFile,
            @Nullable FileTreeLoader.Outcome outcome,
            @Nullable Runnable failureHandler,
            long navigationToken,
            long expectedRefreshGeneration
    ) {
        if (navigationToken != navigationGeneration || expectedRefreshGeneration != refreshGeneration) {
            return;
        }
        if (outcome == FileTreeLoader.Outcome.TIMEOUT) {
            notificationManager.showWarn("访问超时：" + targetFile.getAbsolutePath(), 3);
        } else if (outcome == FileTreeLoader.Outcome.IO_FAILURE) {
            notificationManager.showWarn("无法访问：" + targetFile.getAbsolutePath(), 3);
        } else {
            notificationManager.showWarn("未找到" + targetFile.getAbsolutePath(), 2);
        }
        if (failureHandler != null) {
            failureHandler.run();
        }
    }

    private List<File> buildPathHierarchy(File targetFile) {
        Path targetPath = targetFile.toPath().toAbsolutePath().normalize();
        Path root = targetPath.getRoot();
        if (root == null) {
            return List.of();
        }
        List<File> hierarchy = new ArrayList<>();
        Path current = root;
        hierarchy.add(current.toFile());
        for (Path name : targetPath) {
            current = current.resolve(name);
            hierarchy.add(current.toFile());
        }
        return hierarchy;
    }

    @Nullable
    private TreeItem<File> findChild(TreeItem<File> parent, File targetFile) {
        String targetPath = normalizedPath(targetFile);
        for (TreeItem<File> child : parent.getChildren()) {
            if (child.getValue() != null && pathsEqual(normalizedPath(child.getValue()), targetPath)) {
                return child;
            }
        }
        return null;
    }

    private String normalizedPath(File file) {
        return file.toPath().toAbsolutePath().normalize().toString();
    }

    private boolean pathsEqual(String first, String second) {
        return File.separatorChar == '\\' ? first.equalsIgnoreCase(second) : first.equals(second);
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

    @NotNull
    private File getDownloadsFile() throws IOException {
        Process process = new ProcessBuilder(
                "powershell.exe", "-NoProfile", "-NonInteractive", "-Command",
                "$ErrorActionPreference = 'Stop'; "
                + "[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new(); "
                + "(New-Object -ComObject Shell.Application).NameSpace('shell:Downloads').Self.Path")
                .redirectErrorStream(true)
                .start();
        try {
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                throw new IOException("读取下载目录超时");
            }
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
            if (process.exitValue() != 0 || output.isBlank()) {
                throw new IOException("读取下载目录失败：" + output);
            }
            return new File(output);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("读取下载目录被中断", e);
        } finally {
            process.destroyForcibly();
        }
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
        navigateToFile(getHomeFile(), true, true, null, null);
    }

    @FXML
    protected void desktopDir() {
        long navigationToken = beginNavigation();
        long expectedRefreshGeneration = refreshGeneration;
        JavaFXUIThreadPoolConfig.V_THREAD_POOL.submit(() -> {
            File desktopFile;
            try {
                desktopFile = getDesktopFile();
            } catch (RuntimeException e) {
                log.error("读取桌面目录失败", e);
                desktopFile = null;
            }
            File result = desktopFile;
            Platform.runLater(() -> {
                if (navigationToken != navigationGeneration
                    || expectedRefreshGeneration != refreshGeneration) {
                    return;
                }
                if (result == null) {
                    notificationManager.showWarn("无法读取桌面目录", 2);
                } else {
                    navigateToFile(result, true, true, null, null,
                            navigationToken, expectedRefreshGeneration);
                }
            });
        });
    }

    @FXML
    protected void downloadsDir() {
        long navigationToken = beginNavigation();
        long expectedRefreshGeneration = refreshGeneration;
        JavaFXUIThreadPoolConfig.V_THREAD_POOL.submit(() -> {
            File downloadsFile;
            try {
                downloadsFile = getDownloadsFile();
            } catch (IOException | RuntimeException e) {
                log.error("读取下载目录失败", e);
                downloadsFile = null;
            }
            File result = downloadsFile;
            Platform.runLater(() -> {
                if (navigationToken != navigationGeneration
                    || expectedRefreshGeneration != refreshGeneration) {
                    return;
                }
                if (result == null) {
                    notificationManager.showWarn("无法读取下载目录", 2);
                } else {
                    navigateToFile(result, true, true, null, null,
                            navigationToken, expectedRefreshGeneration);
                }
            });
        });
    }

    @FXML
    protected void newDir() {
        TreeItem<File> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;
        File file = selectedItem.getValue();
        if (file == null) return;

        File dir;
        TreeItem<File> dirItem;
        if (fileInfoCache.isDirectory(file)) {
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

        VBox root = new VBox(new Label("输入新文件夹名:"), textField, new HBox(ok, cancel) {{
            setStyle("-fx-spacing: 20;-fx-alignment: center_right");
        }});
        root.setStyle("-fx-spacing: 15;-fx-padding: 15");
        Modal modal = new Modal(this.getScene().getRoot(), root);
        modal.setMaskClosable(true);
        ok.setOnAction(event -> {
            String newDirName = textField.getText();
            if (newDirName == null || newDirName.isBlank()) {
                notificationManager.showError("新建失败", 2);
                modal.close();
                return;
            }
            File newDir = dir.toPath().resolve(newDirName).toFile();
            long expectedRefreshGeneration = refreshGeneration;
            JavaFXUIThreadPoolConfig.V_THREAD_POOL.submit(() -> {
                CreateDirectoryOutcome outcome;
                try {
                    Files.createDirectory(newDir.toPath());
                    outcome = CreateDirectoryOutcome.SUCCESS;
                } catch (FileAlreadyExistsException e) {
                    outcome = CreateDirectoryOutcome.ALREADY_EXISTS;
                } catch (IOException | SecurityException e) {
                    log.error("新建目录失败: {}", newDir, e);
                    outcome = CreateDirectoryOutcome.IO_FAILURE;
                }
                CreateDirectoryOutcome result = outcome;
                Platform.runLater(() -> {
                    if (result == CreateDirectoryOutcome.ALREADY_EXISTS) {
                        notificationManager.showWarn(newDirName + "文件夹已经存在", 1);
                    } else if (result == CreateDirectoryOutcome.IO_FAILURE) {
                        notificationManager.showError("新建失败", 2);
                    } else {
                        if (expectedRefreshGeneration == refreshGeneration
                            && nodeLoadStates.containsKey(dirItem)) {
                            resetNodeForReload(dirItem);
                            navigateToFile(newDir, true, true, null, null);
                        }
                        notificationManager.showSuccess("新建" + newDirName + "成功", 1);
                    }
                });
            });
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
            List<DeleteTarget> targets = new ArrayList<>();
            for (TreeItem<File> treeItem : new ArrayList<>(selectedItems)) {
                if (treeItem.getValue().getName().isEmpty()) {
                    notificationManager.showWarn("不支持删除磁盘根目录", 3);
                    return;
                }
                targets.add(new DeleteTarget(treeItem, treeItem.getValue(),
                        fileInfoCache.isDirectory(treeItem.getValue())));
            }
            JavaFXUIThreadPoolConfig.V_THREAD_POOL.submit(() -> {
                List<DeleteTarget> deletedTargets = new ArrayList<>();
                IOException failure = null;
                try {
                    for (DeleteTarget target : targets) {
                        if (target.directory()) {
                            FileUtils.deleteDirectory(target.file());
                        } else {
                            FileUtils.delete(target.file());
                        }
                        deletedTargets.add(target);
                    }
                } catch (IOException e) {
                    failure = e;
                }
                IOException resultFailure = failure;
                Platform.runLater(() -> {
                    for (DeleteTarget target : deletedTargets) {
                        TreeItem<File> parent = target.treeItem().getParent();
                        if (parent != null) {
                            parent.getChildren().remove(target.treeItem());
                        }
                    }
                    if (resultFailure == null) {
                        notificationManager.showSuccess("删除成功", 1);
                    } else {
                        log.error("删除文件失败", resultFailure);
                        notificationManager.showError("删除失败", 2);
                    }
                });
            });
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
        File recoveryTarget;
        if (lastSelectedFile != null) {
            recoveryTarget = lastSelectedFile;
        } else if (initialDirectory != null) {
            recoveryTarget = initialDirectory;
        } else if (historySearchQueue != null && !historySearchQueue.isEmpty()) {
            recoveryTarget = historySearchQueue.getFirst();
        } else {
            recoveryTarget = null;
        }

        long currentRefreshGeneration = ++refreshGeneration;
        navigationGeneration++;
        invalidateNodeLoads();
        if (refreshProgress != null) {
            refreshProgress.finish();
        }
        ProgressModal.ProgressContext progress = progressModal.show("加载文件中...");
        refreshProgress = progress;
        fileTreeView.getSelectionModel().clearSelection();
        boolean includeNetworkDrive = showNetIco.isVisible();
        JavaFXUIThreadPoolConfig.V_THREAD_POOL.submit(() -> {
            List<File> roots = new ArrayList<>();
            Throwable error = null;
            try {
                File[] rootFiles = File.listRoots();
                if (rootFiles != null) {
                    for (File rootFile : rootFiles) {
                        if (includeNetworkDrive
                            || !isNetworkDrive(rootFile.getAbsolutePath().replace(File.separator, ""))) {
                            roots.add(rootFile);
                        }
                    }
                }
            } catch (RuntimeException e) {
                error = e;
                log.error("枚举根目录失败", e);
            }
            Throwable resultError = error;
            Platform.runLater(() -> {
                if (currentRefreshGeneration != refreshGeneration) {
                    return;
                }
                if (refreshProgress == progress) {
                    progress.finish();
                    refreshProgress = null;
                }
                if (resultError != null) {
                    notificationManager.showWarn("无法刷新磁盘列表", 2);
                    return;
                }

                fileInfoCache.clear();
                invalidateNodeLoads();
                TreeItem<File> root = fileTreeView.getRoot();
                if (root == null) {
                    root = new TreeItem<>();
                    fileTreeView.setRoot(root);
                    fileTreeView.setShowRoot(false);
                }
                root.setExpanded(true);
                root.getChildren().setAll(roots.stream().map(this::createRootTreeItem).toList());
                fileTreeView.refresh();
                updateSelectedFile();
                if (recoveryTarget != null) {
                    navigateToFile(recoveryTarget, true, true, null, null);
                }
            });
        });
    }

    public static void clearHistory() {
        historySearchQueue.clear();
        historySaveQueue.clear();
    }
}
