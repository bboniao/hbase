package com.bboniao.hbase.util;

import org.hbase.async.GetRequest;
import org.hbase.async.HBaseClient;
import org.hbase.async.KeyValue;

import java.util.List;

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

    public void close() throws Exception {
        this.client.shutdown().join();
    }

    public static void main(String[] args) throws Exception {
        List<KeyValue> list = AsyncHbaseUtil.I.getClient().get(new GetRequest("rc_feature".getBytes(), "000008e46cd4286ff5d2dd8bd5682920|2|19763476".getBytes())).join();
        for (KeyValue kv : list) {
            System.out.println(new String(kv.value()));
        }

    }
}
