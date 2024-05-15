package club.xiaojiawei.controls;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.annotations.OnlyOnce;
import club.xiaojiawei.component.AbstractTableFilter;
import club.xiaojiawei.component.TableFilter;
import club.xiaojiawei.config.JavaFXUIThreadPoolConfig;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import lombok.Getter;

import java.io.IOException;

/**
 * 表格字符串过滤管理器，T最好是String类型，或者其toString方法返回的字符串和表格中显示的字符串是一样的
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/10 17:54
 */
public class TableFilterManager<S, T> extends StackPane {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                    *
     *                                                                         *
     **************************************************************************/

    @NotNull
    private TableColumn<S, T> outerTableColumn;

    @NotNull
    @Getter
    private TableFilterManagerGroup<S, T> tableFilterManagerGroup;

    public TableColumn<S, T> getTableColumn() {
        return outerTableColumn;
    }

    @OnlyOnce
    public void setTableColumn(TableColumn<S, T> tableColumn) {
        if (tableColumn == null){
            throw new NullPointerException("tableColumn");
        }
        if (outerTableColumn == null){
            outerTableColumn = tableColumn;
        }
    }

    @OnlyOnce
    public void setTableFilterManagerGroup(TableFilterManagerGroup<S, T> tableFilterManagerGroup) {
        if (tableFilterManagerGroup == null){
            throw new NullPointerException("tableFilterManagerGroup");
        }
        if (this.tableFilterManagerGroup == null){
            this.tableFilterManagerGroup = tableFilterManagerGroup;
        }
    }

    /* *************************************************************************
     *                                                                         *
     * 构造方法                                                                 *
     *                                                                         *
     **************************************************************************/

    @FXML
    protected Button filterBtn;

    public TableFilterManager() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(TableFilterManager.class.getResource(TableFilterManager.class.getSimpleName() + ".fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            afterFXMLLoaded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Popup popup;

    private AbstractTableFilter<S, T> tableFilter;

    /**
     * 是否处于过滤状态
     */
    private final ReadOnlyBooleanWrapper isFilter = new ReadOnlyBooleanWrapper();

    public Boolean getIsFilter() {
        return isFilter.get();
    }

    public ReadOnlyBooleanProperty isFilterReadOnlyProperty() {
        return isFilter.getReadOnlyProperty();
    }

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void afterFXMLLoaded() {
        addListener();
    }

    private void addListener(){
        filterBtn.setOnAction(actionEvent -> {
            if (outerTableColumn == null || tableFilterManagerGroup == null){
                throw new NullPointerException();
            }
            initPopup();
            if (popup.isShowing()){
                JavaFXUIThreadPoolConfig.SCHEDULED_POOL.submit(() -> {
                    for (int i = 6; i > 0; i--) {
                        try {
                            if (popup.getOpacity() == 1){
                                Platform.runLater(() -> popup.setOpacity(0.4));
                                Thread.sleep(100);
                            }else {
                                Platform.runLater(() -> popup.setOpacity(1));
                                Thread.sleep(100);
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }else {
                popup.show(this.getScene().getWindow());
            }
        });
    }

    @OnlyOnce
    private void initPopup(){
        if (popup != null){
            return;
        }
        String filteringStyleClass = "filteringBtn";
        tableFilter = getTableFilter(outerTableColumn, tableFilterManagerGroup);
        tableFilter.isFilteringReadOnlyProperty().addListener((observableValue, aBoolean, t1) -> {
            filterBtn.getStyleClass().remove(filteringStyleClass);
            if (t1){
                filterBtn.getStyleClass().add(filteringStyleClass);
            }
            isFilter.set(t1);
        });
        tableFilterManagerGroup.register(tableFilter);
        Popup newPopup = new Popup();
        newPopup.getContent().add(tableFilter);
        newPopup.setAutoHide(true);
        Bounds bounds = this.localToScreen(this.getBoundsInLocal());
        newPopup.setX(bounds.getMinX() + 5);
        newPopup.setY(bounds.getMinY() + 10);
        popup = newPopup;
    }

    protected AbstractTableFilter<S, T> getTableFilter(TableColumn<S, T> tableColumn, TableFilterManagerGroup<S, T> tableFilterManagerGroup){
        return new TableFilter<>(tableColumn, tableFilterManagerGroup);
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    public void refresh(){
        if (tableFilter != null){
            tableFilter.refresh();
        }
    }

}
