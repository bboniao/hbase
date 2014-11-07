package com.bboniao.hbase.service;

import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.hbase.async.KeyValue;

import java.util.HashMap;
import java.util.Map;

/**
 * 抽象类
 * Created by bboniao on 11/7/14.
 */
public class BatchGetServiceUtil {

    public static void dealLoop(List<KeyValue> arg, Map<String, Map<String,String>> result) {
        for (KeyValue kv : arg) {
            String rk = new String(kv.key());
            Map<String,String> map = result.get(rk);
            if (map == null) {
                map = new HashMap<>();
                result.put(rk, map);
            }
            map.put(new String(kv.qualifier()), new String(kv.value()));
        }
    }

    public static void dealLoop(Result[] arg, Map<String, Map<String,String>> result) {
        for (Result kv : arg) {
            String rk = new String(kv.getRow());
            Map<String,String> map = result.get(rk);
            if (map == null) {
                map = new HashMap<>();
                result.put(rk, map);
            }
            for (Cell c : kv.rawCells()) {
                map.put(new String(c.getQualifierArray()), new String(c.getValueArray()));    
            }
            
        }
    }
}
