package club.xiaojiawei.test;

import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.controls.NumberField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/2/19 15:19
 */
public class NumberFieldTest extends Application {

    private static Throwable failure;

    public static void main(String[] args) {
        launch(args);
        if (failure != null) {
            throw new AssertionError("NumberField步长验证失败", failure);
        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        NumberField numberField = new NumberField();
        numberField.getStyleClass().add("text-field-ui");
        numberField.setMaxWidth(240);
        Label status = new Label("正在验证数值步长");
        VBox root = new VBox(20, new Label("每次增减0.25，支持按钮及上下方向键"), numberField, status);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        Scene scene = new Scene(root, 560, 300);
        JavaFXUI.addjavafxUIStylesheet(scene);
        primaryStage.setTitle("NumberField 步长验证");
        primaryStage.setScene(scene);
        primaryStage.show();
        verifyStep(numberField);
        verifyFxml();
        numberField.setDecimalCount(2);
        numberField.setStep(5);
        numberField.setText("0.00");
        numberField.increment(1, true);
        afterAnimation(numberField, () -> {
            assertValue(numberField, "1.00", "动画总增量不受自定义步长影响");
            numberField.setStep(0.25);
            numberField.decrement(0.5, true);
            afterAnimation(numberField, () -> {
                assertValue(numberField, "0.50", "动画总减量不受自定义步长影响");
                status.setText("验证通过：步长0.25");
                System.out.println("PASS: default, custom integer/decimal step, buttons, keys, range, readonly, FXML, animations");
                if (getParameters().getRaw().contains("--verify")) {
                    Platform.exit();
                }
            });
        });
    }

    private void verifyStep(NumberField field) {
        field.increment();
        assertValue(field, "1", "默认整数步长");
        field.setDecimalCount(2);
        field.increment();
        assertValue(field, "1.01", "默认小数步长");
        field.setStep(5);
        field.setDecimalCount(0);
        field.setText("10");
        ((Button) field.lookup("#add-btn")).fire();
        assertValue(field, "15", "增加按钮使用自定义步长");
        ((Button) field.lookup("#sub-btn")).fire();
        assertValue(field, "10", "减少按钮使用自定义步长");
        field.setDecimalCount(2);
        field.setStep(0.25);
        field.setText("1.00");
        field.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP, false, false, false, false));
        assertValue(field, "1.25", "上方向键使用小数步长");
        field.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN, false, false, false, false));
        assertValue(field, "1.00", "下方向键使用小数步长");
        field.increment(0.1, false);
        assertValue(field, "1.10", "显式增量保持独立");
        field.setMinValue("0");
        field.setMaxValue("2");
        field.setText("2.00");
        field.increment();
        assertValue(field, "2.00", "上界限制");
        field.setText("0.00");
        field.decrement();
        assertValue(field, "0.00", "下界限制");
        field.setEditable(false);
        field.increment();
        assertValue(field, "0.00", "只读限制");
        field.setEditable(true);
        for (double invalidStep : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            boolean rejected = false;
            try {
                field.setStep(invalidStep);
            } catch (IllegalArgumentException expected) {
                rejected = true;
            }
            if (!rejected || field.getStep() != 0.25) {
                throw new AssertionError("非法步长必须被拒绝且不能改变原配置: " + invalidStep);
            }
        }
        field.increment(0, true);
        assertValue(field, "0.00", "零增量动画");
    }

    private void verifyFxml() throws IOException {
        Parent demo = FXMLLoader.load(NumberFieldTest.class.getResource("/club/xiaojiawei/demo/tab/controls/NumberFieldTab.fxml"));
        Scene demoScene = new Scene(demo, 640, 900);
        JavaFXUI.addjavafxUIStylesheet(demoScene);
        demo.applyCss();
        demo.layout();
        var fields = demo.lookupAll(".text-field-ui");
        long configuredFields = fields.stream()
                .filter(NumberField.class::isInstance)
                .map(NumberField.class::cast)
                .filter(field -> field.getStep() == 5 || field.getStep() == 0.25)
                .count();
        if (configuredFields != 2) {
            throw new AssertionError("FXML没有正确加载两个自定义步长示例");
        }
    }

    private void afterAnimation(NumberField field, Runnable verification) {
        PauseTransition wait = new PauseTransition(Duration.millis(300));
        PauseTransition timeout = new PauseTransition(Duration.seconds(5));
        ChangeListener<String> listener = (observable, oldValue, newValue) -> wait.playFromStart();
        field.textProperty().addListener(listener);
        wait.setOnFinished(event -> {
            field.textProperty().removeListener(listener);
            timeout.stop();
            try {
                verification.run();
            } catch (Throwable error) {
                failure = error;
                Platform.exit();
            }
        });
        timeout.setOnFinished(event -> {
            field.textProperty().removeListener(listener);
            wait.stop();
            failure = new AssertionError("数值动画在5秒内未稳定");
            Platform.exit();
        });
        wait.play();
        timeout.play();
    }

    private void assertValue(NumberField field, String expected, String scenario) {
        if (new BigDecimal(field.getText()).compareTo(new BigDecimal(expected)) != 0) {
            throw new AssertionError(scenario + ": expected=" + expected + ", actual=" + field.getText());
        }
    }
}
