# css-theme-separation - Work Plan

## TL;DR (For humans)
**交付结果：** 控件继续使用原来的类名，加载哪个主题就显示哪种默认外观；显式主题后缀可固定个别控件的风格。

**方案：** 共享结构与两套主题外观分离，主题入口决定默认规则顺序，显式主题选择器提高优先级；每套外观声明只维护一份。

**加载约定：** 按用户最新要求，不兼容旧加载 API、枚举与公开资源入口；统一使用显式主题 API 和两个新 CSS 入口。仓库内所有调用同步迁移，不保留转发包装。

**边界：** 不重设计视觉，不增加运行时节点改名或构建工具；用户已授权执行，视觉验收由用户负责。

**工作量：** 中等偏大。**风险：** 中等，集中在复杂状态、语义修饰类及弹出菜单的样式优先级。

**建议约定：** 新增 base 后缀用于强制基础风格；显式主题与原基础类配合使用；两个默认主题入口择一，两个显式主题后缀互斥。

6 项实施任务、4 项最终检查均已完成，独立技术审查批准；视觉验收由用户负责。最终构建与组合运行时证据见 task-6-summary.md，完整打包矩阵原始日志的保存限制已如实记录。

---

> TL;DR (machine): intent=clear; review_required=false; status=completed; 主题入口分离，类名兼容，6 项实施任务和 4 项最终检查完成；视觉验收由用户负责。

## Scope
### Must have
- 两个新公开入口为 `controls/css/themes/base.css` 与 `controls/css/themes/windows.css`，分别决定无主题后缀控件的默认外观。
- 当前主入口覆盖的全部基础控件和通用样式随入口切换，现有 `*-ui`、尺寸类与语义类无需改名。
- 显式主题为修饰类：`btn-ui btn-ui-windows` 强制 Windows；新增 `btn-ui btn-ui-base` 强制基础。两种入口均支持两种强制风格。
- Hyperlink、ScrollBar 保留 JavaFX 自动添加的原生基础类，因此其原有单后缀用法继续有效。
- 同一控件同时带 base/windows 显式主题类属于不支持的组合，文档明确禁止；纯 CSS 不增加假报错或默认兜底。
- 保留已有 `btn-ui-windows-primary/default` 等类及现有使用组合；兼容清单以资源中实际选择器为准，不能批量替换后缀。
- 统一 API 为 `JavaFXUI.setTheme(Scene, Theme)` 与 `JavaFXUI.themeStylesheet(Theme)`；Theme 只包含 BASE、WINDOWS，映射显式资源路径，不用枚举名拼接路径。
- 用 Theme 替换原 StylesheetEnum；移除旧加载方法 addjavafxUIStylesheet、javafxUIStylesheet、stylesheet、addStylesheet 及旧单控件公开加载方式，仓库内引用全部迁移。JavaFXUI 的日志、演示等无关 API 保留。
- 两个完整默认入口择一加载；setTheme 只替换本库已知主题 URL，清除重复主题入口，重复设置同一主题不重复添加；保持业务样式表及其相对顺序，不修改节点类名。themeStylesheet 支持调用方直接加载 URL/FXML，不要求调用 setTheme 才生效。
### Must NOT have (guardrails, anti-slop, scope boundaries)
- 不重设计颜色、控件布局、尺寸或语义变体；保留最近修复的 Windows 聚焦尺寸稳定行为。
- 不使用操作系统检测、运行时遍历节点改类名、全局用户代理样式替换、CSS 生成器、新外部依赖或 Kotlin 构建链。
- 不把独立复合组件整套换肤加入范围：`controls/css` 下 calendar、notification 等独立样式保持现有契约。
- 不把加载基础主题当作缺失 Windows 规则的补偿；两套外观必须完整定义彼此不同的属性和状态。
- 不擅自改变 `-fx-effect: null`：前轮 JavaFX 实测已确认直接 null 可用，而查找变量解析出的 none 曾报错；迁移保持已验证写法。
- 不保留旧加载方法的 deprecated 转发、旧入口 CSS 包装、旧枚举别名或按控件公开加载兼容层；样式类名兼容要求仍然有效。

## Verification strategy
技术验证由代理执行，视觉验收按用户要求由用户完成。
- Test decision: tests-after；不新增测试框架依赖，使用独立 JavaFX 运行时探针。现有 test 包是演示程序，不视为自动测试套件。
- 使用 IDEA MCP build_project、get_file_problems、execute_terminal_command；探针在 FX 线程应用 CSS、布局并读取真实属性，失败必须非零退出。
- 每个控件覆盖：基础入口 + 默认类、Windows 入口 + 默认类、基础入口 + 强制 Windows、Windows 入口 + 强制基础，以及同主题强制。
- 检查普通、悬停、聚焦、按下、选中、禁用；覆盖实际存在的 tiny/small/big、normal/success/warn/error 等修饰类。
- 检查颜色、边框、背景、圆角、效果、字体、padding、内部 skin 节点及弹出内容；两种入口下同一强制主题的结果须相等。
- 复用聚焦布局探针思路：验证首选尺寸、实际尺寸、insets、相邻控件位置，保留 16 种组合的回归覆盖。
- 所有新证据放 `.omo/evidence/css-theme-separation/`，记录具体命令、输出、通过数量及 CSS 解析错误；不宣称视觉验收完成。

