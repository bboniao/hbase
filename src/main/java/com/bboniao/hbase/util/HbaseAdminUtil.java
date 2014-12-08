package com.bboniao.hbase.util;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import java.io.IOException;

/**
 *
 * Created by bboniao on 11/20/14.
 */
public enum HbaseAdminUtil {

    I;

    private HBaseAdmin hd;

    private HbaseAdminUtil() {
        try {
            hd = new HBaseAdmin(HBaseConfiguration.create());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HBaseAdmin getAdmin() {
        return hd;
    }
}
