package com.ocloud.hbase.impl;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import com.ocloud.hbase.HbaseUtil;

public class HbaseManageImpl {

	public static void main(String[] args) throws IOException {
		Configuration conf = HBaseConfiguration.create();
		HBaseAdmin admin = new HBaseAdmin(conf);
		admin.assign(HbaseUtil.E.toByte("-ROOT-,,0"));
	}
}
