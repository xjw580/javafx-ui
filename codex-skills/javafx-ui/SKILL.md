---
name: javafx-ui
description: 在依赖 javafx-ui（club.xiaojiawei）的项目中使用本库控件、图标及 Base/Windows 主题编写、修改、解释界面。指导公开 API 调用、主题加载、样式类组合及 FXML 集成。
---

# javafx-ui

面向将 javafx-ui 作为依赖的应用项目。先确认项目实际使用的版本，再选择本库公开 API。参考文档基于 3.4.1；使用其他版本时，以该版本提供的 API 为准。

## 使用规则

- 按应用项目现有语言和界面组织方式使用本库；Java、Kotlin 与 FXML 均以相同的公开控件 API 和样式约定为准。
- 优先复用 `club.xiaojiawei.controls` 和 `club.xiaojiawei.controls.ico` 中已有组件。先查公开构造器、属性及继承方法，再编写调用。
- 未确认的方法、样式类或资源路径不能猜测。查项目实际解析的依赖及其源码；无法确认时说明缺少的信息，不添加旧 API 探测、异常吞并或替代实现兜底。
- 通过控件公开属性、事件与样式类完成配置；不要求应用项目具有本库源码目录、Demo 或构建工具。

## 主题与样式

- 默认通过样式类指定控件外观：代码使用 `getStyleClass().addAll(...)`，FXML 使用 `styleClass`。已有样式类能表达的尺寸、语义和外观优先组合使用，不重复写内联 CSS。
- 全局主题通常由应用初始化代码统一管理：使用 `JavaFXUI.setTheme(scene, Theme.BASE/WINDOWS)` 设置场景主题。多场景应用在创建各场景时应用统一选择；默认值使用 `setDefaultTheme`，它不自动切换已有场景。
- FXML 默认只声明控件和样式类，不在页面或控件上重复加载完整主题 CSS。仅在明确需要自行管理样式表时使用 `JavaFXUI.themeStylesheet(theme)`，同一场景不要叠加两个完整主题入口。
- 原生控件需要对应基础类，例如 `btn-ui`、`text-field-ui`；尺寸、语义和强制主题类在基础类上组合。自定义控件通常自行注册基础类，以构造逻辑为准。
- 控件默认跟随全局主题；仅在明确要求局部固定外观时追加强制主题类。同一控件的 `*-ui-base` 与 `*-ui-windows` 互斥。`btn-ui-windows-primary` 等独立变体不是强制主题后缀；Hyperlink 的原生入口和后缀有特殊规则，见 API 参考。
- 使用本库弹窗时，按构造器要求传入所属节点或窗口，使弹窗采用正确主题；无所属节点的 Modal 使用默认主题。

## 按需参考

- 选择控件、查询方法或样式类时读 [API 参考](references/api-reference.md)。
- 需要主题、FXML 或控件组合用法时读 [示例](references/examples.md)。
- 参考中未列出的方法，继续查询对应版本的公开 API；清单不是完整 API 集合。
