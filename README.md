

[![](https://jitpack.io/v/xjw580/javafx-ui.svg)](https://jitpack.io/#xjw580/javafx-ui) ![GitHub](https://img.shields.io/github/license/xjw580/javafx-ui?style=flat-square)

## JavaFX-UI



![demo.png](src/main/resources/club/xiaojiawei/demo/demo.png)

> 适用于javafx的ui组件库（Javafx-based ui component library）



## 使用

1. javafx-ui在maven中央仓库不可用，但可以添加jitpack存储库使用
   
   ```xml
   <repository>
       <id>jitpack.io</id>
       <url>https://jitpack.io</url>
   </repository>
   ```
   
2. 添加最新版本的javafx-ui，就像添加一个正常的依赖一样，例如:

   ```xml
   <dependency>
       <groupId>com.github.xjw580</groupId>
       <artifactId>javafx-ui</artifactId>
       <version>0.2.8</version>
   </dependency>
   ```

   



## 组件和样式查看

- 拉取项目到本地启动 [DemoApplication](src/main/java/club/xiaojiawei/demo/DemoApplication.java) 类
-  查看 [Wiki](https://github.com/xjw580/javafx-ui/wiki)



## 文本分隔线

`LabelSeparator` 是支持文本和图形节点的水平分隔线，图文整体默认居中，支持 `HPos.LEFT`、`HPos.CENTER`、`HPos.RIGHT`。

```java
import club.xiaojiawei.controls.LabelSeparator;
import club.xiaojiawei.controls.ico.SettingsIco;
import javafx.geometry.HPos;
import javafx.scene.control.ContentDisplay;

LabelSeparator separator = new LabelSeparator("分组标题");
separator.setTextAlignment(HPos.LEFT);
separator.setGraphic(new SettingsIco());
separator.setContentDisplay(ContentDisplay.RIGHT);
separator.setGraphicTextGap(8);
```

也可以在 FXML 中使用：

```xml
<?import club.xiaojiawei.controls.LabelSeparator?>
<?import club.xiaojiawei.controls.ico.SettingsIco?>
<LabelSeparator text="分组标题" textAlignment="RIGHT" contentDisplay="RIGHT" graphicTextGap="8">
    <graphic>
        <SettingsIco/>
    </graphic>
</LabelSeparator>
```

`textProperty()`、`graphicProperty()` 和 `textAlignmentProperty()` 支持属性绑定。
`contentDisplay` 使用 JavaFX 的 `ContentDisplay`，支持 `LEFT`（默认）、`RIGHT`、`TOP`、`BOTTOM`、`CENTER`、`TEXT_ONLY` 和 `GRAPHIC_ONLY`。
`graphicTextGap` 设置图文间距，默认 4 像素；这两个属性也支持绑定及 CSS 的 `-fx-content-display`、`-fx-graphic-text-gap`。
`textAlignment` 控制图文整体在分隔线上的位置，与图文内部排列无关。
文本为空时可单独显示图形，没有可见图文内容时显示完整横线，空间不足时自动省略文本。
样式可通过 `.label-separator-ui > .label` 和 `.label-separator-ui > .separator` 设置。
运行演示程序并打开 `LabelSeparator` 页面可预览三种对齐方式。

## [更新历史](HISTORY.md)
