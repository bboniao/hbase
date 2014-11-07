package com.bboniao.hbase.input;

import com.bboniao.hbase.split.MD5StringSplit;
import com.bboniao.hbase.split.SplitAlgorithm;
import com.bboniao.hbase.util.Constant;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.regionserver.BloomType;

import java.io.IOException;

/**
 * 创建htable
 * Created by bboniao on 11/6/14.
 */
public class CreateHtable {

    public void createHtable(String htable, int regionSize) throws IOException {
        SplitAlgorithm splitAlgorithm = new MD5StringSplit();
        byte[][] splits = splitAlgorithm.split(regionSize);

        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(htable));
        HColumnDescriptor coldef = new HColumnDescriptor(Constant.F);
        coldef.setBlockCacheEnabled(true);
        coldef.setBloomFilterType(BloomType.ROWCOL);
        coldef.setCompactionCompressionType(Compression.Algorithm.LZO);
        coldef.setCompressionType(Compression.Algorithm.LZO);
        coldef.setMaxVersions(1);
        coldef.setBlocksize(16384);

        desc.addFamily(coldef);
        HBaseAdmin admin = new HBaseAdmin(HBaseConfiguration.create());
        admin.createTable(desc, splits);
        admin.close();
    }

    public static void main(String[] args) throws IOException {
        new CreateHtable().createHtable("rc_feature", 8);
    }
}
