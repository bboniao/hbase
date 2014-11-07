package com.bboniao.hbase.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * Created by bboniao on 11/6/14.
 */
public class AsyncThreadPoolFactory {

    public static final ExecutorService ASYNC_HBASE_THREAD_POOL = new ThreadPoolExecutor(200, 200,
            0L, TimeUnit.MILLISECONDS,
            new SynchronousQueue<Runnable>(),
            new NamedThreadFactory("asynchbase", true),
            new ThreadPoolAbortPolicy());
}
