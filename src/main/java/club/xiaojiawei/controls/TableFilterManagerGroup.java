package club.xiaojiawei.controls;

import club.xiaojiawei.annotations.OnlyOnce;
import club.xiaojiawei.component.AbstractTableFilter;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Getter;

import java.util.*;


/**
 * 表格过滤器管理器组
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/14 15:14
 */
public class TableFilterManagerGroup<S, T> {

    /* *************************************************************************
     *                                                                         *
     * 属性                                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * 需要管理的表格
     */
    @Getter
    private TableView<S> tableView;
    /**
     * 存储外部表格的原始数据
     */
    private List<S> rawItems;

    /**
     * 自动注册表格列的过滤器，为true时如果在fxml中构造，需要放到column之后
     */
    @Getter
    private boolean autoRegisterColFilter;

    @OnlyOnce
    public void setTableView(TableView<S> tableView) {
        if (tableView == null){
            throw new NullPointerException("tableView");
        }
        if (this.tableView == null){
            this.tableView = tableView;
            enableItemsStack.push(rawItems = new ArrayList<>(tableView.getItems()));
            tableView.itemsProperty().addListener(observable -> {
                updateRawItems(true);
                addItemsListener();
            });
            addItemsListener();
            execAutoRegister();
        }
    }

    public void setAutoRegisterColFilter(boolean autoRegisterColFilter) {
        this.autoRegisterColFilter = autoRegisterColFilter;
        execAutoRegister();
    }

    /**
     * 启用的过滤器
     */
    private final Stack<AbstractTableFilter<S, T >> enableFilterStack = new Stack<>();
    /**
     * 禁用的过滤器
     */
    private final HashSet<AbstractTableFilter<S, T >> disableFilterSet = new HashSet<>();
    /**
     * 临时存放启用的过滤器
     */
    private final Stack<AbstractTableFilter<S, T >> tempEnableFilterStack = new Stack<>();
    /**
     * 启用的过滤器过滤后的结果，和启用的过滤器顺序一致
     */
    private final Stack<List<S>> enableItemsStack = new Stack<>();
    /**
     * 已注册的过滤器
     */
    private final Set<AbstractTableFilter<S, T>> registerFilterSet = new HashSet<>();
    /**
     * 是否为外部更改表数据
     */
    private boolean isOuterChange = true;

    /**
     * 是否自动注册了过滤器
     */
    private boolean isAutoRegister;

    /* *************************************************************************
     *                                                                         *
     * 私有方法                                                                 *
     *                                                                         *
     **************************************************************************/

    private void addItemsListener(){
        tableView.getItems().addListener((ListChangeListener<? super S>) change -> {
            if (isOuterChange){
                boolean isReset = false;
                if (change.next()){
                    if (change.wasReplaced()) {
                        ObservableList<S> items = tableView.getItems();
                        if (items.size() == rawItems.size()){
                            int changeTo = change.getTo();
                            for (int i = change.getFrom(); i < changeTo; i++){
                                rawItems.set(i, items.get(i));
                            }
                        }else {
                            isReset = true;
                        }
                    } else if (change.wasPermutated()) {
                        return;
                    } else if (change.wasRemoved()) {
                        HashSet<S> removedSet = new HashSet<>(change.getRemoved());
                        rawItems.removeIf(removedSet::contains);
                    } else if (change.wasAdded()) {
                        rawItems.addAll(change.getAddedSubList());
                    }
                }
                updateRawItems(isReset);
            }
        });
    }

    private void updateRawItems(boolean isReset){
        if (isReset){
            rawItems.clear();
            rawItems.addAll(tableView.getItems());
        }
        filtration(null);
    }

    private void execAutoRegister(){
        if (!isAutoRegister && autoRegisterColFilter && tableView != null) {
            isAutoRegister = true;
            for (TableColumn<S, ?> column : tableView.getColumns()) {
                Object data = column.getUserData();
                if (Objects.equals(data, "disable")){
                    continue;
                }
                TableFilterManager<S, T> tableFilterManager = getStTableFilterManager(data);
                tableFilterManager.setTableColumn((TableColumn<S, T>) column);
                tableFilterManager.setTableFilterManagerGroup(this);
                column.setGraphic(tableFilterManager);
            }
        }
    }

    private static <S, T> TableFilterManager<S, T> getStTableFilterManager(Object data) {
        TableFilterManager<S, T> tableFilterManager;
        if (data instanceof String s && s.startsWith("date")) {
            String[] split = s.split("=");
            TableDateFilterManager<S, T> manager = new TableDateFilterManager<>();
            tableFilterManager = manager;
            if (split.length > 1){
                manager.setDateFormat(split[1]);
            }
        }else {
            tableFilterManager = new TableFilterManager<>();
        }
        return tableFilterManager;
    }

