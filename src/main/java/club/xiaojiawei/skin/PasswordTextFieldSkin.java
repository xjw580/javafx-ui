package club.xiaojiawei.skin;

import club.xiaojiawei.controls.PasswordTextField;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.PasswordField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

/**
 * 密码框用的皮肤样式
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 16:25
 */
public class PasswordTextFieldSkin extends TextFieldSkin {

    private final Button actionButton = new Button();
    private final SVGPath actionIcon = new SVGPath();

    private boolean mask = true;

    public PasswordTextFieldSkin(PasswordTextField passwordTextField) {

        super(passwordTextField);

        actionButton.setId("actionButton");
        actionButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        actionButton.setPrefSize(30,30);
        actionButton.setFocusTraversable(false);
        actionButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, new Insets(0))));

        getChildren().add(actionButton);
        actionButton.setCursor(Cursor.HAND);
        actionButton.toFront();

        actionIcon.setContent(Icons.SHOW.getContent());
        actionButton.setGraphic(actionIcon);

        actionButton.setVisible(false);

        actionButton.setOnMouseClicked(event -> {
            if(mask) {
                actionIcon.setContent(Icons.HIDE.getContent());
                actionIcon.setFill(Paint.valueOf("#0075FF"));
                mask = false;
            } else {
                actionIcon.setContent(Icons.SHOW.getContent());
                actionIcon.setFill(Color.BLACK);
                mask = true;
            }
            passwordTextField.setText(passwordTextField.getText());
            passwordTextField.end();
        });

        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> actionButton.setVisible(!passwordTextField.isHideForever() && !newValue.isEmpty()));

    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        layoutInArea(actionButton, x, y, w, h,0, HPos.RIGHT, VPos.CENTER);
    }

    @Override
    protected String maskText(String txt) {
        if (getSkinnable() instanceof PasswordField && mask) {
            int n = txt.length();
            return "●".repeat(n);
        } else {
            return txt;
        }
    }
}

enum Icons {

    HIDE("M7.9,11.31l1.8-1.54h.2c3.4,0,5.9-1.11,7.8-3.43-.6-.69-1.3-1.29-2-1.8l1.5-1.11c1.1,.77,2,1.8,2.8,2.91-2.3,3.43-5.7,5.06-10,5.06-.7,.09-1.4,0-2.1-.09Zm-5.6-2.4c-.8-.69-1.6-1.54-2.3-2.57C2.3,2.91,5.7,1.29,10,1.29h1.1l-2,1.71c-3,.17-5.3,1.29-7,3.34,.4,.51,.9,1.03,1.4,1.37,0,0-1.2,1.2-1.2,1.2ZM15.7,0l1.4,1.2L4.5,12l-1.4-1.2L15.7,0Z"),

    SHOW("M10,12c-4.3,0-7.7-2-10-6C2.3,2,5.7,0,10,0s7.7,2,10,6c-2.3,4-5.7,6-10,6Zm.2-10c-3,0-6,1.3-7.7,4,1.7,2.7,4.7,4,7.7,4s5.6-1.3,7.3-4c-1.7-2.7-4.3-4-7.3-4Zm-.2,7c-1.7,0-3-1.3-3-3s1.3-3,3-3,3,1.3,3,3-1.3,3-3,3Zm0-2c.6,0,1-.4,1-1s-.4-1-1-1-1,.4-1,1,.4,1,1,1Z");

    private String content;

    Icons(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