## Execution strategy
### Parallel execution waves
优先级契约验证通过后，API/调用迁移与 CSS 并行。CSS 初次拆分后发现状态还原缺口，按表单、数据控件、菜单、其他基础控件划分互不重叠的文件所有权并行修正；共享层与入口由一个负责人集成。最终构建由 API 负责人独占执行，等待所有 CSS 组就绪，避免旧产物干扰。演示已通过独立运行验证，最终回归继续按依赖推进。

实现结构：
- `controls/css/themes/shared/`：两种外观确实一致的结构、尺寸与公共 token；视觉属性有差异时留在主题层。
- `controls/css/themes/base/`：基础外观；每条默认规则同时声明强制基础选择器。
- `controls/css/themes/windows/`：Windows 外观；每条默认规则同时声明强制 Windows 选择器。
- 基础入口依次导入 shared → windows → base；Windows 入口依次导入 shared → base → windows。
- 两个入口均包含显式主题支持，所以都会引用两套主题定义；它们不是独立精简包。用户只需选择一个入口。
- 例：base 中 `.btn-ui, .btn-ui.btn-ui-base { ... }`；windows 中 `.btn-ui, .btn-ui.btn-ui-windows { ... }`。外观声明不复制，仅增加选择器别名。
- 伪类和后代规则同样成对声明；两个主题对应的默认选择器必须同等优先级，显式规则保持高一层；不可仅给普通态增加别名。
- 清理主题状态中不必要的 !important；必须保留的尺寸/语义优先级单独列入契约。不要靠增加更多 !important 弥补遗漏规则。
- 以两主题属性集合的并集审查每个控件：另一主题独有的阴影、border-insets、背景层等必须有明确还原值，避免前一个主题漏进后一个主题。

### Dependency matrix
| Todo | Depends on | Blocks | Can parallelize with |
| --- | --- | --- | --- |
| 1 | 无 | 2–6 | 无 |
| 2 | 1 | 3–6 | 无 |
| 3 | 2 | 4–6 | 无 |
| 4 | 1；集成验证等待 3 | 5–6 | 2–3 |
| 5 | 4 的 API 已实现；集成验证等待 3–4 | 6 | 2–3 |
| 6 | 5 | F1–F4 | 无 |

## Todos
> Implementation + Test = ONE todo. Never separate.
<!-- APPEND TASK BATCHES BELOW THIS LINE WITH edit/apply_patch - never rewrite the headers above. -->
- [x] 1. 固化类名、属性与状态清单，先验证优先级方案
  References: `common/javafx-ui.css:1`、全部被其导入的 CSS、`common/button.css`、`common/combo-box.css`、`common/windows-menu.css`；路径均相对 `src/main/resources/club/xiaojiawei/controls/css/`。
  Work: 盘点每个主控件、子节点、尺寸/颜色/风格修饰类、伪类、!important 与依赖；用输入框、按钮、可编辑下拉框的隔离样式验证成对选择器方案，再迁移全量。
  Acceptance/QA: IDEA terminal 执行 JavaFX 探针；四种核心组合的真实属性与预期一致。失败用例包括聚焦后的反向强制、带 small 与 normal 的按钮、同场景混用两主题，须没有主题串用。
  Evidence: `.omo/evidence/css-theme-separation/task-1-contract.md` 与 `task-1-priority.log`。
  Commit: 与任务 2–4 共同提交，避免暴露未完成资源结构。

- [x] 2. 分离共享结构、公共 token 和主题外观
  References: 全部 `common/*.css`；`common/common.css:1` 的 token 与通用类；`DESIGN.md`。
  Work: 建立 shared/base/windows 目录，按控件保留可定位的文件名；原有两套 token 名保持稳定。尺寸规则只在确实共用时上移，主题原有间距差异保持原值。
  Acceptance/QA: IDEA build_project 成功；get_file_problems 记录新错误和既有误报；运行时加载所有新资源。失败场景检查缺失 import、循环 import、未解析 token；任一出现阻止继续。
  Evidence: `.omo/evidence/css-theme-separation/task-2-imports.log`。
  Commit: 与任务 3–4 共同提交。

- [x] 3. 全量迁移默认规则与强制主题别名
  References: 任务 1 的完整清单；原 `windows-menu.css`、`windows-scroll-bar.css`、`combo-box.css`、`table-view.css`、`tab-pane.css`；提交 `0f20f2b` 的聚焦修复。
  Work: 每个主题外观块添加默认及显式选择器；对子控件、编辑器、表格单元格、菜单项、内部滚动条、所有状态执行同样规则。共享菜单/滚动条放各自主题内，避免入口循环。显式子控件主题须能覆盖所属控件的普通子节点规则。
  Acceptance/QA: 每种基础控件通过四组合矩阵；实际打开 ComboBox/ContextMenu/Menu 后检查 popup 内节点。反例覆盖 Windows 下强制基础的圆角/阴影还原、基础下强制 Windows 的阴影清除、禁用及选中优先级；聚焦尺寸探针全通过。
  Evidence: `.omo/evidence/css-theme-separation/task-3-matrix.log`、`task-3-popup.log`、`task-3-focus.log`。
  Commit: 与任务 4 一并提交 `refactor: 分离基础与 Windows 主题样式`。

