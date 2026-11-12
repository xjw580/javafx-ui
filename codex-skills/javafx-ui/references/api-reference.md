# 公开 API 与样式索引

以下基于 javafx-ui 3.4.1；不是完整 API 清单。先确认消费项目版本，未列出的方法继续查源码，不凭名称推断。

## 主题入口

入口类：`club.xiaojiawei.JavaFXUI`；枚举：`club.xiaojiawei.enums.Theme`。

通常在应用初始化代码中为场景设置统一主题，控件通过样式类选择外观并跟随主题。FXML 页面无需重复声明主题样式表。

| API | 含义与注意事项 |
| --- | --- |
| `setTheme(Scene, Theme)` | 替换已有本库主题入口并去重，保留其他业务样式表的相对顺序 |
| `setTheme(Scene)` | 加载当前默认主题 |
| `setDefaultTheme(Theme)` / `getDefaultTheme()` | 配置默认主题，初始为 `BASE`，不更新已有场景 |
| `getTheme(Scene)` / `getTheme(Node)` | 查询主题；节点查询考虑自身及祖先的显式后缀和局部入口 |
| `themeStylesheet(Theme)` | 返回完整主题入口 URL |

仅在明确需要手动管理主题样式表时参考以下公开资源路径；常规调用使用上面的 `setTheme`：

- `/club/xiaojiawei/controls/css/themes/base.css`
- `/club/xiaojiawei/controls/css/themes/windows.css`

## 常见样式组合

| 控件 | 基础类 | 可选组合 |
| --- | --- | --- |
| Button | `btn-ui` | `btn-ui-small`、`btn-ui-success`、`btn-ui-windows` 等 |
| TextField / PasswordField | `text-field-ui` | 对应 tiny/small/big 尺寸类、base/windows 主题类 |
| ComboBox | `combo-box-ui` | 对应 tiny/small/big 尺寸类、base/windows 主题类 |
| MenuButton | `menu-btn-ui` | tiny/small/big；main/normal/success/warn/error；base/windows |
| RadioButton | `radio-button-ui` | tiny/small/big 改变指示器大小，不改变文字字号 |
| TableView | `table-view-ui` | 具体变体查依赖版本提供的样式表 |
| LabelSeparator | `label-separator-ui`（自动注册） | 子节点 `.label`、`.separator` |

表中简写后缀需拼接到基础类，例如 `menu-btn-ui-small`。不要将一种控件的后缀推广到所有控件。
普通控件只组合基础类、尺寸类和语义类。明确需要固定局部主题时，强制主题类与基础类配对，并且 base/windows 只能选一个。
Hyperlink 不加附加类时由原生 `.hyperlink` 提供主题入口；`hyperlink-ui-base`、`hyperlink-ui-windows` 可单独使用。
`btn-ui-windows-primary` 等独立变体不与强制主题类叠加。

## 控件 API 提示

控件包为 `club.xiaojiawei.controls`；图标包为 `club.xiaojiawei.controls.ico`；工厂包为 `club.xiaojiawei.factory`。

| 类型 | 已确认 API / 查找提示 |
| --- | --- |
| `LabelSeparator` | 构造器 `()`、`(String)`、`(String, HPos)`；text、graphic、textAlignment、contentDisplay、graphicTextGap 各有 getter/setter/property |
| `WideComboBox<T>` | 继承 `ComboBox<T>`；构造器 `()`、`(ObservableList<T>)`；`setMaxColumCount(int)`、`setOrientation(Orientation)`、`setScrollValueEnabled(boolean)`、`setEnlargeEnabled(boolean)` |
| `Modal` | `Modal(Parent, Node)` 等多个重载；`show()`、`showAndWait()`、`close()`、`showingProperty()`、`getWindow()`；它不是 Node |
| `AbstractIco` | 位于 `controls/ico`，继承 StackPane；`setColor(String)`、`colorProperty()`；具体图标如 `SettingsIco` |
| `NotificationFactory<T>` | 位于 `factory`；`ofNew()`、`ofNew(NotificationTypeEnum, String, T)` 等重载；创建与显示步骤分别核对源码 |

`WideComboBox` 的公开拼写确实是 **Colum**，不能自行改成 Column。
`LabelSeparator.textAlignment` 使用 `HPos`，控制图文整体位置；`ContentDisplay` 控制图文内部排列。
`LabelSeparator` 的内容显示方式及间距支持 CSS `-fx-content-display`、`-fx-graphic-text-gap`。
