package club.xiaojiawei.skin;

import club.xiaojiawei.controls.PasswordTextField;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import lombok.Getter;

/**
 * 密码框用的皮肤样式
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 16:25
 */
public class PasswordTextFieldSkin extends IconTextFieldSkin {

    private final SVGPath actionIcon = new SVGPath();
    private final Button actionButton = buildIconButton();

    private final BooleanProperty mask = new SimpleBooleanProperty(true);

    public PasswordTextFieldSkin(PasswordTextField passwordTextField) {
        super(passwordTextField);
        actionButton.setId("action-btn");
        actionButton.setVisible(false);
        actionButton.setOnMouseClicked(event -> {
            if(mask.get()) {
                actionIcon.setContent(Icons.HIDE.getContent());
                actionIcon.setFill(Paint.valueOf("#0075FF"));
                mask.set(false);
            } else {
                actionIcon.setContent(Icons.SHOW.getContent());
                actionIcon.setFill(Color.BLACK);
                mask.set(true);
            }
            passwordTextField.setText(passwordTextField.getText());
            passwordTextField.end();
        });

        actionIcon.setContent(Icons.SHOW.getContent());
        actionButton.setGraphic(actionIcon);

        addTipButton(actionButton);

        passwordTextField.textProperty().addListener((observable, oldValue, newValue) -> actionButton.setVisible(!passwordTextField.isHideForever() && !newValue.isEmpty()));
    }

    @Override
    protected String maskText(String txt) {
        if (getSkinnable() instanceof PasswordField && (mask == null || mask.get())) {
            return "●".repeat(txt.length());
        } else {
            return txt;
        }
    }
}

@Getter
enum Icons {

    HIDE("M7.9,13.2l1.8-1.8h.2c3.4,0,5.9-1.3,7.8-4-.6-.8-1.3-1.5-2-2.1l1.5-1.3c1.1,.9,2,2.1,2.8,3.4-2.3,4-5.7,5.9-10,5.9-.7,.1-1.4,0-2.1-.1Zm-5.6-2.8c-.8-.8-1.6-1.8-2.3-3C2.3,3.4,5.7,1.5,10,1.5h1.1l-2,2c-3,.2-5.3,1.5-7,3.9,.4,.6,.9,1.2,1.4,1.6,0,0-1.2,1.4-1.2,1.4ZM15.7,0l1.4,1.4L4.5,14l-1.4-1.4L15.7,0Z"),

    SHOW("M10,12c-4.3,0-7.7-2-10-6C2.3,2,5.7,0,10,0s7.7,2,10,6c-2.3,4-5.7,6-10,6Zm.2-10c-3,0-6,1.3-7.7,4,1.7,2.7,4.7,4,7.7,4s5.6-1.3,7.3-4c-1.7-2.7-4.3-4-7.3-4Zm-.2,7c-1.7,0-3-1.3-3-3s1.3-3,3-3,3,1.3,3,3-1.3,3-3,3Zm0-2c.6,0,1-.4,1-1s-.4-1-1-1-1,.4-1,1,.4,1,1,1Z");

    private final String content;

    Icons(String content) {
        this.content = content;
    }

}
