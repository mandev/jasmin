/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adlitteram.jasmin.utils;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author manu
 */
public class ThreadUtils {

 public static ExecutorService newFixedLifoThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
           asLifoBlockingQueue(new LinkedBlockingDeque<Runnable>()));
    }

    public static BlockingQueue asLifoBlockingQueue(BlockingDeque deque) {
        return new AsLIFOBlockingQueue(deque);
    }
}
