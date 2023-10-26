module club.xiaojiawei.javafx_ui {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.girod.javafx.svgimage;

    exports club.xiaojiawei.utils;
    exports club.xiaojiawei.enums;
    exports club.xiaojiawei.controls;
    exports club.xiaojiawei.test;

    opens club.xiaojiawei.controls to javafx.fxml;
    opens club.xiaojiawei.test to javafx.fxml;
    exports club.xiaojiawei.controls.ico;
    opens club.xiaojiawei.controls.ico to javafx.fxml;
}
/**
 * 主颜色：#0075FF
 * hover颜色：#cae1ff
 * effect颜色：#b3d5ff
 */