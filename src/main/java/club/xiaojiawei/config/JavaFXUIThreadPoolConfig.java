package club.xiaojiawei.config;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/7 22:24
 */
public class JavaFXUIThreadPoolConfig {

    public static final ScheduledThreadPoolExecutor SCHEDULED_POOL = new ScheduledThreadPoolExecutor(10, new ThreadFactory() {
        private final AtomicInteger num = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ExtraPool Thread-" + num.getAndIncrement());
        }
    }, new ThreadPoolExecutor.AbortPolicy());

}
