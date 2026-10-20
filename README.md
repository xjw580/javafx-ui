

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
- Demo 左侧“组件 → WideComboBox”展示长短选项、多列横向/纵向排列，可搜索 `WideComboBox` 快速进入；随全局主题切换。
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

## 样式源码与构建

主题样式统一维护在 `src/main/themes`。每种主题的每个组件只有一份源文件，例如：

- `src/main/themes/base/button.css`
- `src/main/themes/windows/button.css`
- `src/main/themes/shared/button.css` 保存共用修饰类。

带有 `/* @theme-variants */` 标记的文件使用普通 JavaFX CSS 语法。同一规则中列出默认选择器、强制主题类和内部主题伪类，共享一份属性声明：

```css
.btn-ui,
.btn-ui.btn-ui-windows,
.btn-ui:ui-windows {
    -fx-background-radius: 4;
}
```

构建会把普通选择器写入 `windows/button.css`，把 `*-ui-windows` / `:ui-windows` 选择器写入 `windows/explicit/button.css`；Base 同理。只对一种加载方式生效的规则仍单独保留，尤其是跨主题时清除原主题属性的规则。独立变体 `btn-ui-windows-primary` 不视为强制主题后缀。普通版和强制版保持各自规则的先后顺序；源文件中的 `@import` 只写入普通版。

执行 `mvn process-resources` 即可生成并复制运行时样式；`mvn package` 会自动执行这一步。生成目录为 `target/generated-resources/themes`，JAR 内的公开样式路径与加载 API 不变。请修改源文件，不要修改生成目录中的文件，也不要直接把合并后的源文件加载进场景。

在 IDEA 直接运行 Demo 前，先执行 Maven 的 `process-resources`；修改 CSS 后也需要重新执行，或将 IDE 构建委托给 Maven。生成器使用项目已有的 JDK 21，无需安装 Node.js、Sass 或 Python。`mvn test` 会检查选择器分离、错误输入、重复生成和过期文件清理。

## 主题与基础样式

先给场景选择一个完整主题入口。相同的 `*-ui` 控件类无需改名，切换入口后会使用对应的默认外观：

```java
import club.xiaojiawei.JavaFXUI;
import club.xiaojiawei.enums.Theme;

JavaFXUI.setTheme(scene, Theme.BASE);
JavaFXUI.setTheme(scene, Theme.WINDOWS);
```

在创建界面前设置默认主题，后续可直接加载默认值：

```java
JavaFXUI.setDefaultTheme(Theme.WINDOWS);
JavaFXUI.setTheme(scene);
```

默认值初始为 `Theme.BASE`，修改默认值不自动切换已有场景。`getDefaultTheme()` 返回配置值，`getTheme(scene)` / `getTheme(node)` 查询实际主题。自定义控件加入尚未加载本库主题的场景时，会加载默认主题。

`Modal`、日期/时间/数字选择弹窗、表格筛选弹窗和 `MultiFileChooser` 每次显示时采用所属节点或窗口的主题；无所属节点的 `Modal` 使用默认主题。

`setTheme` 会替换场景中已有的本库主题入口并清理重复项，其他业务样式表及其相对顺序保持不变。重复设置同一主题不会重复添加样式表。

需要自行管理样式表列表时，可取得明确的主题 URL：

```java
scene.getStylesheets().add(JavaFXUI.themeStylesheet(Theme.WINDOWS));
```

FXML 也可以直接加载公开入口：

```xml
<VBox stylesheets="@/club/xiaojiawei/controls/css/themes/base.css"/>
```

两个公开入口分别为：

- `/club/xiaojiawei/controls/css/themes/base.css`
- `/club/xiaojiawei/controls/css/themes/windows.css`

这是加载 API 的破坏性变更：旧加载方法、旧主题枚举和旧主入口不再保留，请迁移到 `setTheme`、`themeStylesheet` 与上述两个入口。

Base 保留原有视觉风格与语义修饰类。尺寸计算沿用 2.52.2 的方式，仅校准各档位数值；不要求与旧版逐像素同尺寸。两个入口分别加载各自的默认规则，避免不同主题的状态样式相互覆盖。

默认情况下只写基础样式类，已有尺寸类和语义类继续组合使用：

