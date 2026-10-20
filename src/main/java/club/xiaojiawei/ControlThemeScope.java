package club.xiaojiawei;

import club.xiaojiawei.enums.Theme;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Applies a composite control's explicit theme without modifying caller-owned style classes. */
final class ControlThemeScope implements AutoCloseable {
    private static final Object OWNERS_KEY = new Object();
    private static final PseudoClass BASE = PseudoClass.getPseudoClass("ui-base");
    private static final PseudoClass WINDOWS = PseudoClass.getPseudoClass("ui-windows");
    private final Parent root;
    private final Theme theme;
    private final Map<Node, Watch> watches = new IdentityHashMap<>();

    ControlThemeScope(Parent root, Theme theme) {
        this.root = root;
        this.theme = theme;
        attach(root);
    }

    Theme theme() {
        return theme;
    }

    private void attach(Node node) {
        if (watches.containsKey(node)) return;
        Watch watch = new Watch(node);
        watches.put(node, watch);
        node.getStyleClass().addListener(watch.classes);
        if (node instanceof Parent parent) parent.getChildrenUnmodifiable().addListener(watch.children);
        refresh(watch);
    }

    private boolean hasOwnTheme(Node node) {
        return node.getStyleClass().stream()
                .anyMatch(value -> value.endsWith("-ui-base") || value.endsWith("-ui-windows"));
    }

    private void refresh(Watch watch) {
        if (watches.get(watch.node) != watch) return;
        boolean blocked = watch.node != root && hasOwnTheme(watch.node);
        setOwner(watch.node, !blocked);
        if (watch.node instanceof Parent parent) {
            for (Node child : List.copyOf(parent.getChildrenUnmodifiable())) {
                if (blocked) detach(child);
                else attach(child);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setOwner(Node node, boolean active) {
        Map<ControlThemeScope, Boolean> owners =
                (Map<ControlThemeScope, Boolean>) node.getProperties().get(OWNERS_KEY);
        if (owners == null) {
            if (!active) return;
            owners = new IdentityHashMap<>();
            node.getProperties().put(OWNERS_KEY, owners);
        }
        if (active) owners.put(this, Boolean.TRUE);
        else owners.remove(this);
        Theme selected = null;
        for (Node ancestor = node; ancestor != null && selected == null; ancestor = ancestor.getParent()) {
            for (ControlThemeScope owner : owners.keySet()) {
                if (owner.root == ancestor) {
                    selected = owner.theme;
                    break;
                }
            }
        }
        node.pseudoClassStateChanged(BASE, selected == Theme.BASE);
        node.pseudoClassStateChanged(WINDOWS, selected == Theme.WINDOWS);
        if (owners.isEmpty()) node.getProperties().remove(OWNERS_KEY);
    }

    private void detach(Node node) {
        Watch watch = watches.remove(node);
        if (watch == null) return;
        node.getStyleClass().removeListener(watch.classes);
        if (node instanceof Parent parent) {
            parent.getChildrenUnmodifiable().removeListener(watch.children);
            for (Node child : List.copyOf(parent.getChildrenUnmodifiable())) detach(child);
        }
        setOwner(node, false);
    }

    @Override
    public void close() {
        detach(root);
    }

    private final class Watch {
        private final Node node;
        private final ListChangeListener<String> classes = change -> refresh(this);
        private final ListChangeListener<Node> children = this::childrenChanged;

        private void childrenChanged(ListChangeListener.Change<? extends Node> change) {
            if (watches.get(node) != this) return;
            while (change.next()) {
                if (change.wasRemoved()) for (Node child : change.getRemoved()) detach(child);
                if (change.wasAdded() && (node == root || !hasOwnTheme(node))) {
                    for (Node child : change.getAddedSubList()) attach(child);
                }
            }
        }

        private Watch(Node node) {
            this.node = node;
        }
    }
}