- [x] 4. 装配两个新入口并统一主题加载 API
  References: `src/main/java/club/xiaojiawei/JavaFXUI.java:38`、`:46`、`:55`、`:71`；`src/main/java/club/xiaojiawei/enums/StylesheetEnum.java`；`common/javafx-ui.css`。
  Work: 创建 themes/base.css、themes/windows.css，Theme 枚举替代 StylesheetEnum（符号重命名使用 IDEA rename_refactoring）。统一实现 setTheme 与 themeStylesheet，旧加载方法及旧入口不设包装。检索并迁移仓库内全部 Java/FXML/CSS 调用，包括 demo、test、Modal、MultiFileChooser 等创建新 Scene 的位置和硬编码资源路径。原基础加载调用明确改为 BASE，主题演示选择 WINDOWS；独立复合组件仅调整必需的公共样式导入路径，不改变其外观。旧 common 文件在引用迁移且无内部用途后删除；不删除 date-time-common 等仍有内部用途的非主入口资源。
  Acceptance/QA: 两个新 API 与直接 URL/FXML 加载均成功，所有仓库内部调用可编译；加载入口不再出现旧方法/枚举的有效引用。往返切换与重复设置后只有一个本库主题入口，节点类名、业务 CSS 及其相对顺序不变，强制主题保持一致。明确检查资源缺失和 null 主题会失败，不兜底到 BASE。检查独立窗口仍可正常加载原基础外观。
  Evidence: `.omo/evidence/css-theme-separation/task-4-api.log`。
  Commit: 完成任务 2–4 后统一中文提交。

- [x] 5. 更新演示与使用说明
  References: `src/main/java/club/xiaojiawei/demo/DemoApplication.java`、`src/main/resources/club/xiaojiawei/demo/tab/style/WindowsTab.fxml`、`README.md`、`DESIGN.md`。
  Work: 演示中用同一批不带主题后缀的控件切换两种入口，另设强制基础/Windows 对照组；保留显式后缀示例。文档改为 setTheme/themeStylesheet 新用法，明确这是加载 API 的破坏性变更，删除旧按控件加载示例；说明修饰类组合、互斥主题类与独立复合组件范围。
  Acceptance/QA: 加载演示 FXML，切换主题并读取默认组/强制组属性；失败用例检查演示中默认组仍写死 windows 后缀、主题入口重复叠加或业务 CSS 被移除。
  Evidence: `.omo/evidence/css-theme-separation/task-5-demo.log`。
  Commit: `docs: 更新主题选择与强制样式用法`。

- [x] 6. 完成运行时回归与交付准备
  References: 任务 1–5 的清单及证据；前轮 `.omo/evidence/windows-style/FocusLayoutProbe.java` 仅供验证思路参考。
  Work: 对实际打包资源运行完整矩阵与聚焦回归；验证 JavaFX21 CSS 解析、FXML、弹出窗口、不同控件尺寸。运行 git diff --check，检查最终差异与工作区。
  Acceptance/QA: IDEA 构建成功；检查数量明确，无新 CSS 解析错误；强制主题在两个入口下结果相等；普通控件随入口切换；技术缺陷修复后重跑对应失败场景。将视觉验收入口交用户，不代替用户作视觉通过结论。
  Evidence: `.omo/evidence/css-theme-separation/task-6-summary.md`。
  Commit: 若回归发现修复，独立中文 fix 提交；无代码变化不造空提交。

## Final verification wave
全部实施后检查；技术完成与用户视觉验收分别报告。
- [x] F1. 覆盖审查：任务 1 清单逐项对应两主题默认与显式状态，缺失任何基础控件不通过。
- [x] F2. 质量审查：无重复维护的外观声明、循环导入、主题兜底、无依据的 !important 或无关重构。
- [x] F3. 运行时证据审查：真实 JavaFX 控件、popup、切换与聚焦检查的组合证据获批准；完整打包矩阵原始日志被覆盖的限制保留在报告中，视觉验收留给用户。
- [x] F4. 迁移与范围审查：新 API/入口可用，旧加载调用已清除且无兼容包装；样式类名与业务 CSS 保留，独立复合组件未被整套换肤。

## Commit strategy
实现按任务中指定的中文提交执行，不提交临时探针日志，不修改或覆盖用户未提交内容。

## Success criteria
- 同一套节点类名，仅更换入口就改变默认风格；强制类在两入口下保持指定外观及交互状态。
- 主题声明各有唯一维护位置，两个入口均可独立通过 URL/FXML 使用，无运行时节点改名和新构建依赖。
- 基础外观及类名/后缀用法保留，加载 API 与资源入口完成破坏性迁移；Windows 聚焦不再造成尺寸跳动，运行时矩阵与构建通过。
- 用户获得可操作的演示入口与技术验证报告，视觉验收结论由用户给出。
