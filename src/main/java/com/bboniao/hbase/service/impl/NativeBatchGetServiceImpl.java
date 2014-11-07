package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.util.Constant;
import com.bboniao.hbase.util.HtableUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * hbase原生批量get实现. 内部实现也采用异步
 * Created by bboniao on 11/5/14.
 */
public class NativeBatchGetServiceImpl implements BatchGetService {
    @Override
    public List<String> batch(List<GetItem> getItems) {
        List <Get> l = new ArrayList<>(getItems.size());
        for (GetItem item : getItems) {
            Get g = new Get(item.getRowkey());
            g.addFamily(item.getFamily());
            l.add(g);
        }
        HTableInterface htable = HtableUtil.I.getHtable(Constant.HTABLE);
        try {
            Result[] r = htable.get(l);
            List<String> result = new ArrayList<>(r.length);
            for (Result re : r) {
                result.add(new String(re.value()));
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
