module club.xiaojiawei.javafx_ui {
    requires static lombok;
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.girod.javafx.svgimage;
    requires java.desktop;
    requires org.apache.tika.core;
    requires org.slf4j;
    requires jdk.jsobject;

    exports club.xiaojiawei.utils;
    exports club.xiaojiawei.enums;
    exports club.xiaojiawei.controls;
    exports club.xiaojiawei.test;
    exports club.xiaojiawei.controls.ico;
    exports club.xiaojiawei.readme;
    exports club.xiaojiawei.proxy;
    exports club.xiaojiawei;
    exports club.xiaojiawei.readme.tab.style;
    exports club.xiaojiawei.config;
    exports club.xiaojiawei.bean;

    opens club.xiaojiawei.controls to javafx.fxml;
    opens club.xiaojiawei.test to javafx.fxml;
    opens club.xiaojiawei.controls.ico to javafx.fxml;
    opens club.xiaojiawei.readme to javafx.fxml;
    opens club.xiaojiawei.proxy to javafx.fxml;
    opens club.xiaojiawei.readme.tab.style to javafx.fxml;
    opens club.xiaojiawei.readme.tab.component to javafx.fxml;
    opens club.xiaojiawei.bean to javafx.fxml;
}