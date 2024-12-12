package club.xiaojiawei.controls;

import club.xiaojiawei.skin.VisibleTreeViewSkin;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeView;

/**
 * @author 肖嘉威
 * @date 2024/12/12 10:19
 */
public class VisibleTreeView<T> extends TreeView<T> {

    private VisibleTreeViewSkin<?> visibleTreeViewSkin;

    @Override
    protected Skin<?> createDefaultSkin() {
        return visibleTreeViewSkin = new VisibleTreeViewSkin<>(this);
    }

    public boolean isIndexVisible(int index) {
        return visibleTreeViewSkin != null && visibleTreeViewSkin.isIndexVisible(index);
    }

}
