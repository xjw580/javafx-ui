package club.xiaojiawei.factory;

import club.xiaojiawei.controls.AbstractTableFilterManager;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/29 15:01
 */
@Slf4j
public class TableFilterManagerFactory {

    private static final List<AbstractTableFilterManager> LIST;

    static {
        ServiceLoader<AbstractTableFilterManager> tableFilterManagers = ServiceLoader.load(AbstractTableFilterManager.class);
        LIST = StreamSupport.stream(tableFilterManagers.spliterator(), false).toList();
        LIST.forEach(i -> log.info("AbstractTableFilterManager SPI load: {}", i.getClass()));
    }

    public static<S, T> AbstractTableFilterManager<S, T> build(String userData){
        if (LIST == null) {
            return null;
        }
        for (AbstractTableFilterManager<S, T> abstractTableFilterManager : LIST) {
            boolean isNeed = abstractTableFilterManager.needHandle(userData);
            if (isNeed) {
                try {
                    return abstractTableFilterManager.getClass().getConstructor().newInstance();
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    log.error("创建{}失败",abstractTableFilterManager.getClass(), e);
                }
                return null;
            }
        }
        return null;
    }

}
