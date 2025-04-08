package club.xiaojiawei.tablecell;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2024/11/18 23:14
 */
public class TextFieldTableCellUI<S, T> extends TextFieldTableCell<S, T> {

    protected TextField node;

    public TextFieldTableCellUI() {
    }

    public TextFieldTableCellUI(StringConverter<T> stringConverter) {
        super(stringConverter);
    }

    {
        AtomicReference<ChangeListener<Node>> changeListenerAtomicReference = new AtomicReference<>();
        changeListenerAtomicReference.set((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.getStyleClass().addAll(styleClass());
                graphicProperty().removeListener(changeListenerAtomicReference.get());
                if (newValue instanceof TextField textField) {
                    node = textField;
                }
            }
        });
        graphicProperty().addListener(changeListenerAtomicReference.get());
    }

    protected String[] styleClass() {
        return new String[]{"text-field-ui", "text-field-ui-tiny"};
    }

    @Override
    public void startEdit() {
        if (node != null) {
            node.setPrefWidth(getWidth() - 1);
            node.setPrefHeight(getHeight() - 1);
        }
        super.startEdit();
    }

}
