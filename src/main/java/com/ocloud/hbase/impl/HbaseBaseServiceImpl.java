package com.ocloud.hbase.impl;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import com.ocloud.hbase.HbaseBaseService;
import com.ocloud.hbase.HbaseUtil;

/**
 * Hbase工具类
 * @author gu.haibo
 *
 */

@org.springframework.context.annotation.Configuration
@ImportResource("classpath:/spring/properties-config.xml")
@Service
public class HbaseBaseServiceImpl implements HbaseBaseService {
	
	//HBASE的配置文件
	private static final URL HBASE_SITE=Thread.currentThread().getContextClassLoader().getResource("hbase-site.xml");
	
	//是否开放hbase存储
	@Value("#{hbaseProperties['open_hbase']}")
	private boolean openHbase;
	
	//Htable的池子
	private HTablePool hpool;
	
	//Htable池子的大小
	@Value("#{hbaseProperties['pool_size']}")
	private int poolSize;

	//插入数据是否有缓存
	@Value("#{hbaseProperties['has_cache']}")
	private boolean hasCache;

	//缓存的大小
	@Value("#{hbaseProperties['write_buffer_size']}")
	private int writeBufferSize;
	
	public HbaseBaseServiceImpl() {
		super();
		init();
	}

	/**
	 * 获得一个Htable实例
	 * @param tableName
	 * @param autoFlush 是否自动刷新数据、不缓存
	 * @return
	 * @throws IOException
	 */
	private HTableInterface getHTables(String tableName,boolean autoFlush) throws IOException {
		HTableInterface ht = hpool.getTable(tableName);
		if(!autoFlush && hasCache){
			HTable table = (HTable)ht;
			table.setAutoFlush(false);
			table.setWriteBufferSize(writeBufferSize);
			return table;
		}
		return ht;
	}
	
	@Override
	public HTableInterface getHTablesWithCache(String tableName) throws Exception{
		return getHTables(tableName, false);
	}
	
	@Override
	public HTableInterface getHTablesWithFlush(String tableName) throws Exception{
		return getHTables(tableName, true);
	}
	
	@Override
	public void returnHTables(HTableInterface obj) throws Exception{
//		hpool.putTable(obj);
	}
	
	/**
	 * 初始化HTable池子
	 */
	private void init(){
		if(hpool == null){
			Configuration hc = HBaseConfiguration.create();
			hc.addResource(HBASE_SITE);
			hpool = new HTablePool(hc,poolSize);
		}
	}
	
	@Override
	public long increaseValue(String tableName, String rowKey,
			String family, String colunm, long value) throws Exception {
		if(!isOpenHbase())
			return 0L;
		HTableInterface table = getHTablesWithFlush(tableName);
		long newValue = table.incrementColumnValue(HbaseUtil.E.toByte(rowKey), HbaseUtil.E.toByte(family),
				HbaseUtil.E.toByte(colunm), value);
		returnHTables( table);
		return newValue;
	}

	@Override
	public void put(String tableName, Put put) throws Exception {
		HTableInterface table = getHTablesWithFlush(tableName);
		table.put(put);
		returnHTables( table);
	}

	@Override
	public void put(String tableName, List<Put> puts) throws Exception {
		HTableInterface table = getHTablesWithCache(tableName);
		table.put(puts);
		table.flushCommits();
		returnHTables( table);
		
	}

	@Override
	public void del(String tableName, Delete del) throws Exception {
		HTableInterface table = getHTablesWithFlush(tableName);
		table.delete(del);
		returnHTables( table);
		
	}

	@Override
	public void del(String tableName, List<Delete> dels) throws Exception {
		HTableInterface table = getHTablesWithCache(tableName);
		table.delete(dels);
		table.flushCommits();
		returnHTables( table);
		
	}

	@Override
	public Result get(String tableName, Get get) throws Exception {
		HTableInterface table = getHTablesWithFlush(tableName);
		Result r = table.get(get);
		returnHTables( table);
		return r;
	}

	@Override
	public Result[] get(String tableName, List<Get> gets) throws Exception {
		HTableInterface table = getHTablesWithFlush(tableName);
		Result[] r = table.get(gets);
		returnHTables( table);
		return r;
	}

	@Override
	public Result increment(String tableName, Increment increment)
			throws Exception {
		HTableInterface table = getHTablesWithFlush(tableName);
		Result r = table.increment(increment);
		returnHTables( table);
		return r;
	}

	@Override
	public boolean exists(String tableName, Get get) throws Exception {
		HTableInterface table = getHTablesWithFlush(tableName);
		boolean r = table.exists(get);
		returnHTables( table);
		return r;
	}

	@Override
	public ResultScanner getScanner(String tableName, Scan scan)
			throws Exception {
		HTableInterface table = getHTablesWithFlush(tableName);
		ResultScanner r = table.getScanner(scan);
		returnHTables( table);
		return r;
	}

	
	public void setHasCache(boolean hasCache) {
		this.hasCache = hasCache;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	@Override
	public boolean isOpenHbase() {
		return openHbase;
	}

	public void setOpenHbase(boolean openHbase) {
		this.openHbase = openHbase;
	}

	public void setWriteBufferSize(int writeBufferSize) {
		this.writeBufferSize = writeBufferSize;
	}
	
}

