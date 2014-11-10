package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.service.BatchGetServiceUtil;
import com.bboniao.hbase.util.AsyncThreadPoolFactory;
import com.bboniao.hbase.util.Constant;
import com.bboniao.hbase.util.HtableUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 没有超时机制
 * Created by bboniao on 11/5/14.
 */
public class AsyncJdkBatchGetServiceImplV1 implements BatchGetService {
    @Override
    public  Map<String, Map<String,String>> batch(List<GetItem> getItems) {
        final  Map<String, Map<String,String>> result = new ConcurrentHashMap<>(getItems.size());
        final CountDownLatch latch = new CountDownLatch(getItems.size());

        int count = 0;
        List<Get> list = new ArrayList<>(Constant.BATCH_GROUP_SIZE);
        for (GetItem item : getItems) {
            Get get = new Get(item.getRowkey());
            get.addFamily(item.getFamily());
            list.add(get);
            count++;
            if (count % Constant.BATCH_GROUP_SIZE == 0) {
                AsyncThreadPoolFactory.ASYNC_HBASE_THREAD_POOL.submit(new Task(list, result));
                list = new ArrayList<>(Constant.BATCH_GROUP_SIZE);
                latch.countDown();
            }
        }
        AsyncThreadPoolFactory.ASYNC_HBASE_THREAD_POOL.submit(new Task(list, result));
        try {
            latch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private final class Task implements Callable<Boolean> {

        private List<Get> get;

        private Map<String, Map<String,String>> result;

        private Task(List<Get> get, Map<String, Map<String, String>> result) {
            this.get = get;
            this.result = result;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                final Result[] r = HtableUtil.I.getHtable(Constant.HTABLE).get(get);
                BatchGetServiceUtil.dealLoop(r, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}
