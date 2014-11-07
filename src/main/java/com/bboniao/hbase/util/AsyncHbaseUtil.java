package com.bboniao.hbase.util;

import org.hbase.async.HBaseClient;

/**
 * 异步hbase客户端
 * Created by bboniao on 11/6/14.
 */
public enum AsyncHbaseUtil {

    I;

    private HBaseClient client;

    AsyncHbaseUtil() {
        client = new HBaseClient(Constant.ZOOKEEPER, Constant.ZOOKEEPER_PATH, AsyncThreadPoolFactory.ASYNC_HBASE_THREAD_POOL);
    }

    public HBaseClient getClient() {
        return client;
    }
}
