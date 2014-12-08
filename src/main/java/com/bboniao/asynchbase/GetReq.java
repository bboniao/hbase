package com.bboniao.asynchbase;

import java.util.Map;
import java.util.NavigableSet;

/**
 * Created by bboniao on 11/19/14.
 */
public class GetReq {

    private byte[] row;

    private boolean cacheBlocks;

    private int maxVersions = 1;

    private Map<byte[], NavigableSet<byte[]>> families;

    public GetReq(byte[] row) {
        this.row = row;
    }

    public boolean hasFamilies() {
        return families != null && !families.isEmpty();
    }

    public Map<byte[], NavigableSet<byte[]>> getFamilyMap() {
        return families;
    }

    public int getMaxVersions() {
        return maxVersions;
    }

    public void setMaxVersions(int maxVersions) {
        this.maxVersions = maxVersions;
    }

    public boolean getCacheBlocks() {
        return cacheBlocks;
    }

    public void setCacheBlocks(boolean cacheBlocks) {
        this.cacheBlocks = cacheBlocks;
    }

    public byte[] getRow() {
        return row;
    }

    public void setRow(byte[] row) {
        this.row = row;
    }
}
