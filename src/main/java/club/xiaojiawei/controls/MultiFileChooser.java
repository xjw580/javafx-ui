package club.xiaojiawei.controls;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.annotations.Nullable;
import club.xiaojiawei.bean.FileChooserFilter;
import club.xiaojiawei.component.MultiFileChooserView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author 肖嘉威
 * @date 2024/12/2 16:07
 */
public class MultiFileChooser {

    private final StringProperty title;

    private final ObjectProperty<File> initialDirectory;

    private MultiFileChooserView multiFileChooserView;

//    private Modal modal;

    private Stage stage;

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public File getInitialDirectory() {
        return initialDirectory.get();
    }

    public ObjectProperty<File> initialDirectoryProperty() {
        return initialDirectory;
    }

    public void setInitialDirectory(File initialDirectory) {
        this.initialDirectory.set(initialDirectory);
    }

    public MultiFileChooser() {
        title = new SimpleStringProperty(this, "title");
        initialDirectory = new SimpleObjectProperty<>(this, "initialDirectory");
    }

    private void loadMultiFileChooserView() {
        if (multiFileChooserView == null) {
            multiFileChooserView = new MultiFileChooserView(this);
        }
    }

    public void showSingleDirDialog(@NotNull Window parentWindows, @Nullable Consumer<@NotNull List<File>> callback) {
        loadMultiFileChooserView();
        showDialog(parentWindows, callback, SelectionMode.SINGLE, List.of(FileChooserFilter.DIR_CHOOSER_FILTER));
    }

    public void showMultiDirDialog(@NotNull Window parentWindows, @Nullable Consumer<@NotNull List<File>> callback) {
        loadMultiFileChooserView();
        showDialog(parentWindows, callback, SelectionMode.MULTIPLE, List.of(FileChooserFilter.DIR_CHOOSER_FILTER));
    }

    public void showSingleFileDialog(@NotNull Window parentWindows, @Nullable Consumer<@NotNull List<File>> callback) {
        loadMultiFileChooserView();
        showDialog(parentWindows, callback, SelectionMode.SINGLE, List.of(FileChooserFilter.FILE_CHOOSER_FILTER));
    }

    public void showMultiFileDialog(@NotNull Window parentWindows, @Nullable Consumer<@NotNull List<File>> callback) {
        loadMultiFileChooserView();
        showDialog(parentWindows, callback, SelectionMode.MULTIPLE, List.of(FileChooserFilter.FILE_CHOOSER_FILTER));
    }

    public void showDialog(
            @NotNull Window parentWindows,
            @Nullable Consumer<@NotNull List<File>> callback,
            @NotNull SelectionMode selectionMode,
            @Nullable List<@NotNull FileChooserFilter> fileFilters
    ) {
        loadMultiFileChooserView();
        multiFileChooserView.setCallback(callback);
        multiFileChooserView.clearFileFilter();
        multiFileChooserView.addFileFilters(fileFilters);
        multiFileChooserView.setSelectionMode(selectionMode);
        multiFileChooserView.refresh();
        if (stage == null) {
//            modal = new Modal(parentWindows.getScene().getRoot(), multiFileChooserView);
//            modal.setEscClosable(false);
            stage = new Stage();
            Scene scene = new Scene(multiFileChooserView);
            stage.setScene(scene);
            JavaFXUI.addjavafxUIStylesheet(scene);
            stage.initOwner(parentWindows);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.titleProperty().bind(titleProperty());
//            stage.initStyle(StageStyle.UTILITY);
            if (parentWindows instanceof Stage parentStage) {
                stage.getIcons().addAll(parentStage.getIcons());
            }
        }
        stage.show();
    }

    public void hideDialog() {
        if (stage != null) {
            stage.close();
        }
    }

    public static void clearHistory() {
        MultiFileChooserView.clearHistory();
    }

}
