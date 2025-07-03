package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.ico.ClearIco;
import club.xiaojiawei.controls.ico.FileIco;
import club.xiaojiawei.controls.ico.HelpIco;
import club.xiaojiawei.controls.ico.UpdateIco;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class MenuBarTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().addAll("menu-bar-ui");
        menuBar.setMaxWidth(200);
        Menu menu = new Menu();
        menu.getStyleClass().add("menu-ui");
        menu.setGraphic(new VBox(
                new FileIco(),
                new Label("复制")
        ){{
            setAlignment(Pos.CENTER);
            setSpacing(4);
            setPadding(new Insets(5, 2 , 3, 2));
        }});
        MenuItem menuItem = new MenuItem("复制1");
        menuItem.getStyleClass().add("menu-item-ui");
        menuItem.setGraphic(new UpdateIco());
        MenuItem menuItem2 = new MenuItem("复制2");
        menuItem2.getStyleClass().add("menu-item-ui");
        menuItem2.setGraphic(new HelpIco());
        MenuItem menuItem3 = new MenuItem("复制3");
        menuItem3.getStyleClass().add("menu-item-ui");
        menuItem3.setGraphic(new ClearIco());
        menu.getItems().addAll(menuItem, menuItem2, menuItem3);
        menuBar.getMenus().addAll(menu);
        HBox hBox = new HBox(menuBar, new Button("hello"));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        Scene scene = new Scene(new VBox(hBox), 400, 500);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
