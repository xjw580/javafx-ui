import club.xiaojiawei.controls.TableValueFilterManager;
import club.xiaojiawei.controls.AbstractTableFilterManager;

module club.xiaojiawei.javafx_ui {
    uses AbstractTableFilterManager;
    provides AbstractTableFilterManager with
            TableValueFilterManager,
            club.xiaojiawei.controls.TableCheckFilterManager,
            club.xiaojiawei.controls.TableDateFilterManager;

    requires static lombok;
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.girod.javafx.svgimage;
    requires java.desktop;
    requires org.apache.tika.core;
    requires org.slf4j;
    requires jdk.jsobject;

    exports club.xiaojiawei;
    exports club.xiaojiawei.utils;
    exports club.xiaojiawei.func;
    exports club.xiaojiawei.enums;
    exports club.xiaojiawei.component;
    exports club.xiaojiawei.controls;
    exports club.xiaojiawei.controls.ico;
    exports club.xiaojiawei.demo;
    exports club.xiaojiawei.demo.tab.style;
    exports club.xiaojiawei.test;
    exports club.xiaojiawei.bean;
    exports club.xiaojiawei.skin;
    exports club.xiaojiawei.factory;
    exports club.xiaojiawei.annotations;

    opens club.xiaojiawei.component to javafx.fxml;
    opens club.xiaojiawei.controls to javafx.fxml;
    opens club.xiaojiawei.controls.ico to javafx.fxml;
    opens club.xiaojiawei.demo to javafx.fxml;
    opens club.xiaojiawei.demo.tab.style to javafx.fxml;
    opens club.xiaojiawei.demo.tab.controls to javafx.fxml;
    opens club.xiaojiawei.bean to javafx.fxml;
    opens club.xiaojiawei.factory to javafx.fxml;
    opens club.xiaojiawei.skin to javafx.fxml;
    opens club.xiaojiawei.func to javafx.fxml;
    opens club.xiaojiawei.test to javafx.fxml;
}