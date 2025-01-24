package com.adlitteram.jasmin.utils;

import java.util.concurrent.*;

public class ThreadUtils {

    public static ExecutorService newFixedLifoThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                asLifoBlockingQueue(new LinkedBlockingDeque<Runnable>()));
    }

    public static BlockingQueue asLifoBlockingQueue(BlockingDeque deque) {
        return new AsLIFOBlockingQueue(deque);
    }
}