```xml
<Button styleClass="btn-ui, btn-ui-small, btn-ui-success"/>
<TextField styleClass="text-field-ui"/>
<TableView styleClass="table-view-ui"/>
```

Windows 采用现代浅色外观：白色表面、浅灰细边框、小圆角、轻阴影和蓝色交互反馈。输入框聚焦与标签页选中使用蓝色底边，列表和表格使用浅蓝选中背景；普通入口与 `*-ui-windows` 强制样式同步更新。

Button、Label、TextField/PasswordField、ToggleButton 继续通过内容、padding 与实际边框计算尺寸；Base Button/ToggleButton 不添加占位边框。TextField 的 padding 保持四边相同。ComboBox 沿用内部单元格和编辑器的首选高度，外层 padding 为 0；默认 12px 字号下，各控件同档位对齐，分数缩放下允许一个物理像素的取整差异。字号放大后，内容撑高的控件与固定首选高度的 ComboBox 仍按各自原有逻辑布局。

这些控件及 CheckBox、RadioButton 均支持对应的 tiny/small/big 类，需配合基础类使用。CheckBox/RadioButton 不增加外层 padding；尺寸类仅改变指示器，不改变文字字号。CheckBox 默认档保留原生标记尺寸；勾号边长分别为 0.55em、0.75em、1.3em，方框由标记、内部间距和边框自然计算。半选横线同步缩放，选中与半选状态的方框尺寸相同。

RadioButton 支持 `radio-button-ui-tiny`、`radio-button-ui-small`、`radio-button-ui-big`，与 `radio-button-ui` 组合使用。尺寸类同时调整圆框和圆点，不改变文字字号；同字号下，tiny/small/默认/big 四档圆框均与 CheckBox 对应方框等宽等高，完整指示器可自然撑高控件。Demo 的 RadioButton 页面提供四档示例，随全局 Base/Windows 主题切换。

控件外层不设置固定的最小、首选或最大高度。大图标和多行按钮仍可自然撑高，调用方的尺寸约束继续有效。ComboBox 的 tiny/small/默认/big 四档单元格首选高度为 21/25/31/37px；Windows 显示区扣除 2px 边框空间，编辑器首选高度也比对应档位少 2px。显示区、编辑器与弹出列表保留各自的内边距，不再通过外层 padding 模拟高度。表格等容器的整体高度不参与单行控件等高比较。

个别控件需要固定外观时，在对应基础类上追加一个主题修饰类：

```xml
<Button styleClass="btn-ui, btn-ui-base"/>
<Button styleClass="btn-ui, btn-ui-windows"/>
<TextField styleClass="text-field-ui, text-field-ui-base"/>
<TextField styleClass="text-field-ui, text-field-ui-windows"/>
```

主题修饰类必须与对应的 `*-ui` 基础类配对；`*-ui-base` 与 `*-ui-windows` 互斥，同时添加两者属于不支持的组合。`btn-ui-windows-primary`、`btn-ui-windows-default` 等既有按钮变体继续可用，它们是独立变体，不与固定主题修饰类叠加。

`Hyperlink` 是原生样式类例外：不添加附加类时由 JavaFX 自带的 `.hyperlink` 作为主题默认入口；既有 `hyperlink-ui-base`、`hyperlink-ui-windows` 后缀仍可单独使用，以固定对应外观。

自定义控件也随主题切换：输入扩展、Switch、日期/时间/时长选择器、Modal/ProgressModal、Notification、WindowBar、文件上传/选择器、表格筛选、FilterComboBox/WideComboBox、VisibleTreeView、LabelSeparator、MessageBox 和 Carousel。它们保留局部 CSS 的布局规则，颜色、边框和阴影由主题提供。

自定义控件自动注册基础类，例如 `date-ui`、`filter-field-ui`、`switch-ui`。追加 `date-ui-base` 或 `date-ui-windows` 可固定整个控件及内部子控件的主题；独立弹窗也会继承该选择。移除后缀恢复所在场景或祖先的主题，内部节点自身显式指定的主题优先。输入扩展沿用 `text-field-ui`，下拉扩展沿用 `combo-box-ui`，支持对应尺寸和语义类。

启动 `DemoApplication` 后，在左侧切换 Base/Windows 主题即可查看现有各控件页面的对应风格，无需单独的主题展示页。

## [更新历史](HISTORY.md)
