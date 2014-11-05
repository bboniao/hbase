package com.bboniao.hbase.pojo;

/**
 * 保存get需要用的信息
 * Created by bboniao on 11/5/14.
 */
public class GetItem {

    private byte[] table;

    private byte[] rowkey;

    private byte[] family;

    private byte[] qualifier;

    private int versions;

    private byte[] value;

    public GetItem(byte[] table, byte[] rowkey, byte[] family, byte[] qualifier, int versions) {
        this.table = table;
        this.rowkey = rowkey;
        this.family = family;
        this.qualifier = qualifier;
        this.versions = versions;
    }

    public byte[] getTable() {
        return table;
    }

    public byte[] getRowkey() {
        return rowkey;
    }

    public byte[] getFamily() {
        return family;
    }

    public byte[] getQualifier() {
        return qualifier;
    }

    public int getVersions() {
        return versions;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
}
