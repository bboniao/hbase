package com.ocloud.hbase;

import java.util.List;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

public interface HbaseBaseService {
	
	/**
	 * 是否开放Hbase存储
	 * @return
	 */
	public boolean isOpenHbase();
	
	/**
	 * 插入数据库的时候刷新数据
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public HTableInterface getHTablesWithFlush(String tableName) throws Exception;
	
	/**
	 * 归还Htable实例(没有实现)
	 * @param obj
	 * @throws Exception
	 */
	public void returnHTables(HTableInterface obj) throws Exception;
	
	/**
	 * 插入数据库的时候缓存数据
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public HTableInterface getHTablesWithCache(String tableName) throws Exception;
	
	/**
	 * 原子方式增加值
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param colunm
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public long increaseValue(String tableName, String rowKey,
			String family, String colunm, long value) throws Exception;
	
	/**
	 * 插入，没有缓存，执行之后就会有效果
	 * @param tableName
	 * @param put
	 * @throws Exception
	 */
	public void put(String tableName,Put put) throws Exception;
	
	/**
	 * 批量插入，先缓存，之后一起flush
	 * @param tableName
	 * @param puts
	 * @throws Exception
	 */
	public void put(String tableName,List<Put> puts) throws Exception;
	
	/**
	 * 删除，没有缓存，执行之后就会有效果
	 * @param tableName
	 * @param del
	 * @throws Exception
	 */
	public void del(String tableName,Delete del) throws Exception;
	
	/**
	 * 批量删除，先缓存，之后一起flush
	 * @param tableName
	 * @param puts
	 * @throws Exception
	 */
	public void del(String tableName,List<Delete> dels) throws Exception;
	
	/**
	 * 获得一列
	 * @param tableName
	 * @param get
	 * @return
	 * @throws Exception
	 */
	public Result get(String tableName,Get get) throws Exception;
	
	/**
	 * 获得多列
	 * @param tableName
	 * @param get
	 * @return
	 * @throws Exception
	 */
	public Result[] get(String tableName,List<Get> get) throws Exception;
	
	/**
	 * 对一列多个属性加值
	 * @param tableName
	 * @param increment
	 * @return
	 * @throws Exception
	 */
	public Result increment(String tableName,Increment increment) throws Exception;
	
	/**
	 * 判断是否存在
	 * @param tableName
	 * @param get
	 * @return
	 * @throws Exception
	 */
	public boolean exists(String tableName,Get get) throws Exception;
	
	/**
	 * 批量抓取一组数据
	 * @param tableName
	 * @param scan
	 * @return
	 * @throws Exception
	 */
	public ResultScanner getScanner(String tableName,Scan scan) throws Exception;

}
