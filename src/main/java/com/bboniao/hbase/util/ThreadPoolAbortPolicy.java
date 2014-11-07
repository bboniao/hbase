package com.bboniao.hbase.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 拒绝策略
 * Created by bboniao on 11/6/14.
 */
public class ThreadPoolAbortPolicy extends ThreadPoolExecutor.AbortPolicy {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String threadFactoryName = e.getThreadFactory().toString();
        String msg = String.format("%s is EXHAUSTED!" +
                        " Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)," +
                        " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)!", threadFactoryName,
                e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating());
        logger.error(msg);
        throw new RejectedExecutionException(msg);
    }

    public ThreadPoolAbortPolicy() {
    }
}
