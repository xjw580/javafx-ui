package club.xiaojiawei.controls;

import club.xiaojiawei.skin.ComboBoxWideSkin;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;

/**
 * @author 肖嘉威
 * @date 2024/11/15 11:31
 */
public class WideComboBox<T> extends ComboBox<T> {

    private final IntegerProperty maxColumCount = new SimpleIntegerProperty(this, "columCount", 3);

    public int getMaxColumCount() {
        return maxColumCount.get();
    }

    public IntegerProperty maxColumCountProperty() {
        return maxColumCount;
    }

    public void setMaxColumCount(int maxColumCount) {
        this.maxColumCount.set(maxColumCount);
    }

    public WideComboBox() {
        this(FXCollections.observableArrayList());
    }

    public WideComboBox(ObservableList<T> items) {
        super(items);
        setOnMouseClicked(event -> {
            show();
        });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ComboBoxWideSkin<>(this);
    }

}
