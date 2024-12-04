import club.xiaojiawei.controls.TableValueFilterManager;
import club.xiaojiawei.controls.AbstractTableFilterManager;

module club.xiaojiawei.javafx_ui {
    uses AbstractTableFilterManager;
    provides AbstractTableFilterManager with
            TableValueFilterManager,
            club.xiaojiawei.controls.TableCheckFilterManager,
            club.xiaojiawei.controls.TableDateFilterManager;

    requires static lombok;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.girod.javafx.svgimage;
    requires java.desktop;
    requires org.apache.tika.core;
    requires org.slf4j;
    requires jdk.jsobject;
    requires jdk.unsupported.desktop;
    requires org.apache.commons.io;

    exports club.xiaojiawei;
    exports club.xiaojiawei.func;
    exports club.xiaojiawei.enums;
    exports club.xiaojiawei.controls;
    exports club.xiaojiawei.controls.ico;
    exports club.xiaojiawei.factory;
    exports club.xiaojiawei.annotations;
    exports club.xiaojiawei.component;

    opens club.xiaojiawei.component to javafx.fxml;
    opens club.xiaojiawei.controls to javafx.fxml;
    opens club.xiaojiawei.controls.ico to javafx.fxml;
    opens club.xiaojiawei.demo to javafx.fxml, javafx.base, javafx.graphics;
    opens club.xiaojiawei.demo.component to javafx.fxml;
    opens club.xiaojiawei.demo.tab.style to javafx.fxml, javafx.base;
    opens club.xiaojiawei.demo.tab.controls to javafx.fxml, javafx.base;
    opens club.xiaojiawei.bean to javafx.fxml;
    opens club.xiaojiawei.factory to javafx.fxml;
    opens club.xiaojiawei.skin to javafx.fxml;
    opens club.xiaojiawei.func to javafx.fxml;
    opens club.xiaojiawei.test to javafx.fxml, javafx.base, javafx.graphics;
}