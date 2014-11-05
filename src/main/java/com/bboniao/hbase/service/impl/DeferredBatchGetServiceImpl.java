package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;

import java.util.List;

/**
 * 使用com.stumbleupon.async实现批量get
 * Created by bboniao on 11/5/14.
 */
public class DeferredBatchGetServiceImpl implements BatchGetService {
    @Override
    public List<GetItem> batch(List<GetItem> getItems) {
        return null;
    }
}
