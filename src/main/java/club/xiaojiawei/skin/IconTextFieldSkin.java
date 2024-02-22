package club.xiaojiawei.skin;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

/**
 *
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/22 11:03
 */
public class IconTextFieldSkin extends TextFieldSkin {

    private final HBox btnGroup = new HBox();

    public IconTextFieldSkin(TextField control) {
        super(control);

        btnGroup.setId("btn-group");
        btnGroup.setStyle("-fx-spacing: 5;-fx-alignment: CENTER");

        Group group = new Group(btnGroup);
        HBox hBox = new HBox(group);
        hBox.setStyle("-fx-translate-y: -1;-fx-alignment: CENTER_RIGHT;");

        StackPane stackPane = new StackPane(hBox);
        btnGroup.widthProperty().addListener((observableValue, number, t1) -> {
            Insets insets = control.getPadding();
            control.setStyle(String.format("-fx-padding: %s %s %s %s!important;", insets.getTop(), insets.getRight() + t1.doubleValue() , insets.getBottom(), insets.getLeft()));
            group.setTranslateX(t1.doubleValue());
        });
        stackPane.setId("btn-stack-pane");
        stackPane.toFront();
        getChildren().add(stackPane);
    }

    protected void addIconButton(Button ...button){
        btnGroup.getChildren().addAll(button);
    }

    protected void removeIconButton(Button ...button){
        btnGroup.getChildren().removeAll(button);
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
