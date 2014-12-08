package com.bboniao.asynchbase;

/**
 * keyvalue
 * Created by bboniao on 11/19/14.
 */
public class KV {

    private byte[] row;

    private byte[] family;

    private byte[] qulifier;

    private long timestamp;

    private byte type;

    private byte[] value;

    public KV(byte[] row, byte[] family, byte[] qulifier, long timestamp, byte type, byte[] value) {
        this.row = row;
        this.family = family;
        this.qulifier = qulifier;
        this.timestamp = timestamp;
        this.type = type;
        this.value = value;
    }

    public byte[] getRow() {
        return row;
    }

    public byte[] getFamily() {
        return family;
    }

    public byte[] getQulifier() {
        return qulifier;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }
}
