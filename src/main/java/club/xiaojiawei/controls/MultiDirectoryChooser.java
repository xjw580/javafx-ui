package club.xiaojiawei.controls;

import club.xiaojiawei.component.MultiDirectoryChooserView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Window;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author 肖嘉威
 * @date 2024/12/2 16:07
 */
public class MultiDirectoryChooser {

    private final StringProperty title;

    private final ObjectProperty<File> initialDirectory;

    private MultiDirectoryChooserView multiDirectoryChooserView;

    private Modal modal;

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

    public MultiDirectoryChooser() {
        title = new SimpleStringProperty(this, "title");
        initialDirectory = new SimpleObjectProperty<>(this, "initialDirectory");
    }

    public void showDialog(Window parentWindows, Consumer<List<File>> callback) {
        if (multiDirectoryChooserView == null) {
            multiDirectoryChooserView = new MultiDirectoryChooserView(this, files -> {
                if (callback != null) {
                    callback.accept(files);
                }
                modal.close();
            });
        }
        multiDirectoryChooserView.refresh();
        if (modal == null) {
            modal = new Modal(parentWindows.getScene().getRoot(), multiDirectoryChooserView);
        }
        modal.show();
    }


}
