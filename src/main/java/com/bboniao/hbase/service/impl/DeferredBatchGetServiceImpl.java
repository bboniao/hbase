package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.util.AsyncThreadPoolFactory;
import com.bboniao.hbase.util.Constant;
import com.bboniao.hbase.util.HtableUtil;
import com.stumbleupon.async.Callback;
import com.stumbleupon.async.Deferred;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 使用com.stumbleupon.async实现批量get
 * Created by bboniao on 11/5/14.
 */
public class DeferredBatchGetServiceImpl implements BatchGetService {

    private ExecutorService pool = AsyncThreadPoolFactory.ASYNC_HBASE_THREAD_POOL;

    @Override
    public List<String> batch(List<GetItem> getItems) {
        final List<String> result = new ArrayList<>(getItems.size());
        final CountDownLatch latch = new CountDownLatch(getItems.size());

        for (GetItem item : getItems) {
            Get get = new Get(item.getRowkey());
            get.addFamily(item.getFamily());

            AsyncGet asyncGet = new AsyncGet(get);
            asyncGet.getDeferred().addBoth(new Callback<Object, String>() {
                @Override
                public Object call(String arg) throws Exception {
                    latch.countDown();
                    if(arg != null) {
                        result.add(arg);
                    }
                    return null;
                }
            });

            pool.submit(asyncGet);
        }
        try {
            latch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    static class AsyncGet implements Callable<Boolean> {

        private Get get;

        private Deferred<String> deferred;

        AsyncGet(Get get) {
            this.get = get;
            this.deferred = new Deferred<>();
        }

        final void callback(final String result) {
            deferred.callback(result);
        }

        final Deferred<String> getDeferred() {
            return deferred;
        }

        @Override
        public Boolean call() throws Exception {
            Result r = HtableUtil.I.getHtable(Constant.HTABLE).get(get);
            if (r != null && r.value() != null) {
                callback(new String(r.value()));
            }
            return true;
        }
    }
}
