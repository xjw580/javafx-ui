module club.xiaojiawei.javafx_ui {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.girod.javafx.svgimage;
    requires java.desktop;
    requires org.apache.tika.core;
    requires org.slf4j;
    requires static lombok;
    requires javafx.web;
    requires org.bytedeco.opencv;
    requires org.bytedeco.openblas.platform;
    requires jdk.jsobject;
    requires commons.math3;

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

    opens club.xiaojiawei.controls to javafx.fxml;
    opens club.xiaojiawei.test to javafx.fxml;
    opens club.xiaojiawei.controls.ico to javafx.fxml;
    opens club.xiaojiawei.readme to javafx.fxml;
    opens club.xiaojiawei.proxy to javafx.fxml;
    opens club.xiaojiawei.readme.tab.style to javafx.fxml;
    opens club.xiaojiawei.readme.tab.component to javafx.fxml;
    exports club.xiaojiawei.bean;
    opens club.xiaojiawei.bean to javafx.fxml;
}
/**
 * 主颜色：#0075FF
 * hover颜色：#cae1ff
 * effect颜色：#b3d5ff
 */