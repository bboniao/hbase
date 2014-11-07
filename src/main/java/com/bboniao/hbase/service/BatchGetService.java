package com.bboniao.hbase.service;

import com.bboniao.hbase.pojo.GetItem;
import java.util.List;
import java.util.Map;

/**
 * 批量抓取
 * Created by bboniao on 11/5/14.
 */
public interface BatchGetService {

    /**
     * 批量抓取
     */
    public Map<String, Map<String,String>> batch(List<GetItem> getItems);
}
