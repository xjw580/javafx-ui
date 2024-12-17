package club.xiaojiawei.controls;

import club.xiaojiawei.skin.ComboBoxWideSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * @author 肖嘉威
 * @date 2024/11/15 11:31
 */
public class WideComboBox<T> extends ComboBox<T> {

    private final IntegerProperty maxColumCount = new SimpleIntegerProperty(this, "maxColumCount", 4);

    /**
     * 鼠标悬浮在此控件上时滚动鼠标以改变值
     */
    private final BooleanProperty scrollValueEnabled = new SimpleBooleanProperty(this, "scrollEnabled", true);

    private final ObjectProperty<Orientation> orientation = new SimpleObjectProperty<>(this, "orientation", Orientation.HORIZONTAL);

    private final BooleanProperty enlargeEnabled = new SimpleBooleanProperty(this, "enlargeEnabled", true);

    public int getMaxColumCount() {
        return maxColumCount.get();
    }

    public IntegerProperty maxColumCountProperty() {
        return maxColumCount;
    }

    public void setMaxColumCount(int maxColumCount) {
        this.maxColumCount.set(maxColumCount);
    }

    public boolean getScrollValueEnabled() {
        return scrollValueEnabled.get();
    }

    public BooleanProperty scrollValueEnabledProperty() {
        return scrollValueEnabled;
    }

    public void setScrollValueEnabled(boolean scrollValueEnabled) {
        this.scrollValueEnabled.set(scrollValueEnabled);
    }

    public Orientation getOrientation() {
        return orientation.get();
    }

    public ObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation.set(orientation);
    }

    public boolean isEnlargeEnabled() {
        return enlargeEnabled.get();
    }

    public BooleanProperty enlargeEnabledProperty() {
        return enlargeEnabled;
    }

    public void setEnlargeEnabled(boolean enlargeEnabled) {
        this.enlargeEnabled.set(enlargeEnabled);
    }

    public WideComboBox() {
        this(FXCollections.observableArrayList());
    }

    public WideComboBox(ObservableList<T> items) {
        super(items);
        addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            show();
        });
        addEventFilter(ScrollEvent.SCROLL, event -> {
            if (getScrollValueEnabled()) {
                int index = getSelectionModel().getSelectedIndex();
                if (event.getDeltaY() > 0) {
                    index++;
                } else if (event.getDeltaY() < 0) {
                    if (index > 0) {
                        index--;
                    }
                }
                getSelectionModel().select(index);
            }
        });
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ComboBoxWideSkin<>(this);
    }

}
