package club.xiaojiawei.config;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/7 22:24
 */
public class JavaFXUIThreadPoolConfig {

    public static final ScheduledThreadPoolExecutor SCHEDULED_POOL = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
        private final AtomicInteger num = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "JavaFX-UI-POOL Thread-" + num.getAndIncrement());
        }
    }, new ThreadPoolExecutor.AbortPolicy());

    public static final ExecutorService V_THREAD_POOL = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("ExtraPool VThread-", 0).factory());
}
