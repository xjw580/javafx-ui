module club.xiaojiawei.javafxplus {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;

    exports club.xiaojiawei.utils;
    exports club.xiaojiawei.enums;
    exports club.xiaojiawei.controls;

    opens club.xiaojiawei.controls to javafx.fxml;
}