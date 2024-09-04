package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.ico.NextIco;
import club.xiaojiawei.controls.ico.NextPageIco;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/30 16:04
 */
public class SplitPaneTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        int size = 6;
        for (int i = 0; i < size; i++) {
            Item item = new Item();
            item.setPrefHeight(100);
            splitPane.setDividerPosition(i, 1D * (i +1) / 6 );
            splitPane.getItems().add(item);
        }


        Scene scene = new Scene(new StackPane(splitPane){{setStyle("-fx-padding: 20");}}, 1000, 600);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static class Item extends HBox{

        private final ImageView imageView;

        private final TagPane tagPane;

        public Item() {
            setMinHeight(0);
            imageView = new ImageView();
            tagPane = new TagPane();

            getChildren().addAll(tagPane, imageView);

        }
    }

    static class TagPane extends HBox{

        private final GridPane contentPane = new GridPane();
        private final AnchorPane scalePane = new AnchorPane();



        public TagPane() {
            setMinWidth(100);
            setMaxWidth(100);
            setMinHeight(50);
            setStyle("-fx-border-color: gray;-fx-border-width: 0 1 0 0");
            HBox.setHgrow(contentPane, Priority.ALWAYS);
            contentPane.getRowConstraints().addAll(
                    new RowConstraints(){{setValignment(VPos.CENTER);}},
                    new RowConstraints(){{setValignment(VPos.CENTER);}},
                    new RowConstraints(){{setValignment(VPos.CENTER);}}
            );
            contentPane.getColumnConstraints().addAll(
                    new ColumnConstraints(){{setHalignment(HPos.CENTER);}},
                    new ColumnConstraints(){{setHalignment(HPos.CENTER);}}
            );
            VBox vBox = new VBox();
            vBox.getChildren().addAll(new NextIco(), new NextPageIco());
            contentPane.add(vBox, 0, 0, 1, 1);
            contentPane.add(new Text("FP1"){{setStyle("-fx-font-size: 16;-fx-font-weight: bold");}}, 0, 1, 2, 1);
            contentPane.add(new Text("(0.0/0.0)"), 0, 2, 2, 1);

            Label label = new Label("100");
            Label label1 = new Label("50");
            Label label2 = new Label("25");
            Label label3 = new Label("10");
            Label label4 = new Label("5");
            scalePane.getChildren().addAll(label, label1, label2, label3, label4);
            AnchorPane.setRightAnchor(label, 2D);
            AnchorPane.setTopAnchor(label, 1D);

            AnchorPane.setRightAnchor(label1, 2D);
            AnchorPane.setTopAnchor(label1, 16D);

            AnchorPane.setRightAnchor(label2, 2D);
            AnchorPane.setTopAnchor(label2, 32D);

            AnchorPane.setRightAnchor(label3, 2D);
            AnchorPane.setTopAnchor(label3, 48D);

            AnchorPane.setRightAnchor(label4, 2D);
            AnchorPane.setTopAnchor(label4, 64D);

            getChildren().addAll(contentPane, scalePane);
        }


    }
}
