# SVG icon migration

This implementation was rebuilt on 2026-10-29 from javafx-ui commit
`24d50fa61683a6d8209e629191a53cfe21ef11fe`. It is a new implementation and
verification run, not a recovered copy of the earlier isolated test workspace.

## Dependency

The production dependency is `com.github.xjw580:javafx-svg`, pinned to immutable
commit `e8d64c5a7491f67e7a448a9dbf2eac594f99cf4f` through JitPack. This does not
require a `v1.0.0-rc` tag or claim that a GitHub Release has been published.
JavaFX is consistently set to 21.0.8. The JPMS module uses `club.xiaojiawei.svg`
and explicitly resolves `kotlin.stdlib`.

## Behavior

- All 74 built-in icon classes delegate loading to `AbstractIco.loadSvg`.
  Each icon has a separate JavaFX scene graph; parsed SVG documents are cached
  by the SVG library's bounded resource cache.
- The existing constructors, `color` property and FXML color strings remain.
  Color strings may still use JavaFX theme lookup tokens.
- Only the icon's generated color stylesheet is replaced. Caller stylesheets
  are preserved, including when reordered.
- Color rules use the SVG view’s reversible `-svg-fill` / `-svg-stroke`
  theme properties instead of a universal descendant selector. Clearing `color`
  restores source paints, and source `fill="none"` remains hollow. `StartIco` changes
  stroke only. `LinkDirIco` changes both folder/arrow fill and the arrow's
  original stroke, without adding an outline to the folder.
- The `SvgView` retains its natural size in smaller/larger containers, rather
  than silently rescaling because it is a resizable `Region`.
- `LinkDirIco`'s viewBox includes the out-of-bounds arrow and its stroke;
  `StartIco` includes the stroked triangle; `LockIco` removes empty side margins.

The renderer change may alter intrinsic sizing and antialiasing versus
fxsvgimage. Pixel identity and full binary/behavioral compatibility with the old
renderer are not promised. Custom `AbstractIco` subclasses which relied on the
old universal color selector should review their `getColorStyle` override.

## Reproduction

Use a full JDK 21 and Maven. First build the project:

```sh
mvn clean verify
python3 verification/verify-svg-icons.py
```

The second command resolves test-only Monocle and runs the packaged JAR on both
the classpath and JPMS module path. Pass `--desktop` to use a desktop JavaFX
session instead. `--maven`, `--settings` and `--repository` accept explicit
local build configuration. No Monocle dependency is added to the published POM.
Logs and the icon contact sheet are written under `target/svg-verification/`.

The verifier checks all 74 icons, rendering, red/blue color changes, source-paint
restoration, fixed natural sizing, shared-document/independent-node behavior,
caller stylesheet ownership, theme-token FXML, and the real `Time.fxml` control.
Source-paint checks explicitly construct each icon with `(String) null`. The
no-argument constructors are checked separately: BrainIco, NextIco, NextPageIco,
PinterIco, ScreenshotIco and TrendIco intentionally default to black; the others
default to source paints. This preserves their existing API behavior.
It also checks the three adjusted icons' painted bounds against their viewport.
The legacy Illustrator `enable-background` diagnostic is explicitly allowed: these
icon resources do not use background-input filters. All other diagnostics fail
the check.

Windows, GPU rendering, complete application interaction, and an old/new
pixel-by-pixel comparison are not covered by the software-rendered verifier.

## Verified on 2026-10-29

- Linux, Temurin 21.0.13+9, Maven 3.8.8, JavaFX 21.0.8.
- Maven resolved the pinned POM and JAR from JitPack. No local SVG source
  directory or manually installed replacement artifact was used for acceptance.
- Final `clean verify`: success, including all 33 theme-generation assertions,
  complete project compilation, and binary/source/Javadoc packaging.
- Packaged classpath consumer: 74 icons, 1,129 assertions passed.
- Packaged JPMS consumer: 74 icons, 1,129 assertions passed.
- The generated 74-icon contact sheet was visually inspected, including the
  adjusted LinkDir, Start and Lock icons.

The environment emitted font-cache and missing SLF4J-provider warnings during
headless execution; neither caused verification failure. No Windows/GPU result
or old-renderer pixel/performance equivalence is asserted.
