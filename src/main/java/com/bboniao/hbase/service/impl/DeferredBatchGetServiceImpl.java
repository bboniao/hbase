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

        Callback<Object, Result[]> cb = new Callback<Object, Result[]>() {
            @Override
            public Object call(Result[] arg) throws Exception {
                latch.countDown();
                if(arg != null) {
                    BatchGetServiceUtil.dealLoop(arg, result);
                }
                return null;
            }
        };
        List<Get> list = new ArrayList<>(Constant.BATCH_GROUP_SIZE);
        int count = 0;
        List<AsyncGet> lists = new ArrayList<>();
        for (GetItem item : getItems) {
            Get get = new Get(item.getRowkey());
            get.addFamily(item.getFamily());
            list.add(get);
            count++;
            if (count % Constant.BATCH_GROUP_SIZE == 0) {
                AsyncGet aget = new AsyncGet(list);
                aget.getDeferred().addBoth(cb);
                lists.add(aget);
                list = new ArrayList<>(Constant.BATCH_GROUP_SIZE);
            }

        }
        lists.add(new AsyncGet(list));

        for (AsyncGet l : lists) {
            pool.submit(l);
        }
        try {
            latch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    static class AsyncGet implements Callable<Boolean> {

        private List<Get> gets;

        private Deferred<Result[]> deferred;

        AsyncGet(List<Get> gets) {
            this.gets = gets;
            this.deferred = new Deferred<>();
        }

        final Deferred<Result[]> getDeferred() {
            return deferred;
        }

        @Override
        public Boolean call() throws Exception {
            if (gets.isEmpty()) {
                return false;
            }
            Result[] rr = HtableUtil.I.getHtable(Constant.HTABLE).get(gets);

            deferred.callback(rr);
            return true;
        }
    }
}
