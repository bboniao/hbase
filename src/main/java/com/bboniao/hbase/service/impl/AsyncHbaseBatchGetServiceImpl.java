package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.service.BatchGetServiceUtil;
import com.bboniao.hbase.util.AsyncHbaseUtil;
import com.bboniao.hbase.util.Constant;
import com.stumbleupon.async.Callback;
import org.hbase.async.GetRequest;
import org.hbase.async.KeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 采用asynchbase的方式来批量get
 * Created by bboniao on 11/5/14.
 */
public class AsyncHbaseBatchGetServiceImpl implements BatchGetService {
    @Override
    public  Map<String, Map<String,String>> batch(List<GetItem> getItems) {
        final  Map<String, Map<String,String>> result = new ConcurrentHashMap<>(getItems.size());
        final CountDownLatch latch = new CountDownLatch(getItems.size());

        for (GetItem item : getItems) {
            GetRequest get = new GetRequest(Constant.HTABLE, item.getRowkey(), item.getFamily());
            AsyncHbaseUtil.I.getClient().get(get).addBoth(new Callback<Object, ArrayList<KeyValue>>() {
                @Override
                public Object call(ArrayList<KeyValue> arg) throws Exception {
                    latch.countDown();
                    if(arg != null && !arg.isEmpty()) {
                        BatchGetServiceUtil.dealLoop(arg, result);
                    }
                    return null;
                }
            });
        }
        try {
            latch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
