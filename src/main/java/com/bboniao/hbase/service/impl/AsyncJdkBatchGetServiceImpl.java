package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.service.BatchGetServiceUtil;
import com.bboniao.hbase.util.AsyncThreadPoolFactory;
import com.bboniao.hbase.util.Constant;
import com.bboniao.hbase.util.HtableUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 采用jdk异步方式批量get
 * Created by bboniao on 11/5/14.
 */
public class AsyncJdkBatchGetServiceImpl implements BatchGetService {
    @Override
    public Map<String, Map<String,String>> batch(List<GetItem> getItems) {
        Map<String, Map<String,String>> result = new ConcurrentHashMap<>();

        List<Get> gets = new ArrayList<>(getItems.size());
        for (GetItem getItem : getItems) {
            Get g = new Get(getItem.getRowkey());
            g.addFamily(getItem.getFamily());
            gets.add(g);
        }
        GetGroupTask task = new GetGroupTask(gets, result);
        Future<Boolean> future = AsyncThreadPoolFactory.ASYNC_HBASE_THREAD_POOL.submit(task);
        Boolean r = Boolean.FALSE;
        try {
            r = future.get(500, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ignored) {
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (!r) {
            future.cancel(Boolean.FALSE);
        }
        return result;
    }

    private static class GetGroupTask implements Callable<Boolean> {

        private List<Get> gets;

        private Map<String, Map<String,String>> result;

        private GetGroupTask(List<Get> gets, Map<String, Map<String,String>> result) {
            this.gets = gets;
            this.result = result;
        }

        @Override
        public Boolean call() throws Exception {
            List<Future<Boolean>> futureList = new ArrayList<>();
            List<Get> getList = new ArrayList<>(20);
            int count = 0;
            for (Get get : gets) {
                getList.add(get);
                if (count % 20 == 0) {
                    Future<Boolean> f = AsyncThreadPoolFactory.ASYNC_HBASE_THREAD_POOL.submit(new GetTask(getList, result));
                    futureList.add(f);
                    getList = new ArrayList<>(20);
                }

                count++;
            }
            Future<Boolean> f = AsyncThreadPoolFactory.ASYNC_HBASE_THREAD_POOL.submit(new GetTask(getList, result));
            futureList.add(f);


            boolean isOk = false;
            for (Future<Boolean> future : futureList) {
                try {
                    isOk = future.get(500, TimeUnit.MILLISECONDS);
                } catch (TimeoutException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    //中断未提交任务,已经完成的任务直接返回.
                    if (!isOk) {
                        future.cancel(Boolean.FALSE);
                    }
                }
            }
            return isOk;
        }
    }

    private static class GetTask implements Callable<Boolean> {

        private List<Get> gets;

        private Map<String, Map<String,String>> result;

        private GetTask(List<Get> gets, Map<String, Map<String,String>> result) {
            this.gets = gets;
            this.result = result;
        }

        @Override
        public Boolean call() throws Exception {
            Result[] rr = HtableUtil.I.getHtable(Constant.HTABLE).get(this.gets);
            if (rr != null) {
                BatchGetServiceUtil.dealLoop(rr, result);
                return true;
            } else {
                return false;
            }
        }
    }
}
