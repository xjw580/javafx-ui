package club.xiaojiawei.skin;

import club.xiaojiawei.controls.WideComboBox;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.control.skin.ComboBoxBaseSkin;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.util.Objects;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/21 9:54
 */
public class ComboBoxWideSkin<T> extends ComboBoxBaseSkin<T> {

    private final WideComboBox<T> outerComboBox;

    private PopupControl popup;

    private GridPane popupNode;

    private final Label displayLabel = new Label();

    private final ListChangeListener<? super T> itemChangeListener = observable -> {
        draw();
    };

    public ComboBoxWideSkin(WideComboBox<T> comboBoxBase) {
        super(comboBoxBase);
        outerComboBox = comboBoxBase;
    }

    private double prevWidth;

    @Override
    public Node getDisplayNode() {
        if (!displayLabel.getStyleClass().contains("list-cell")) {
            displayLabel.getStyleClass().add("list-cell");
            outerComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, o, t1) -> {
                displayLabel.setText(outerComboBox.getConverter().toString(t1));
                ObservableList<Node> children = getPopupContent().getChildren();
                boolean isFind = false;
                for (Node child : children) {
                    if (!isFind && Objects.equals(child.getUserData(), t1)) {
                        child.getStyleClass().remove("bg-ui");
                        child.getStyleClass().add("bg-ui");
                        isFind = true;
                    } else {
                        child.getStyleClass().remove("bg-ui");
                    }
                }
            });
            outerComboBox.nodeOrientationProperty().addListener((observableValue, nodeOrientation, t1) -> {
                draw();
            });
            displayLabel.setText(outerComboBox.getConverter().toString(outerComboBox.getValue()));
        }
        return displayLabel;
    }

    private GridPane getPopupContent() {
        if (popupNode == null) {
            popupNode = new GridPane();
            popupNode.setStyle("-fx-background-color: white;-fx-effect: default-effect");
            popupNode.getStyleClass().add("grid-pane");
            outerComboBox.getItems().addListener(itemChangeListener);
            outerComboBox.itemsProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue != null) {
                    oldValue.removeListener(itemChangeListener);
                }
                if (newValue != null) {
                    newValue.addListener(itemChangeListener);
                }
            });
            draw();
        }
        return popupNode;
    }

    private void draw() {
        ObservableList<T> items = outerComboBox.getItems();
        int size = items.size();
        GridPane gridPane = getPopupContent();
        gridPane.getChildren().clear();
        int maxCol = outerComboBox.getMaxColumCount(), col = 0, row = 0;
        int l = maxCol;
        for (int i = 1; i < l; i++) {
            if (Math.ceil((double) size / i) < 6) {
                maxCol = i;
                break;
            }
        }
        T value = outerComboBox.getValue();
        for (int i = 0; i < maxCol; i++) {
            ColumnConstraints column1 = new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, true);
            gridPane.getColumnConstraints().add(column1);
        }
        boolean isFind = false;
        StringConverter<T> converter = outerComboBox.getConverter();
        int maxRow = (int) Math.ceil(items.size() / (double) maxCol);
        for (T item : items) {
            if (item == null) continue;
            Label label = new Label(converter.toString(item));
            StackPane stackPane = new StackPane(label);
            if (outerComboBox.isEnlargeEnabled()) {
                stackPane.setStyle("-fx-padding: 10 15 10 15");
            }
            stackPane.setUserData(item);
            stackPane.getStyleClass().addAll("bg-hover-ui", "radius-ui");
            stackPane.setPrefHeight(displayLabel.getHeight());
            if (maxCol == 1) {
                stackPane.setMinWidth(prevWidth = calcRealWidth());
            }
            double v = displayLabel.getHeight() / 3D;
            stackPane.setPadding(new Insets(0, v, 0, v));
            stackPane.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    outerComboBox.getSelectionModel().select(item);
                    hide();
                }
            });
            if (!isFind && Objects.equals(value, item)) {
                stackPane.getStyleClass().add("bg-ui");
                isFind = true;
            }
            if (Objects.equals(outerComboBox.getOrientation(), Orientation.HORIZONTAL)) {
                gridPane.add(stackPane, col++, row);
                if (col >= maxCol) {
                    col = 0;
                    row++;
                }
            } else {
                gridPane.add(stackPane, col, row++);
                if (row >= maxRow) {
                    row = 0;
                    col++;
                }
            }
        }
    }

    private double calcRealWidth() {
        double offsetX = 0;
        if (outerComboBox.getEffect() instanceof DropShadow shadow) {
            offsetX = shadow.getRadius() - shadow.getOffsetX();
        }
        return outerComboBox.getWidth() - offsetX;
    }

    private PopupControl getPopup() {
        if (popup == null) {
            popup = new PopupControl() {
                {
                    this.setSkin(new Skin<>() {
                        public Skinnable getSkinnable() {
                            return ComboBoxWideSkin.this.getSkinnable();
                        }

                        public Node getNode() {
                            return ComboBoxWideSkin.this.getPopupContent();
                        }

                        public void dispose() {
                        }
                    });
                }

                public Styleable getStyleableParent() {
                    return ComboBoxWideSkin.this.getSkinnable();
                }
            };
            popup.setAutoHide(true);
            popup.setAutoFix(true);
            popup.setHideOnEscape(true);
            popup.showingProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    outerComboBox.hide();
                }
            });
            popup.getStyleClass().add("combo-box-popup");
        }
        return popup;
    }

    private void locatePopup() {
        if (!outerComboBox.isShowing()) return;
        if (calcRealWidth() != prevWidth) {
            draw();
        }
        Bounds bounds = displayLabel.localToScreen(displayLabel.getBoundsInLocal());
        PopupControl popupControl = getPopup();
        double offsetX = 0, offsetY = 0;
        double popupOffsetX = 0, popupOffsetY = 0;
//        if (outerComboBox.getEffect() instanceof DropShadow shadow) {
//            offsetX = shadow.getOffsetX() - shadow.getRadius();
//            offsetY = shadow.getOffsetY() + shadow.getRadius();
//        }
        if (getPopupContent().getEffect() instanceof DropShadow shadow) {
            popupOffsetX = -shadow.getOffsetX() + shadow.getRadius();
            popupOffsetY = -shadow.getOffsetY() + shadow.getRadius();
        }
        popupControl.setX(bounds.getMinX() - offsetX - popupOffsetX);
        popupControl.setY(bounds.getMaxY() - offsetY - popupOffsetY + 1);
    }

    private void showPopup() {
        if (getPopupContent().getChildren().isEmpty()) return;
        locatePopup();
        getPopup().show(outerComboBox.getScene().getWindow());
    }

    @Override
    public void show() {
        showPopup();
    }

    @Override
    public void hide() {
        getPopup().hide();
    }


}
