package club.xiaojiawei.demo.component;

import club.xiaojiawei.controls.CopyLabel;
import club.xiaojiawei.controls.NotificationManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/7/10 16:08
 */
public class StylePage extends ScrollPane {

    public static final String SEPARATOR  = "、";
    @FXML
    private VBox basePane;
    @FXML
    private HBox baseStyleClasses;
    @FXML
    private Label baseIntroduce;
    @FXML
    private HBox baseShow;

    private final ObjectProperty<NotificationManager<Object>> notificationManager = new SimpleObjectProperty<>();

    public NotificationManager<Object> getNotificationManager() {
        return notificationManager.get();
    }

    public ObjectProperty<NotificationManager<Object>> notificationManagerProperty() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager<Object> notificationManager) {
        this.notificationManager.set(notificationManager);
    }

    public void setBaseIntroduce(String baseIntroduce) {
        this.baseIntroduce.setText(baseIntroduce);
    }
    public String getBaseIntroduce() {
        return baseIntroduce.getText();
    }
    public void setBaseStyleClasses(String[] baseStyleClasses) {
        ObservableList<Node> children = this.baseStyleClasses.getChildren();
        addStyleClasses(children, baseStyleClasses);
    }
    public String[] getBaseStyleClasses() {
        return getStyleClasses(this.baseStyleClasses);
    }
    public ObservableList<Node> getBaseShowChildren(){
        return this.baseShow.getChildren();
    }


    @FXML
    private VBox sizePane;
    @FXML
    private HBox sizeStyleClasses;
    @FXML
    private Label sizeIntroduce;
    @FXML
    private HBox sizeShow;
    
    public void setSizeIntroduce(String sizeIntroduce) {
        this.sizeIntroduce.setText(sizeIntroduce);
    }
    public String getSizeIntroduce() {
        return sizeIntroduce.getText();
    }
    public void setSizeStyleClasses(String[] sizeStyleClasses) {
        ObservableList<Node> children = this.sizeStyleClasses.getChildren();
        addStyleClasses(children, sizeStyleClasses);
    }
    public String[] getSizeStyleClasses() {
        return getStyleClasses(this.sizeStyleClasses);
    }
    public ObservableList<Node> getSizeShowChildren(){
        return this.sizeShow.getChildren();
    }
    

    @FXML
    private VBox colorPane;
    @FXML
    private HBox colorStyleClasses;
    @FXML
    private Label colorIntroduce;
    @FXML
    private HBox colorShow;
    
    public void setColorIntroduce(String colorIntroduce) {
        this.colorIntroduce.setText(colorIntroduce);
    }
    public String getColorIntroduce() {
        return colorIntroduce.getText();
    }
    public void setColorStyleClasses(String[] colorStyleClasses) {
        ObservableList<Node> children = this.colorStyleClasses.getChildren();
        addStyleClasses(children, colorStyleClasses);
    }
    public String[] getColorStyleClasses() {
        return getStyleClasses(this.colorStyleClasses);
    }
    public ObservableList<Node> getColorShowChildren(){
        return this.colorShow.getChildren();
    }

    @FXML
    private VBox stylePane;
    @FXML
    private HBox styleStyleClasses;
    @FXML
    private Label styleIntroduce;
    @FXML
    private HBox styleShow;

    public void setStyleIntroduce(String styleIntroduce) {
        this.styleIntroduce.setText(styleIntroduce);
    }
    public String getStyleIntroduce() {
        return styleIntroduce.getText();
    }
    public void setStyleStyleClasses(String[] styleStyleClasses) {
        ObservableList<Node> children = this.styleStyleClasses.getChildren();
        addStyleClasses(children, styleStyleClasses);
    }
    public String[] getStyleStyleClasses() {
        return getStyleClasses(this.styleStyleClasses);
    }
    public ObservableList<Node> getStyleShowChildren(){
        return this.styleShow.getChildren();
    }

    @FXML
    private VBox allPane;
    @FXML
    private HBox allStyleClasses;
    @FXML
    private Label allIntroduce;
    @FXML
    private HBox allShow;
    
    public void setAllIntroduce(String allIntroduce) {
        this.allIntroduce.setText(allIntroduce);
    }
    public String getAllIntroduce() {
        return allIntroduce.getText();
    }
    public void setAllStyleClasses(String[] allStyleClasses) {
        ObservableList<Node> children = this.allStyleClasses.getChildren();
        addStyleClasses(children, allStyleClasses);
    }
    public String[] getAllStyleClasses() {
        return getStyleClasses(this.allStyleClasses);
    }
    public ObservableList<Node> getAllShowChildren(){
        return this.allShow.getChildren();
    }
    

    public StylePage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(this.getClass().getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void afterFXMLLoaded() {
        baseStyleClasses.getChildren().addListener((ListChangeListener<? super Node>) observable -> baseRunnable.accept(null));
        baseShow.getChildren().addListener((ListChangeListener<? super Node>) observable -> baseRunnable.accept(baseShow));
        baseIntroduce.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean notEmpty = isNotEmpty(newValue);
            baseIntroduce.getParent().setVisible(notEmpty);
            baseIntroduce.getParent().setManaged(notEmpty);
        });
        
        sizeStyleClasses.getChildren().addListener((ListChangeListener<? super Node>) observable -> sizeRunnable.accept(null));
        sizeShow.getChildren().addListener((ListChangeListener<? super Node>) observable -> sizeRunnable.accept(sizeShow));
        sizeIntroduce.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean notEmpty = isNotEmpty(newValue);
            sizeIntroduce.getParent().setVisible(notEmpty);
            sizeIntroduce.getParent().setManaged(notEmpty);
        });
        
        colorStyleClasses.getChildren().addListener((ListChangeListener<? super Node>) observable -> colorRunnable.accept(null));
        colorShow.getChildren().addListener((ListChangeListener<? super Node>) observable -> colorRunnable.accept(colorShow));
        colorIntroduce.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean notEmpty = isNotEmpty(newValue);
            colorIntroduce.getParent().setVisible(notEmpty);
            colorIntroduce.getParent().setManaged(notEmpty);
        });
        
        styleStyleClasses.getChildren().addListener((ListChangeListener<? super Node>) observable -> styleRunnable.accept(null));
        styleShow.getChildren().addListener((ListChangeListener<? super Node>) observable -> styleRunnable.accept(styleShow));
        styleIntroduce.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean notEmpty = isNotEmpty(newValue);
            styleIntroduce.getParent().setVisible(notEmpty);
            styleIntroduce.getParent().setManaged(notEmpty);
        });
        
        allStyleClasses.getChildren().addListener((ListChangeListener<? super Node>) observable -> allRunnable.accept(null));
        allShow.getChildren().addListener((ListChangeListener<? super Node>) observable -> allRunnable.accept(allShow));
        allIntroduce.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean notEmpty = isNotEmpty(newValue);
            allIntroduce.getParent().setVisible(notEmpty);
            allIntroduce.getParent().setManaged(notEmpty);
        });
    }
    
    private final Consumer<Node> baseRunnable = node -> {
        boolean notEmpty = isNotEmpty(baseStyleClasses, baseShow);
        if (node != null) {
            node.setVisible(notEmpty);
            node.setManaged(notEmpty);
        }
        basePane.setVisible(notEmpty);
        basePane.setManaged(notEmpty);
    };
    private final Consumer<Node> sizeRunnable = node -> {
        boolean notEmpty = isNotEmpty(sizeStyleClasses, sizeShow);
        if (node != null) {
            node.setVisible(notEmpty);
            node.setManaged(notEmpty);
        }
        sizePane.setVisible(notEmpty);
        sizePane.setManaged(notEmpty);
    };
    private final Consumer<Node> colorRunnable = node -> {
        boolean notEmpty = isNotEmpty(colorStyleClasses, colorShow);
        if (node != null) {
            node.setVisible(notEmpty);
            node.setManaged(notEmpty);
        }
        colorPane.setVisible(notEmpty);
        colorPane.setManaged(notEmpty);
    };
    private final Consumer<Node> styleRunnable = node -> {
        boolean notEmpty = isNotEmpty(styleStyleClasses, styleShow);
        if (node != null) {
            node.setVisible(notEmpty);
            node.setManaged(notEmpty);
        }
        stylePane.setVisible(notEmpty);
        stylePane.setManaged(notEmpty);
    };
    private final Consumer<Node> allRunnable = node -> {
        boolean notEmpty = isNotEmpty(allStyleClasses, allShow);
        if (node != null) {
            node.setVisible(notEmpty);
            node.setManaged(notEmpty);
        }
        allPane.setVisible(notEmpty);
        allPane.setManaged(notEmpty);
    };

    private boolean isNotEmpty(String s){
        return s != null && !s.isEmpty();
    }

    private boolean isNotEmpty(HBox styleClasses, HBox show){
        return !styleClasses.getChildren().isEmpty() || !show.getChildren().isEmpty();
    }

    private void addStyleClasses(ObservableList<Node> children, String[] styleClasses){
        for (int i = 0; i < styleClasses.length; i++) {
            String styleClass = styleClasses[i];
            CopyLabel copyLabel = new CopyLabel(styleClass);
            copyLabel.getStyleClass().add("title-three");
            copyLabel.notificationManagerProperty().bind(notificationManager);
            children.add(copyLabel);
            if (i < styleClasses.length - 1) {
                Label label = new Label(SEPARATOR);
                label.getStyleClass().add("title-three");
                children.add(label);
            }
        }
    }

    private String[] getStyleClasses(HBox styleClasses) {
        return styleClasses.getChildren()
                .stream()
                .filter(label -> !Objects.equals(SEPARATOR, ((Label) label).getText()))
                .map(label -> ((Label) label).getText())
                .toArray(String[]::new);
    }

}
