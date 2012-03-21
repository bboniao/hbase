package com.ocloud.hbase;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.filter.FilterList;

public interface HbaseService {
	
	/**
	 * 批量插入数据
	 * @param tableName
	 * @param keyValue 外层key为rowkey，内层key为family:clounm
	 * @throws Exception
	 */
	public void insert(String tableName, Map<String, Map<String, Object>> keyValue) throws Exception;
	
	/**
	 * 插入单列多条数据
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param keyValue key为clounm
	 * @throws Exception
	 */
	public void insert(String tableName, String rowKey, String family,
			Map<String, Object> keyValue) throws Exception;
	
	/**
	 * 插入单列单条数据
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param column
	 * @param value
	 * @throws Exception
	 */
	public void insert(String tableName, String rowKey, String family,
			String column,Object value) throws Exception;
	
	/**
	 * 获得单条记录
	 * @param tableName
	 * @param rowName
	 * @param family
	 * @param colunm
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public Object get(String tableName, String rowName, String family,
			String colunm, Class<?> clazz) throws Exception;
	
	/**
	 * 判断是否存在
	 * @param tableName
	 * @param rowName
	 * @param family
	 * @param column
	 * @return
	 * @throws Exception
	 */
	public boolean exists(String tableName,String rowName,String family,String column) throws Exception;
	
	/**
	 * 累加
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param colunm
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public long increaseValue(String tableName, String rowKey,
			String family, String colunm, long value) throws Exception ;

	/**
	 * 批量累加一列的多个属性
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param keyValue
	 * @return
	 * @throws Exception
	 */
	public Map<String,Long> increaseValue(String tableName, String rowKey,
			String family, Map<String,Long> keyValue) throws Exception;
	
	/**
	 * 获得一列的多个数值
	 * @param tableName
	 * @param rowkey
	 * @param keys key为family:colunm,value为存放值的class
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> get(String tableName, String rowkey,Map<String,Class<?>> keys)
			throws Exception;
	
	/**
	 * 获得多列的多个数据
	 * @param tableName
	 * @param startRowKey
	 * @param endRowKey
	 * @param keys key为family:colunm,value为存放值的class
	 * @return
	 * @throws Exception
	 */
	public Map<String,Map<String,Object>> getMap(String tableName,  String startRowKey, String endRowKey,
			Map<String,Class<?>> keys) throws Exception;
	
	/**
	 * 获得多列的一个数据
	 * @param tableName
	 * @param startRowKey
	 * @param endRowKey
	 * @param family
	 * @param colunm
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public List<Object> getList(String tableName,  String startRowKey, String endRowKey,String family,String colunm,Class<?> clazz,FilterList fl) throws Exception;
	
	/**
	 * 获得多列的多个数据
	 * @param tableName
	 * @param startRowKey
	 * @param endRowKey
	 * @param keys key为family:colunm,value为存放值的class
	 * @return
	 * @throws Exception
	 */
	public Map<String,Map<String,Object>> getMap(String tableName,  String startRowKey, String endRowKey,
			Map<String,Class<?>> keys,FilterList fl) throws Exception;
	
	/**
	 * 获得多列的一个数据
	 * @param tableName
	 * @param startRowKey
	 * @param endRowKey
	 * @param family
	 * @param colunm
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public List<Object> getList(String tableName,  String startRowKey, String endRowKey,String family,String colunm,Class<?> clazz) throws Exception;
	
}
