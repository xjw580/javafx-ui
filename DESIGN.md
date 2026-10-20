# JavaFX UI 主题与样式约定

## 1. 主题入口

基础控件通过两个完整入口加载主题：`controls/css/themes/base.css` 与 `controls/css/themes/windows.css`。应用只选择其中一个作为场景默认主题；运行时切换使用 `JavaFXUI.setTheme(Scene, Theme)`，直接取得 URL 使用 `JavaFXUI.themeStylesheet(Theme)`。

加载 API 已完成破坏性迁移，不保留旧加载方法、旧枚举或旧主入口兼容层。`setTheme` 只替换本库已知主题入口，保留业务样式表及其相对顺序。

## 2. 样式类契约

控件继续使用已有 `*-ui` 基础类。默认外观由当前入口决定，调用方不需要在切换主题时改节点类名；tiny/small/big 等尺寸类和 normal/success/warn/error 等语义类保持原有职责。

固定单个控件的主题时，使用“基础类 + 一个主题修饰类”，例如 `btn-ui btn-ui-base` 或 `btn-ui btn-ui-windows`。修饰类不能脱离对应基础类使用；base/windows 两个修饰类互斥，同时出现属于不支持的组合。`btn-ui-windows-primary/default` 等既有显式变体保持独立，不与主题修饰类混用。

`Hyperlink` 沿用 JavaFX 原生 `.hyperlink` 作为无后缀默认入口。既有 `hyperlink-ui-base/windows` 是兼容例外，可单独使用并固定对应主题外观。

## 3. 样式分层

每个入口依次加载公共 token、当前主题默认规则及 explicit 规则、shared 尺寸和语义修饰规则、对侧主题的 explicit 规则。base 与 windows 默认规则不在同一个入口中混用；explicit 目录只匹配强制主题后缀或内部 `ui-base/ui-windows` 伪类，不包含裸基础类或根节点规则。复合控件通过内部伪类传递强制主题，不修改调用方的样式类列表。

Base 的视觉规则与语义修饰类优先级以 2.52.2 为基线，保留根节点颜色变量继承行为；单行控件尺寸已改为两主题共用，不再要求与旧版尺寸一致。强制主题所需的跨主题属性重置仅放在 explicit 目录，避免改变普通基础类和业务 CSS 的级联关系。

ComboBox 的共享修饰规则由各主题组件文件先行导入，使编辑态规则保持旧版的后置顺序，不能再由 shared 索引重复导入。

同一主题的默认规则与显式规则通过运行时对照保持一致；公共颜色和语义值集中维护，显式规则另外重置对侧主题的属性。伪类、内部 skin 节点、弹出内容和滚动条必须遵循相同的主题选择关系，避免只切换控件普通态。

Base 的 TableView 继承根节点自定义颜色；Windows 默认及 TableView 强制主题沿用控件处的主题色板，优先于根节点的同名 lookup。因此主题等价对照以未叠加业务覆盖的控件为准，默认 Base 的业务覆盖行为另与 2.52.2 对照。

## 4. 视觉与布局

基础主题保留原有圆角、阴影与配色。Windows 主题采用现代浅色外观：白色表面、浅灰细边框、小圆角、轻阴影与蓝色交互反馈，不随操作系统自动切换。两主题继承 JavaFX 与调用方字体，不强制字形。

Base 与 Windows 的单行控件几何规则集中在 shared/control-size.css：同字体、普通单行内容和同档位时，按钮、标签、输入框、可编辑/不可编辑下拉框、切换按钮等高。四档上下 padding 为 2/4/7/10，统一预留 1px 边框空间。主题组件和 explicit 规则仅保留外观，不重复覆盖这些控件的 padding、边框宽度和边框间距。ComboBox 的显示单元格、编辑器和箭头不重复增加上下间距，显示内容不使用固定高度。

外层 min/pref/max height 保持 JavaFX 的内容计算方式。大图标、ContentDisplay.TOP、多行文本和复合 graphic 可自然撑高，代码或 FXML 中设置的首选尺寸继续有效。CheckBox/RadioButton 的内部标记按字体大小缩放；其内部尺寸不会变成控件外层的高度限制。表格等容器的整体高度不参与单行等高比较。

CheckBox 保留原生 Skin，默认档沿用原生标记尺寸。tiny/small/big 通过 shared/selection-size.css 将内部 mark 的边长设为 0.55em、0.75em、1.3em，不修改字体或外层间距；方框仍由标记、内部间距与边框自然计算。尺寸档的半选横线在相同标记布局内缩放绘制，避免状态切换改变方框尺寸。CSS 字号变化时按 em 缩放，完整指示器可撑高控件，不强行实现跨控件等高。

RadioButton 与 CheckBox 共用 shared/selection-size.css 的尺寸规则：tiny/small/默认/big 四档圆框与对应方框等宽等高，由相同的内部标记尺寸、间距和边框自然计算，不再单独限制圆框直径。默认档与 CheckBox 保持相同的标记尺寸；指示器内部使用对称间距，子节点允许小数坐标居中，避免圆点因不对称内边距或像素取整偏离圆框中心。尺寸类不改文字字号和外层间距，选中、悬停、聚焦及禁用时尺寸一致，完整指示器允许自然撑高整行。

Windows 输入框使用蓝色底边、标签页使用蓝色选中指示、列表与表格使用浅蓝选中背景和左侧强调线；这些反馈通过绘制层实现。聚焦不使用外扩光晕。Windows 聚焦前后保持边框宽度、边框间距和内边距一致，避免控件尺寸或内容位置跳动；禁用、选中、悬停、按下和聚焦状态均由当前或显式主题完整定义。主题不缩放或裁切内容以满足固定高度。

## 5. 范围与验证

主题覆盖基础控件和自定义复合控件：输入扩展、Switch、日期/时间/时长及其选择器、Modal/ProgressModal、Notification、WindowBar、文件上传/选择器、表格筛选、下拉/树扩展、LabelSeparator、MessageBox、Carousel。局部 CSS 保留布局与尺寸，外观迁移到各主题的 custom-controls.css。

`JavaFXUI.setDefaultTheme(Theme)` 设置新场景和无所属节点弹窗的默认主题，初始为 Base，不自动修改已有场景。`setTheme(Scene)` 使用该默认值，指定场景仍可使用 `setTheme(Scene, Theme)`。自定义控件通过 `initializeTheme` 注册基础类；场景没有主题入口时自动加载默认值。组件后缀在局部 Parent 加载主题入口，同时以内部伪类覆盖标准子控件的交互状态；动态子节点和 skin 节点同样受控，嵌套显式主题优先，移除后缀清理局部主题。

独立窗口和 Popup 每次显示时通过所属节点解析主题，包含祖先的显式后缀与局部入口；重复打开会跟随所属场景的主题变化。`Modal`、`MultiFileChooser` 和组件 FXML 不再写死 Base。

演示程序左侧主题选择器切换同一 Scene，各控件在原有展示页中随主题更新，不设置独立 Windows 展示页。技术验证覆盖 CSS 解析、FXML 加载、交互状态、弹出内容和聚焦布局；视觉验收由用户完成。
