package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.service.BatchGetServiceUtil;
import com.bboniao.hbase.util.AsyncThreadPoolFactory;
import com.bboniao.hbase.util.Constant;
import com.bboniao.hbase.util.HtableUtil;
import com.stumbleupon.async.Callback;
import com.stumbleupon.async.Deferred;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 使用com.stumbleupon.async实现批量get
 * Created by bboniao on 11/5/14.
 */
public class DeferredBatchGetServiceImpl implements BatchGetService {

    private ExecutorService pool = AsyncThreadPoolFactory.ASYNC_HBASE_THREAD_POOL;

    @Override
    public Map<String, Map<String,String>> batch(List<GetItem> getItems) {
        final Map<String, Map<String,String>> result = new ConcurrentHashMap<>();
        final CountDownLatch latch = new CountDownLatch(getItems.size());

        List<Get> list = new ArrayList<>(20);
        int count = 0;
        Callback<Object, Map<String, Map<String,String>>> cb = new Callback<Object, Map<String, Map<String,String>>>() {
            @Override
            public Object call(Map<String, Map<String,String>> arg) throws Exception {
                latch.countDown();
                if(arg != null) {
                    result.putAll(arg);
                }
                return null;
            }
        };
        for (GetItem item : getItems) {
            Get get = new Get(item.getRowkey());
            get.addFamily(item.getFamily());
            list.add(get);

            if (count % 20 == 0) {
                AsyncGet asyncGet = new AsyncGet(list);
                asyncGet.getDeferred().addBoth(cb);
                pool.submit(asyncGet);
            }

            count++;
        }
        AsyncGet asyncGet = new AsyncGet(list);
        asyncGet.getDeferred().addBoth(cb);
        pool.submit(asyncGet);
        try {
            latch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    static class AsyncGet implements Callable<Boolean> {

        private List<Get> gets;

        private Deferred<Map<String, Map<String,String>>> deferred;

        AsyncGet(List<Get> gets) {
            this.gets = gets;
            this.deferred = new Deferred<>();
        }

        final void callback(final Map<String, Map<String,String>> result) {
            deferred.callback(result);
        }

        final Deferred<Map<String, Map<String,String>>> getDeferred() {
            return deferred;
        }

        @Override
        public Boolean call() throws Exception {
            Result[] rr = HtableUtil.I.getHtable(Constant.HTABLE).get(gets);
            Map<String, Map<String,String>> map = new HashMap<>();

            BatchGetServiceUtil.dealLoop(rr, map);

            callback(map);

            return true;
        }
    }
}
