package com.bboniao.hbase.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Created by bboniao on 11/6/14.
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger mThreadNum = new AtomicInteger(1);

    private final String mPrefix;

    private final boolean mDaemo;

    private final ThreadGroup mGroup;

    public NamedThreadFactory(String prefix, boolean daemo) {
        mPrefix = prefix + "-thread-";
        mDaemo = daemo;
        SecurityManager s = System.getSecurityManager();
        mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
    }

    public Thread newThread(Runnable runnable) {
        String name = mPrefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(mGroup, runnable, name, 0);
        ret.setDaemon(mDaemo);
        return ret;
    }

    @Override
    public String toString() {
        return mPrefix;
    }
}
