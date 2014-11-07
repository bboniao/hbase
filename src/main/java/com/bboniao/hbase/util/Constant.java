package com.bboniao.hbase.util;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * 静态变量
 * Created by bboniao on 11/6/14.
 */
public final class Constant {

    public final static String TAB = "\t";

    public final static String COLON = ":";

    public final static int BATCH_SIZE = 1000;

    public static final String UNDERSCORE = "_";

    public static final String VERTICAL_ORIGIN = "|";

    public static final String VIDEO_TYPE_VER_ID = "2";

    public static final byte[] F = Bytes.toBytes("f");

    public static final byte[] HTABLE = Bytes.toBytes("rc_feature");

    public static final String ZOOKEEPER = "10.16.14.251";

    public static final String ZOOKEEPER_PATH = "/hbase-test";
}
