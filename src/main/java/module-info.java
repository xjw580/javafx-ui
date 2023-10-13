module club.xiaojiawei.javafxplus {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    exports club.xiaojiawei.utils;
    exports club.xiaojiawei.enums;
    exports club.xiaojiawei.controls;
    exports club.xiaojiawei.test;

    opens club.xiaojiawei.controls to javafx.fxml;
    opens club.xiaojiawei.test to javafx.fxml;
}