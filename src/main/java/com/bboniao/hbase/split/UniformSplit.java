package com.bboniao.hbase.split;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.ArrayUtils;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Arrays;

/**
 * 适合以long(int等)型rowkey的spilt算法
 * Created by bboniao on 11/6/14.
 */
public class UniformSplit implements SplitAlgorithm {
    private static final byte XFF = (byte) 0xFF;
    private static final byte[] firstRowBytes = ArrayUtils.EMPTY_BYTE_ARRAY;
    private static final byte[] lastRowBytes = new byte[] {XFF, XFF, XFF, XFF, XFF, XFF, XFF, XFF };
    @Override
    public byte[][] split(int n) {
        Preconditions.checkArgument(Bytes.compareTo(lastRowBytes, firstRowBytes) > 0,
                "last row (%s) is configured less than first row (%s)", Bytes.toStringBinary(lastRowBytes),
                Bytes.toStringBinary(firstRowBytes));

        byte[][] splits = Bytes.split(firstRowBytes, lastRowBytes, n - 1);
        if (splits == null) {
            Preconditions.checkState(Boolean.FALSE, "Could not split region with given user input: " + this);
            return null;
        }

        // remove endpoints, which are included in the splits list
        return Arrays.copyOfRange(splits, 1, splits.length - 1);
    }
}
