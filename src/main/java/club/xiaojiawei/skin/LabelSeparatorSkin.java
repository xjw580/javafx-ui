package club.xiaojiawei.skin;

import club.xiaojiawei.controls.LabelSeparator;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBase;

/**
 * 使用原生 Separator 绘制两侧横线，Label 保持透明背景。
 */
public class LabelSeparatorSkin extends SkinBase<LabelSeparator> {

    private final Separator leftLine = new Separator();
    private final Label label = new Label();
    private final Separator rightLine = new Separator();

    public LabelSeparatorSkin(LabelSeparator control) {
        super(control);
        label.textProperty().bind(control.textProperty());
        label.graphicProperty().bind(control.graphicProperty());
        label.contentDisplayProperty().bind(control.contentDisplayProperty());
        label.graphicTextGapProperty().bind(control.graphicTextGapProperty());
        label.setMinWidth(0);
        leftLine.setMinWidth(0);
        rightLine.setMinWidth(0);
        getChildren().setAll(leftLine, label, rightLine);
        registerChangeListener(control.textProperty(), observable -> control.requestLayout());
        registerChangeListener(control.graphicProperty(), observable -> control.requestLayout());
        registerChangeListener(control.contentDisplayProperty(), observable -> control.requestLayout());
        registerChangeListener(control.graphicTextGapProperty(), observable -> control.requestLayout());
        registerChangeListener(control.textAlignmentProperty(), observable -> control.requestLayout());
    }

    private boolean hasContent() {
        String text = getSkinnable().getText();
        boolean showText = text != null && !text.isEmpty()
                           && label.getContentDisplay() != ContentDisplay.GRAPHIC_ONLY;
        boolean showGraphic = label.getGraphic() != null && label.getGraphic().isVisible()
                              && label.getContentDisplay() != ContentDisplay.TEXT_ONLY;
        return showText || showGraphic;
    }

    @Override
    protected void layoutChildren(double x, double y, double width, double height) {
        boolean contentVisible = hasContent();
        label.setVisible(contentVisible);
        rightLine.setVisible(contentVisible);
        if (!contentVisible) {
            leftLine.resizeRelocate(x, y, width, height);
            return;
        }

        double minLineWidth = Math.min(width, snapSizeX(leftLine.prefWidth(-1))
                                              + snapSizeX(rightLine.prefWidth(-1)));
        double labelWidth = Math.min(width - minLineWidth, snapSizeX(label.prefWidth(-1)));
        double lineWidth = Math.max(0, width - labelWidth);
        double leftWidth = switch (getSkinnable().getTextAlignment()) {
            case LEFT -> Math.min(snapSizeX(leftLine.prefWidth(-1)), lineWidth / 2);
            case CENTER -> lineWidth / 2;
            case RIGHT -> lineWidth - Math.min(snapSizeX(rightLine.prefWidth(-1)), lineWidth / 2);
        };
        leftWidth = snapPositionX(leftWidth);
        double labelHeight = Math.min(height, snapSizeY(label.prefHeight(labelWidth)));

        leftLine.resizeRelocate(x, y, leftWidth, height);
        label.resizeRelocate(x + leftWidth, y + snapPositionY((height - labelHeight) / 2),
                labelWidth, labelHeight);
        rightLine.resizeRelocate(x + leftWidth + labelWidth, y,
                Math.max(0, width - leftWidth - labelWidth), height);
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset,
                                     double bottomInset, double leftInset) {
        return leftInset + rightInset;
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return leftInset + 200 + (hasContent() ? label.prefWidth(-1) : 0) + rightInset;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset,
                                      double bottomInset, double leftInset) {
        return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset,
                                       double bottomInset, double leftInset) {
        double contentHeight = leftLine.prefHeight(-1);
        if (hasContent()) {
            contentHeight = Math.max(contentHeight, label.prefHeight(-1));
        }
        return topInset + contentHeight + bottomInset;
    }

    @Override
    public void dispose() {
        label.textProperty().unbind();
        label.graphicProperty().unbind();
        label.contentDisplayProperty().unbind();
        label.graphicTextGapProperty().unbind();
        label.setGraphic(null);
        super.dispose();
    }
}
