package club.xiaojiawei.component;

import club.xiaojiawei.controls.ico.AbstractIco;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

/**
 * 窗口标题栏
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/13 16:17
 */
public class WindowBar extends AnchorPane {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public String getTitle() {
        return titleText.getText();
    }

    /**
     * 标题
     * @return
     */
    public StringProperty titleProperty() {
        return titleText.textProperty();
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public WindowBar() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WindowBar.class.getResource(WindowBar.class.getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private HBox titlePane;
    @FXML
    private AbstractIco pinIco;
    @FXML
    private Text titleText;

    private double startX;

    private double startY;

    private void afterFXMLLoaded() {
        addListener();
    }

    private void addListener(){
        titlePane.setCursor(Cursor.MOVE);
        titlePane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            this.getScene().getWindow().setOpacity(0.4);
            startX = event.getSceneX();
            startY = event.getSceneY();
        });
        titlePane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> this.getScene().getWindow().setOpacity(1));
        titlePane.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            this.getScene().getWindow().setX(event.getScreenX() - startX);
            this.getScene().getWindow().setY(event.getScreenY() - startY);
        });
    }

    /**
     * 是否置顶窗口
     */
    @FXML
    protected void pinMouseClicked(){
        if (Objects.equals(pinIco.getColor(), "gray")){
            pinIco.setColor("main-color!important");
            Window window = this.getScene().getWindow();
            if (window instanceof Popup popup){
                popup.setAutoHide(false);
            }else if (window instanceof Stage stage){
                stage.setAlwaysOnTop(true);
            }
        }else {
            pinIco.setColor("gray");
            Window window = this.getScene().getWindow();
            if (window instanceof Popup popup){
                popup.setAutoHide(true);
            }else if (window instanceof Stage stage){
                stage.setAlwaysOnTop(false);
            }
        }
    }

    /**
     * 关闭窗口
     */
    @FXML
    protected void closeMouseClicked(){
        this.getScene().getWindow().hide();
    }

}
