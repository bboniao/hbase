package com.bboniao.hbase.split;

/**
 * split算法
 * Created by bboniao on 11/6/14.
 */
public interface SplitAlgorithm {

    public byte[][] split(int n);
}
