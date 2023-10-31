package club.xiaojiawei.proxy;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/1 0:20
 */
public abstract class ProxyAnchorPaneRegion<T extends Region> extends AnchorPane implements ProxyRegion<T>{

    public double getProxyHeightProperty() {
        return getRegion().heightProperty().get();
    }
    public ReadOnlyDoubleProperty proxyHeightProperty() {
        return getRegion().heightProperty();
    }
    public double getProxyWidthProperty() {
        return getRegion().widthProperty().get();
    }
    public ReadOnlyDoubleProperty proxyWidthProperty() {
        return getRegion().widthProperty();
    }
    public double getProxyPrefHeightProperty() {
        return getRegion().prefHeightProperty().get();
    }
    public DoubleProperty proxyPrefHeightProperty() {
        return getRegion().prefHeightProperty();
    }
    public void setProxyPrefHeightProperty(double prefHeightProperty) {
        getRegion().prefHeightProperty().set(prefHeightProperty);
    }
    public double getProxyPrefWidthProperty() {
        return getRegion().prefWidthProperty().get();
    }
    public DoubleProperty proxyPrefWidthProperty() {
        return getRegion().prefWidthProperty();
    }
    public void setProxyPrefWidthProperty(double prefWidthProperty) {
        getRegion().prefWidthProperty().set(prefWidthProperty);
    }
    public double getProxyMaxHeightProperty() {
        return getRegion().maxHeightProperty().get();
    }
    public DoubleProperty proxyMaxHeightProperty() {
        return getRegion().maxHeightProperty();
    }
    public void setProxyMaxHeightProperty(double maxHeightProperty) {
        getRegion().maxHeightProperty().set(maxHeightProperty);
    }
    public double getProxyMaxWidthProperty() {
        return getRegion().maxWidthProperty().get();
    }
    public DoubleProperty proxyMaxWidthProperty() {
        return getRegion().maxWidthProperty();
    }
    public void setProxyMaxWidthProperty(double maxWidthProperty) {
        getRegion().maxWidthProperty().set(maxWidthProperty);
    }
    public double getProxyMinHeightProperty() {
        return getRegion().minHeightProperty().get();
    }
    public DoubleProperty proxyMinHeightProperty() {
        return getRegion().minHeightProperty();
    }
    public void setProxyMinHeightProperty(double minHeightProperty) {
        getRegion().minHeightProperty().set(minHeightProperty);
    }
    public double getProxyMinWidthProperty() {
        return getRegion().minWidthProperty().get();
    }
    public DoubleProperty proxyMinWidthProperty() {
        return getRegion().minWidthProperty();
    }
    public void setProxyMinWidthProperty(double minWidthProperty) {
        getRegion().minWidthProperty().set(minWidthProperty);
    }
}
