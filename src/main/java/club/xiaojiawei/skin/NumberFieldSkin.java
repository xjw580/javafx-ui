package club.xiaojiawei.skin;

import club.xiaojiawei.controls.NumberField;
import club.xiaojiawei.utils.TextFieldSkinUtil;
import javafx.scene.control.Button;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 17:35
 */
public class NumberFieldSkin extends TextFieldSkin {

    public NumberFieldSkin(NumberField numberField) {
        super(numberField);

        Button subBtn = TextFieldSkinUtil.buildButton("M11.3,1.4H.7c-.38,0-.7-.31-.7-.7S.31,0,.7,0H11.3c.38,0,.7,.31,.7,.7s-.31,.7-.7,.7Z", Paint.valueOf("#4E5053FF"));
        subBtn.setId("sub-btn");
        subBtn.setOnAction(actionEvent -> numberField.decrement());
        Button addBtn = TextFieldSkinUtil.buildButton("M11.4,5.4H6.6V.6c0-.33-.27-.6-.6-.6s-.6,.27-.6,.6V5.4H.6c-.33,0-.6,.27-.6,.6s.27,.6,.6,.6H5.4v4.8c0,.33,.27,.6,.6,.6s.6-.27,.6-.6V6.6h4.8c.33,0,.6-.27,.6-.6s-.27-.6-.6-.6Z", Paint.valueOf("#4E5053FF"));
        addBtn.setId("add-btn");
        addBtn.setOnAction(actionEvent -> numberField.increment());

        HBox hBox = new HBox();
        hBox.setId("btn-group");
        hBox.setStyle("-fx-spacing: 5;-fx-translate-y: -1;-fx-alignment: CENTER_RIGHT");
        hBox.getChildren().addAll(
                subBtn,
                addBtn
        );

        StackPane stackPane = new StackPane(hBox);
        stackPane.setId("btn-stack-pane");
        stackPane.toFront();
        
        getChildren().add(stackPane);
    }

}
