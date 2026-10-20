package club.xiaojiawei;

import club.xiaojiawei.demo.DemoApplication;
import club.xiaojiawei.enums.Theme;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Marker;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 获取或添加样式表路径
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/10/30 0:03
 */
@SuppressWarnings("all")
public class JavaFXUI {

    private static volatile Theme defaultTheme = Theme.BASE;
    private static final Object THEME_SCOPE_KEY = new Object();

    /** 设置新场景和无所属窗口弹窗使用的默认主题，不覆盖已显式设置的场景。 */
    public static void setDefaultTheme(Theme theme) {
        defaultTheme = Objects.requireNonNull(theme, "theme must not be null");
    }

    public static Theme getDefaultTheme() {
        return defaultTheme;
    }

    @Getter
    @Setter
    private static Marker logMark;

    /**
     * 启动演示程序
     */
    public static void launchDemoProgram() {
        Application.launch(DemoApplication.class);
    }

    /**
     * 设置场景主题，同一场景只保留一个本库主题入口。
     *
     * @param scene 场景
     * @param theme 主题
     */
    public static void setTheme(Scene scene, Theme theme) {
        Objects.requireNonNull(scene, "scene must not be null");
        replaceThemeStylesheet(scene.getStylesheets(), theme);
    }

    private static void replaceThemeStylesheet(List<String> stylesheets, Theme theme) {
        String requestedStylesheet = themeStylesheet(theme);
        List<String> knownThemeStylesheets = Arrays.stream(Theme.values())
                .map(JavaFXUI::themeStylesheet)
                .toList();
        int firstThemeIndex = -1;
        int themeCount = 0;
        boolean requestedThemeOnly = true;
        for (int index = 0; index < stylesheets.size(); index++) {
            String stylesheet = stylesheets.get(index);
            if (knownThemeStylesheets.contains(stylesheet)) {
                if (firstThemeIndex < 0) {
                    firstThemeIndex = index;
                }
                themeCount++;
                requestedThemeOnly &= requestedStylesheet.equals(stylesheet);
            }
        }
        if (themeCount == 1 && requestedThemeOnly) {
            return;
        }
        stylesheets.removeIf(knownThemeStylesheets::contains);
        int insertionIndex = firstThemeIndex < 0
                ? stylesheets.size()
                : Math.min(firstThemeIndex, stylesheets.size());
        stylesheets.add(insertionIndex, requestedStylesheet);
    }

    public static void setTheme(Scene scene) {
        setTheme(scene, getDefaultTheme());
    }

    /** 获取场景实际加载的主题；未加载主题时返回全局默认值。 */
    public static Theme getTheme(Scene scene) {
        Objects.requireNonNull(scene, "scene must not be null");
        Theme rootTheme = findTheme(scene.getRoot().getStylesheets());
        if (rootTheme != null) {
            return rootTheme;
        }
        Theme sceneTheme = findTheme(scene.getStylesheets());
        return sceneTheme == null ? getDefaultTheme() : sceneTheme;
    }

    /** 获取节点所在主题，优先采用节点或祖先的显式主题后缀和局部主题入口。 */
    public static Theme getTheme(Node node) {
        Objects.requireNonNull(node, "node must not be null");
        for (Node current = node; current != null; current = current.getParent()) {
            for (int index = current.getStyleClass().size() - 1; index >= 0; index--) {
                String styleClass = current.getStyleClass().get(index);
                if (styleClass.endsWith("-ui-windows")) {
                    return Theme.WINDOWS;
                }
                if (styleClass.endsWith("-ui-base")) {
                    return Theme.BASE;
                }
            }
            if (current instanceof Parent parent) {
                Theme localTheme = findTheme(parent.getStylesheets());
                if (localTheme != null) {
                    return localTheme;
                }
            }
        }
        return node.getScene() == null ? getDefaultTheme() : getTheme(node.getScene());
    }

    private static Theme findTheme(List<String> stylesheets) {
        for (int index = stylesheets.size() - 1; index >= 0; index--) {
            String stylesheet = stylesheets.get(index);
            for (Theme theme : Theme.values()) {
                if (themeStylesheet(theme).equals(stylesheet)) {
                    return theme;
                }
            }
        }
        return null;
    }

    /**
     * 注册自定义控件的主题入口。普通控件沿用所在场景；显式后缀也作用于内部子控件。
     * 场景未加载本库主题时，首次加入控件会加载默认主题。
     */
    public static void initializeTheme(Parent control, String styleClass) {
        Objects.requireNonNull(control, "control must not be null");
        Objects.requireNonNull(styleClass, "styleClass must not be null");
        if (!control.getStyleClass().contains(styleClass)) {
            control.getStyleClass().add(styleClass);
        }
        boolean[] updating = {false};
        Runnable updateExplicitTheme = () -> {
            if (updating[0]) {
                return;
            }
            updating[0] = true;
            try {
                boolean base = control.getStyleClass().contains(styleClass + "-base");
                boolean windows = control.getStyleClass().contains(styleClass + "-windows");
                if (base && windows) {
                    throw new IllegalArgumentException("A control cannot force both Base and Windows themes");
                }
                if (base || windows) {
                    Theme theme = base ? Theme.BASE : Theme.WINDOWS;
                    replaceThemeStylesheet(control.getStylesheets(), theme);
                    ControlThemeScope scope = (ControlThemeScope) control.getProperties().get(THEME_SCOPE_KEY);
                    if (scope == null || scope.theme() != theme) {
                        if (scope != null) scope.close();
                        control.getProperties().put(THEME_SCOPE_KEY, new ControlThemeScope(control, theme));
                    }
                } else {
                    ControlThemeScope scope = (ControlThemeScope) control.getProperties().remove(THEME_SCOPE_KEY);
                    if (scope != null) scope.close();
                    control.getStylesheets().removeIf(stylesheet -> Arrays.stream(Theme.values())
                            .anyMatch(theme -> themeStylesheet(theme).equals(stylesheet)));
                }
            } finally {
                updating[0] = false;
            }
        };
        control.getStyleClass().addListener((ListChangeListener<String>) change -> updateExplicitTheme.run());
        control.sceneProperty().addListener((observable, previous, scene) -> {
            if (scene != null && findTheme(scene.getStylesheets()) == null
                && findTheme(scene.getRoot().getStylesheets()) == null) {
                setTheme(scene);
            }
        });
        updateExplicitTheme.run();
    }

    /** 每次显示独立窗口或 Popup 时重新采用所属节点的主题；owner 为 null 时使用默认主题。 */
    public static void followTheme(Window window, Node owner) {
        Objects.requireNonNull(window, "window must not be null");
        window.addEventHandler(WindowEvent.WINDOW_SHOWING, event ->
                setTheme(window.getScene(), owner == null ? getDefaultTheme() : getTheme(owner)));
    }

    /**
     * 获取主题入口样式表 URL。
     *
     * @param theme 主题
     * @return 主题入口样式表 URL
     */
    public static String themeStylesheet(Theme theme) {
        Theme requiredTheme = Objects.requireNonNull(theme, "theme must not be null");
        URL resource = JavaFXUI.class.getResource(requiredTheme.resourcePath());
        if (resource == null) {
            throw new IllegalStateException("Theme stylesheet not found: " + requiredTheme.resourcePath());
        }
        return resource.toExternalForm();
    }

}
