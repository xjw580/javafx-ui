package club.xiaojiawei.skin;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.skin.TreeViewSkin;
import javafx.scene.control.skin.VirtualFlow;

/**
 * @author 肖嘉威
 * @date 2024/12/12 10:15
 */

public class VisibleTreeViewSkin<T> extends TreeViewSkin<T> {

    public VisibleTreeViewSkin(TreeView<T> treeView) {
        super(treeView);
    }

    public boolean isIndexVisible(int index) {
        VirtualFlow<TreeCell<T>> flow = getVirtualFlow();
        return flow.getFirstVisibleCell() != null &&
               flow.getLastVisibleCell() != null &&
               flow.getFirstVisibleCell().getIndex() < index &&
               flow.getLastVisibleCell().getIndex() > index;
    }
}