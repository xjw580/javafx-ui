# 常用示例

以下展示本库公开 API，在应用项目已初始化的 JavaFX 应用线程中执行。示例使用 Java；Kotlin 项目可调用同一 API，按项目语言习惯编写。

## 代码统一设置主题，样式类指定控件外观

```java
import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.enums.Theme;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

Button submit = new Button("提交");
submit.getStyleClass().addAll("btn-ui", "btn-ui-small", "btn-ui-success");
Scene scene = new Scene(new VBox(12, submit), 480, 320);
JavaFXUI.setTheme(scene, Theme.WINDOWS);
```

切换主题时对现有场景调用 `setTheme`，控件样式类保持不变。设置后续默认值使用 `JavaFXUI.setDefaultTheme(Theme.WINDOWS)`，创建场景时调用 `JavaFXUI.setTheme(scene)`；已有场景仍须显式更新。

仅在明确需要某个按钮固定外观时，才追加 `btn-ui-base` 或 `btn-ui-windows`；普通按钮跟随全局主题即可。

## 图文分隔线

```java
import club.xiaojiawei.controls.LabelSeparator;
import club.xiaojiawei.controls.ico.SettingsIco;
import javafx.geometry.HPos;
import javafx.scene.control.ContentDisplay;

LabelSeparator separator = new LabelSeparator("设置", HPos.LEFT);
separator.setGraphic(new SettingsIco());
separator.setContentDisplay(ContentDisplay.RIGHT);
separator.setGraphicTextGap(8);
```

自定义控件自行注册基础类。不要为了设置图文顺序而改用 `textAlignment`，它只控制整体对齐。

## 多列下拉框

```java
import club.xiaojiawei.controls.WideComboBox;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;

WideComboBox<String> choices = new WideComboBox<>(
        FXCollections.observableArrayList("短选项", "较长的选项内容", "第三项"));
choices.setMaxColumCount(2);
choices.setOrientation(Orientation.HORIZONTAL);
choices.setScrollValueEnabled(false);
```

## FXML 通过样式类配置控件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import club.xiaojiawei.controls.LabelSeparator?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.layout.VBox?>
<VBox xmlns:fx="http://javafx.com/fxml/1" spacing="12">
    <LabelSeparator text="操作" textAlignment="LEFT"/>
    <Button text="提交" styleClass="btn-ui, btn-ui-small, btn-ui-success"/>
</VBox>
```

应用代码加载此 FXML、创建 Scene 后，统一调用 `JavaFXUI.setTheme(scene, Theme.WINDOWS)`。页面通过 `styleClass` 指定控件样式，无需另加主题入口。