    /**
     * <p>1. 过滤</p>
     *    表格：最终结果，
     *    其他过滤器：最终结果，
     *    最后过滤器：不变，
     *    非过滤器：最终结果，
     * <p>2. 取消过滤</p>
     *    <p>2.1. 顺序取消</p>
     *       表格：最终结果，
     *       其他过滤器：最终结果，
     *       最后过滤器：最终结果，
     *       最后启用的过滤器：倒数第二个结果，
     *       非过滤器：最终结果，
     *    <p>2.2. 非顺序取消</p>
     *       表格：最终结果，
     *       其他过滤器：最终结果，
     *       最后过滤器：倒数第二个结果，
     *       非过滤器：最终结果，
     * @param lastFilter 最后一个过滤器，为null表示源数据有更新，全部需要重新过滤
     */
    private void handleUpdateItems(AbstractTableFilter<S, T> lastFilter){
        List<S> finalFilerResult;
//        是否是按过滤顺序取消的过滤
        boolean isOrder = tempEnableFilterStack.size() <= 1;
        boolean existDisableFilter = false;
        if (lastFilter == null){
            for (AbstractTableFilter<S, T> filter : registerFilterSet) {
                filter.changeTableItems(enableItemsStack.get(0));
            }
        }
        while (!tempEnableFilterStack.empty()){
            AbstractTableFilter<S, T> popFilter = tempEnableFilterStack.pop();
            finalFilerResult = popFilter.getFilter().apply(List.copyOf(enableItemsStack.peek()));
            if (finalFilerResult == null){
                existDisableFilter = true;
                disableFilterSet.add(popFilter);
            }else {
                disableFilterSet.remove(popFilter);
                enableFilterStack.push(popFilter);
                enableItemsStack.push(finalFilerResult);
            }
        }
        finalFilerResult = List.copyOf(enableItemsStack.peek());
        AbstractTableFilter<S, T> excludeFilter = null;

        if (!enableFilterStack.empty()){
            if (lastFilter == null){
                excludeFilter = enableFilterStack.peek();
                excludeFilter.changeTableItems(enableItemsStack.get(enableItemsStack.size() - 2));
            }else if (existDisableFilter){
                if (isOrder) {
                    excludeFilter = enableFilterStack.peek();
                    excludeFilter.changeTableItems(enableItemsStack.get(enableItemsStack.size() - 2));
                } else {
                    lastFilter.changeTableItems(enableItemsStack.get(enableItemsStack.size() - 2));
                }
            }
        }

        for (AbstractTableFilter<S, T> filter : registerFilterSet) {
            if (filter != lastFilter && filter != excludeFilter){
                filter.changeTableItems(finalFilerResult);
            }
        }
//        如果是移除元素操作，且在监听器内部代码中修改tableView.getItems()会报错，因为内部在对UnmodifiableList修改，所以此处延迟执行修改
        Platform.runLater(() -> {
            isOuterChange = false;
            tableView.getItems().setAll(enableItemsStack.peek());
            isOuterChange = true;
        });
    }

    /* *************************************************************************
     *                                                                         *
     * 公共方法                                                                 *
     *                                                                         *
     **************************************************************************/

    /**
     * 注册过滤器
     * @param filter
     * @return int <br>1：注册成功<br>0：注册失败，因为已经注册过了<br>-1：注册失败，过滤器不合法
     */
    public int register(AbstractTableFilter<S, T> filter){
        if (filter == null){
            return -1;
        }
        if (registerFilterSet.contains(filter)){
            return 0;
        }
        registerFilterSet.add(filter);
        disableFilterSet.add(filter);
        filter.changeTableItems(List.copyOf(enableItemsStack.peek()));
        return 1;
    }

    /**
     * 过滤数据
     * @param filter 为null：表示源数据有更新，需要从第一个过滤器至最后一个过滤器需要重新过滤<br>
     *               不为null：表示该过滤器有更新，从此过滤器开始到至最后一个过滤器需要重新过滤<br>
     */
    public void filtration(AbstractTableFilter<S, T> filter){
        AbstractTableFilter<S, T> lastFilter = null;
        if (filter != null){
            if (disableFilterSet.contains(filter)){
                disableFilterSet.remove(filter);
                enableFilterStack.push(filter);
                enableItemsStack.push(enableItemsStack.peek());
            }
            lastFilter = enableFilterStack.peek();
        }
        while (!enableFilterStack.empty()){
            AbstractTableFilter<S, T> filterPop = enableFilterStack.pop();
            enableItemsStack.pop();
            tempEnableFilterStack.push(filterPop);
            if (filterPop == filter){
                break;
            }
        }
        handleUpdateItems(lastFilter);
    }

    public void resetFilter(){
        for (AbstractTableFilter<S, T> filter : registerFilterSet) {
            filter.reset();
        }
    }

    /**
     * 建议用调用此方法代替tableView.getItems().clear();
     */
    public void clear(){
        isOuterChange = false;
        tableView.getItems().clear();
        updateRawItems(true);
        isOuterChange = true;
    }

    /**
     * 建议用调用此方法代替tableView.getItems().setAll();
     * @param items
     */
    public void setAll(Collection<S> items){
        isOuterChange = false;
        tableView.getItems().setAll(items);
        updateRawItems(true);
        isOuterChange = true;
    }

}
