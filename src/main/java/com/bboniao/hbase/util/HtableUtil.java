package com.bboniao.hbase.util;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;

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

    public void close() throws IOException {
        this.connection.close();
    }

    public static void main(String[] args) throws IOException {
        Result r = HtableUtil.I.getHtable("rc_feature".getBytes()).get(new Get("000008e46cd4286ff5d2dd8bd5682920|2|19763476".getBytes()));
        System.out.println(r);
    }
}
