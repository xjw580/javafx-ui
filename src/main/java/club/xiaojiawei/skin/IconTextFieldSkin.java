package club.xiaojiawei.skin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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

    private Insets initInsets = null;

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

        nodePane.setId("icon-pane");
        nodePane.setStyle("-fx-spacing: 5;-fx-alignment: CENTER");

        Group group = new Group(nodePane);
        HBox hBox = new HBox(group);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        nodePane.widthProperty().addListener((observableValue, number, t1) -> {
            if (t1.intValue() == number.intValue()) {
                return;
            }
            if (initInsets == null) {
                initInsets = control.getPadding();
            }

            if (!hideIcon.get()) {
                control.setStyle(String.format("-fx-padding: %s %s %s %s!important;", initInsets.getTop(), initInsets.getRight() + (placeholder? t1.doubleValue() : 0) , initInsets.getBottom(), initInsets.getLeft()));
            }
            group.setTranslateX(placeholder? t1.doubleValue() : 0);
        });

        hideIcon.addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                group.setVisible(false);
                group.setManaged(false);
                control.setStyle("");
            }else {
                group.setVisible(true);
                group.setManaged(true);
                control.setStyle(String.format("-fx-padding: %s %s %s %s!important;", initInsets.getTop(), initInsets.getRight() + (placeholder? nodePane.getWidth() : 0) , initInsets.getBottom(), initInsets.getLeft()));
            }
        });

        getChildren().add(hBox);
    }

    protected void addTipButton(Node...nodes){
        nodePane.getChildren().addAll(nodes);
    }

    protected void removeTipButton(Node ...nodes){
        nodePane.getChildren().removeAll(nodes);
    }

    protected Button buildIconButton(){
        return buildIconButton(null, null);
    }

    protected Button buildIconButton(String svgContent){
        return buildIconButton(svgContent, Color.BLACK);
    }

    protected Button buildIconButton(String svgContent, Paint svgPaint){
        Button button = new Button();

        if (svgContent != null){
            SVGPath svgPath = new SVGPath();
            button.setGraphic(svgPath);
            svgPath.setContent(svgContent);
            svgPath.setFill(svgPaint);
        }

        button.setPadding(new Insets(0));
        button.setFocusTraversable(false);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setStyle("-fx-background-color: transparent;-fx-cursor: hand");

        return button;
    }
}
