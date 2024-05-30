package club.xiaojiawei.test;

import javafx.animation.PauseTransition;
import javafx.beans.DefaultProperty;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/28 9:26
 */
public class MyPoint{

    private final ObservableList<String> children = FXCollections.observableArrayList();

    public ObservableList<String> getChildren() {
        return children;
    }

    private  int x;

    private  int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public MyPoint(@NamedArg("x") int x, @NamedArg("y") int y) {
        this.x = x;
        this.y = y;
    }

}
