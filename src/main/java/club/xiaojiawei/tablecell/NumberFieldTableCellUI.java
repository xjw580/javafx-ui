package club.xiaojiawei.tablecell;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2024/11/18 23:14
 */
public class NumberFieldTableCellUI<S, T> extends TextFieldTableCell<S, T> {

    private TextField node;

    {
        AtomicReference<ChangeListener<Node>> changeListenerAtomicReference = new AtomicReference<>();
        changeListenerAtomicReference.set((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.getStyleClass().addAll(styleClass());
                graphicProperty().removeListener(changeListenerAtomicReference.get());
                if (newValue instanceof TextField textField) {
                    node = textField;
                    textField.setTextFormatter(new TextFormatter<>(change -> {
                        String nodeText = node.getText();
                        if (nodeText == null) {
                            return change;
                        }
                        String text = nodeText.substring(0, change.getRangeStart()) + change.getText() + nodeText.substring(change.getRangeEnd());
                        if (text.matches("^-?\\d*(\\.\\d{0,5})?$")) {
                            return change;
                        }
                        return null;
                    }));
                }
            }
        });
        graphicProperty().addListener(changeListenerAtomicReference.get());
    }

    protected String[] styleClass(){
        return new String[]{"text-field-ui", "text-field-ui-tiny"};
    }

    @Override
    public void startEdit() {
        if (node != null) {
            node.setPrefWidth(getWidth());
            node.setPrefHeight(getHeight());
        }
        super.startEdit();
    }

}
