package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.enums.Theme;
import club.xiaojiawei.controls.WideComboBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.util.List;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/23 21:14
 */
public class WideComboBoxTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        WideComboBox<String> wideComboBox = new WideComboBox<>();
        wideComboBox.getStyleClass().addAll("combo-box-ui", "combo-box-ui-normal");
        wideComboBox.getItems().addAll("短", "中等长度", "这是下拉列表中最长的选项");
        Label widthLabel = new Label();
        widthLabel.textProperty().bind(wideComboBox.widthProperty().asString("控件宽度：%.0f"));
        VBox root = new VBox(20, new Label("切换长短选项，控件宽度应保持不变"), wideComboBox, widthLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        Scene scene = new Scene(root, 640, 360);
        JavaFXUI.setTheme(scene, Theme.BASE);
        primaryStage.setTitle("WideComboBox 宽度与列表更新验证");
        primaryStage.setScene(scene);
        primaryStage.show();
        verifyWidths(wideComboBox, root);
        verifyItemsTracking(root);
        if (getParameters().getRaw().contains("--verify")) {
            Platform.exit();
        }
    }

    private void verifyWidths(WideComboBox<String> comboBox, VBox root) {
        double initialWidth = layoutWidth(comboBox, root);
        for (String item : comboBox.getItems()) {
            comboBox.getSelectionModel().select(item);
            assertWidth(initialWidth, layoutWidth(comboBox, root), "切换选中项");
        }
        comboBox.getSelectionModel().selectFirst();
        for (int i = 0; i < 20; i++) {
            comboBox.requestLayout();
            assertWidth(initialWidth, layoutWidth(comboBox, root), "重复布局");
        }

        String longerItem = "新增一个比原来所有选项都更长的选项，用于验证重新测量";
        comboBox.getItems().add(longerItem);
        double expandedWidth = layoutWidth(comboBox, root);
        if (expandedWidth <= initialWidth) {
            throw new AssertionError("新增最长项后宽度没有增加");
        }
        comboBox.getItems().remove(longerItem);
        assertWidth(initialWidth, layoutWidth(comboBox, root), "移除最长项");

        ObservableList<String> originalItems = comboBox.getItems();
        comboBox.setItems(FXCollections.observableArrayList("短", longerItem));
        assertWidth(expandedWidth, layoutWidth(comboBox, root), "替换列表");
        comboBox.setItems(originalItems);
        assertWidth(initialWidth, layoutWidth(comboBox, root), "恢复列表");

        StringConverter<String> originalConverter = comboBox.getConverter();
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(String value) {
                return value == null ? "" : "转换后的选项：" + value;
            }

            @Override
            public String fromString(String value) {
                return value;
            }
        });
        double convertedWidth = layoutWidth(comboBox, root);
        if (convertedWidth <= initialWidth) {
            throw new AssertionError("修改转换器后宽度没有增加");
        }
        comboBox.getSelectionModel().selectLast();
        assertWidth(convertedWidth, layoutWidth(comboBox, root), "转换后切换选中项");
        comboBox.setConverter(originalConverter);
        comboBox.getSelectionModel().selectFirst();

        comboBox.setStyle("-fx-font-size: 24px;");
        double largeFontWidth = layoutWidth(comboBox, root);
        if (largeFontWidth <= initialWidth) {
            throw new AssertionError("增大字体后宽度没有增加");
        }
        comboBox.getSelectionModel().selectLast();
        assertWidth(largeFontWidth, layoutWidth(comboBox, root), "大字体下切换选中项");
        comboBox.setStyle("");
        comboBox.getSelectionModel().selectFirst();
        assertWidth(initialWidth, layoutWidth(comboBox, root), "恢复样式");
        System.out.printf("PASS: initial=%.1f, expanded=%.1f, converted=%.1f, largeFont=%.1f%n",
                initialWidth, expandedWidth, convertedWidth, largeFontWidth);
    }

    private void verifyItemsTracking(VBox root) {
        WideComboBox<String> comboBox = new WideComboBox<>();
        root.getChildren().add(comboBox);
        try {
            root.applyCss();
            root.layout();
            GridPane popup = popupContent(comboBox);
            ObservableList<String> originalItems = comboBox.getItems();
            ObservableList<String> replacement = FXCollections.observableArrayList();
            comboBox.setItems(replacement);
            replacement.addAll("趋势组A", "趋势组B");
            assertPopupItems(popup, List.of("趋势组A", "趋势组B"), "等值空列表替换后添加项目");

            var popupNodes = List.copyOf(popup.getChildren());
            originalItems.add("旧列表项目");
            if (!popupNodes.equals(popup.getChildren())) {
                throw new AssertionError("替换后旧列表仍在触发弹出列表重绘");
            }

            ObservableList<String> equalItems = FXCollections.observableArrayList(replacement);
            comboBox.setItems(equalItems);
            equalItems.setAll("趋势组C", "趋势组D");
            assertPopupItems(popup, List.of("趋势组C", "趋势组D"), "等值非空列表替换后修改项目");
            popupNodes = List.copyOf(popup.getChildren());
            replacement.clear();
            if (!popupNodes.equals(popup.getChildren())) {
                throw new AssertionError("等值非空列表替换后旧列表监听未解除");
            }

            equalItems.setAll("趋势组A", "趋势组B");
            assertPopupItems(popup, List.of("趋势组A", "趋势组B"), "切回原协议项目");
            comboBox.setItems(null);
            assertPopupItems(popup, List.of(), "清空数据源");
            ObservableList<String> restoredItems = FXCollections.observableArrayList();
            comboBox.setItems(restoredItems);
            restoredItems.add("恢复项目");
            assertPopupItems(popup, List.of("恢复项目"), "从空数据源恢复");

            comboBox.setSkin(null);
            popupNodes = List.copyOf(popup.getChildren());
            restoredItems.add("销毁后添加");
            comboBox.setItems(FXCollections.observableArrayList("销毁后替换"));
            if (!popupNodes.equals(popup.getChildren())) {
                throw new AssertionError("皮肤销毁后列表监听未解除");
            }
            System.out.println("PASS: 数据源替换、协议切换和监听清理");
        } finally {
            comboBox.hide();
            comboBox.setSkin(null);
            root.getChildren().remove(comboBox);
        }
    }

    private GridPane popupContent(WideComboBox<String> comboBox) {
        comboBox.show();
        try {
            return Window.getWindows().stream()
                    .filter(window -> window instanceof PopupControl popup
                                      && popup.getOwnerWindow() == comboBox.getScene().getWindow())
                    .map(window -> window.getScene().getRoot().lookup(".grid-pane"))
                    .filter(GridPane.class::isInstance)
                    .map(GridPane.class::cast)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("未找到趋势组弹出列表"));
        } finally {
            comboBox.hide();
        }
    }

    private void assertPopupItems(GridPane popup, List<String> expected, String scenario) {
        List<String> actual = popup.getChildren().stream()
                .map(node -> ((Label) ((StackPane) node).getChildren().getFirst()).getText())
                .toList();
        if (!expected.equals(actual)) {
            throw new AssertionError(scenario + ": expected=" + expected + ", actual=" + actual);
        }
    }

    private double layoutWidth(WideComboBox<String> comboBox, VBox root) {
        root.applyCss();
        root.layout();
        return comboBox.getWidth();
    }

    private void assertWidth(double expected, double actual, String scenario) {
        if (Math.abs(expected - actual) > 0.01) {
            throw new AssertionError(scenario + ": expected=" + expected + ", actual=" + actual);
        }
    }
}
