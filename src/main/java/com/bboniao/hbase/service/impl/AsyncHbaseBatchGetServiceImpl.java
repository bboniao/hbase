package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;

import java.util.List;

/**
 * 采用asynchbase的方式来批量get
 * Created by bboniao on 11/5/14.
 */
public class AsyncHbaseBatchGetServiceImpl implements BatchGetService {
    @Override
    public List<GetItem> batch(List<GetItem> getItems) {
        return null;
    }
}
