package club.xiaojiawei.controls;

import club.xiaojiawei.skin.LabelSeparatorSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.HPos;
import javafx.scene.AccessibleRole;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.Objects;
import java.util.List;

/**
 * 带文本和图形的水平分隔线，默认居中，支持左、右对齐。
 * 文本为空且没有图形时显示完整横线；空间不足时文本以省略号显示。
 * 可通过 .label-separator-ui > .label 和 .separator 定制样式。
 *
 * @author 肖嘉威
 * @date 2026/10/8 11:04
 */
public class LabelSeparator extends Control {

    public static final String DEFAULT_STYLE_CLASS = "label-separator-ui";

    private static final StyleablePropertyFactory<LabelSeparator> STYLEABLES =
            new StyleablePropertyFactory<>(Control.getClassCssMetaData());
    private static final CssMetaData<LabelSeparator, ContentDisplay> CONTENT_DISPLAY =
            STYLEABLES.createEnumCssMetaData(ContentDisplay.class, "-fx-content-display",
                    separator -> separator.contentDisplay, ContentDisplay.LEFT);
    private static final CssMetaData<LabelSeparator, Number> GRAPHIC_TEXT_GAP =
            STYLEABLES.createSizeCssMetaData("-fx-graphic-text-gap",
                    separator -> separator.graphicTextGap, 4.0);

    private final StringProperty text = new SimpleStringProperty(this, "text", "");
    private final ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic");
    private final StyleableObjectProperty<ContentDisplay> contentDisplay =
            new SimpleStyleableObjectProperty<>(CONTENT_DISPLAY, this, "contentDisplay", ContentDisplay.LEFT);
    private final StyleableDoubleProperty graphicTextGap =
            new SimpleStyleableDoubleProperty(GRAPHIC_TEXT_GAP, this, "graphicTextGap", 4.0);
    private final ObjectProperty<HPos> textAlignment =
            new SimpleObjectProperty<>(this, "textAlignment", HPos.CENTER);

    public LabelSeparator() {
        this("");
    }

    public LabelSeparator(String text) {
        this(text, HPos.CENTER);
    }

    public LabelSeparator(String text, HPos textAlignment) {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        setFocusTraversable(false);
        setAccessibleRole(AccessibleRole.TEXT);
        setText(text);
        setTextAlignment(textAlignment);
    }

    public final String getText() {
        return text.get();
    }

    public final StringProperty textProperty() {
        return text;
    }

    public final void setText(String text) {
        this.text.set(text);
    }

    public final Node getGraphic() {
        return graphic.get();
    }

    /**
     * 图形节点，由 contentDisplay 决定其与文本的相对位置；null 表示没有图形。
     */
    public final ObjectProperty<Node> graphicProperty() {
        return graphic;
    }

    public final void setGraphic(Node graphic) {
        this.graphic.set(graphic);
    }

    public final ContentDisplay getContentDisplay() {
        return contentDisplay.get();
    }

    /**
     * 与 JavaFX Label 一致的图文排列方式，默认 LEFT。
     */
    public final ObjectProperty<ContentDisplay> contentDisplayProperty() {
        return contentDisplay;
    }

    public final void setContentDisplay(ContentDisplay contentDisplay) {
        this.contentDisplay.set(Objects.requireNonNull(contentDisplay, "contentDisplay"));
    }

    public final double getGraphicTextGap() {
        return graphicTextGap.get();
    }

    /**
     * 图文间距，默认 4 像素。
     */
    public final DoubleProperty graphicTextGapProperty() {
        return graphicTextGap;
    }

    public final void setGraphicTextGap(double graphicTextGap) {
        this.graphicTextGap.set(graphicTextGap);
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return STYLEABLES.getCssMetaData();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public final HPos getTextAlignment() {
        return textAlignment.get();
    }

    /**
     * 图文整体位置，取值为 LEFT、CENTER、RIGHT，不允许绑定到 null。
     */
    public final ObjectProperty<HPos> textAlignmentProperty() {
        return textAlignment;
    }

    public final void setTextAlignment(HPos textAlignment) {
        this.textAlignment.set(Objects.requireNonNull(textAlignment, "textAlignment"));
    }

    @Override
    public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
        if (attribute == AccessibleAttribute.TEXT && getAccessibleText() == null) {
            return getText();
        }
        return super.queryAccessibleAttribute(attribute, parameters);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new LabelSeparatorSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(LabelSeparator.class.getResource("css/labelSeparator.css")).toExternalForm();
    }
}
