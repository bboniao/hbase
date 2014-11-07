package com.bboniao.hbase.util;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;

import java.io.IOException;

/**
 * 原生hbase client
 * Created by bboniao on 11/6/14.
 */
public enum HtableUtil {

    I;

    private HConnection connection;

    HtableUtil() {
        try {
            connection = HConnectionManager.createConnection(HBaseConfiguration.create());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HTableInterface getHtable(byte[] table) {
        try {
            return connection.getTable(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
