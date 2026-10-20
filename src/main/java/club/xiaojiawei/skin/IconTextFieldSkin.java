package club.xiaojiawei.skin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

/**
 *
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/22 11:03
 */
public class IconTextFieldSkin extends TextFieldSkin {

    private final HBox nodePane = new HBox();

    private final boolean placeholder;

    private final BooleanProperty hideIcon = new SimpleBooleanProperty(false);

    public boolean isHideIcon() {
        return hideIcon.get();
    }

    public void setHideIcon(boolean hideIcon) {
        this.hideIcon.set(hideIcon);
    }

    public BooleanProperty hideIconProperty() {
        return hideIcon;
    }

    public IconTextFieldSkin(TextField control) {
        this(control, true);
    }

    public IconTextFieldSkin(TextField control, boolean placeholder) {
        super(control);
        this.placeholder = placeholder;
        nodePane.setId("icon-pane");
        nodePane.setSpacing(5);
        nodePane.setAlignment(Pos.CENTER);
        nodePane.setManaged(false);
        registerChangeListener(hideIcon, observable -> {
            nodePane.setVisible(!isHideIcon());
            control.requestLayout();
        });
        getChildren().add(nodePane);
    }

    private double iconWidth() {
        return isHideIcon() ? 0 : snapSizeX(nodePane.prefWidth(-1));
    }

    @Override
    protected void layoutChildren(double x, double y, double width, double height) {
        double iconWidth = Math.min(width, iconWidth());
        super.layoutChildren(x, y, Math.max(0, width - (placeholder ? iconWidth : 0)), height);
        nodePane.resizeRelocate(x + width - iconWidth, y, iconWidth, height);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset)
               + (placeholder ? iconWidth() : 0);
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset,
                                     double bottomInset, double leftInset) {
        return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset)
               + (placeholder ? iconWidth() : 0);
    }

    protected void addTipButton(Node... nodes) {
        nodePane.getChildren().addAll(nodes);
        getSkinnable().requestLayout();
    }

    protected void removeTipButton(Node... nodes) {
        nodePane.getChildren().removeAll(nodes);
        getSkinnable().requestLayout();
    }

    protected Button buildIconButton() {
        return buildIconButton(null, null);
    }

    protected Button buildIconButton(String svgContent) {
        return buildIconButton(svgContent, Color.BLACK);
    }

    protected Button buildIconButton(String svgContent, Paint svgPaint) {
        Button button = new Button();
        button.getStyleClass().add("input-action-button");

        if (svgContent != null) {
            SVGPath svgPath = new SVGPath();
            button.setGraphic(svgPath);
            svgPath.setContent(svgContent);
            svgPath.setFill(svgPaint);
            svgPath.getStyleClass().add("input-action-graphic");
        }

        button.setPadding(new Insets(0));
        button.setFocusTraversable(false);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setCursor(Cursor.HAND);

        return button;
    }
}
