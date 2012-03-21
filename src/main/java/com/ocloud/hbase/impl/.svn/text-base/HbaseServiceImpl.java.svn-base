package com.ocloud.hbase.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocloud.hbase.HbaseBaseService;
import com.ocloud.hbase.HbaseService;
import com.ocloud.hbase.HbaseUtil;

@Service
public class HbaseServiceImpl implements HbaseService {
	
	//family和clounm的分隔符
	public static final String SEPARATOR=":";
	
	@Autowired
	private HbaseBaseService hbaseBaseService;

	public void setHbaseBaseService(HbaseBaseService hbaseBaseService) {
		this.hbaseBaseService = hbaseBaseService;
	}

	@Override
	public void insert(String tableName, Map<String, Map<String, Object>> keyValue) throws Exception {
		if(!hbaseBaseService.isOpenHbase())
			return;
		List<Put> puts = new ArrayList<Put>();
		for(Entry<String, Map<String, Object>> entrys : keyValue.entrySet()){
			Put put = new Put(HbaseUtil.E.toByte(entrys.getKey()));
			for (Entry<String, Object> entry : entrys.getValue().entrySet()) {
				String[] keys = entry.getKey().split(SEPARATOR);
				put.add(HbaseUtil.E.toByte(keys[0]), HbaseUtil.E.toByte(keys[1]), HbaseUtil.E.toByte(entry
						.getValue()));
			}
			puts.add(put);
		}
		hbaseBaseService.put(tableName, puts);
	}
	
	@Override
	public void insert(String tableName, String rowKey, String family,
			Map<String, Object> keyValue) throws Exception {
		if(!hbaseBaseService.isOpenHbase())
			return;
		Put put = new Put(HbaseUtil.E.toByte(rowKey));
		for (Entry<String, Object> entry : keyValue.entrySet()) {
			put.add(HbaseUtil.E.toByte(family), HbaseUtil.E.toByte(entry.getKey()), HbaseUtil.E.toByte(entry
					.getValue()));
		}
		hbaseBaseService.put(tableName, put);
	}
	
	@Override
	public void insert(String tableName, String rowKey, String family,
			String column,Object value) throws Exception {
		if(!hbaseBaseService.isOpenHbase())
			return;
		Put put = new Put(HbaseUtil.E.toByte(rowKey));
		put.add(HbaseUtil.E.toByte(family), HbaseUtil.E.toByte(column), HbaseUtil.E.toByte(value));
		hbaseBaseService.put(tableName, put);
	}
	
	@Override
	public Object get(String tableName, String rowName, String family,
			String colunm, Class<?> clazz) throws Exception {
		if(!hbaseBaseService.isOpenHbase())
			return null;
		Get get = new Get(HbaseUtil.E.toByte(rowName));
		Result result = hbaseBaseService.get(tableName, get);
		return HbaseUtil.E.toType(result.getValue(
				HbaseUtil.E.toByte(family), HbaseUtil.E.toByte(colunm)), clazz);
	}
	
	@Override
	public boolean exists(String tableName,String rowName,String family,String column) throws Exception{
		if(!hbaseBaseService.isOpenHbase())
			return false;
		Get get = new Get(HbaseUtil.E.toByte(rowName));
		get.addColumn(HbaseUtil.E.toByte(family), HbaseUtil.E.toByte(column));
		return hbaseBaseService.exists(tableName, get);
	}

	@Override
	public long increaseValue(String tableName, String rowKey,
			String family, String colunm, long value) throws Exception {
		if(!hbaseBaseService.isOpenHbase())
			return 0L;
		return hbaseBaseService.increaseValue(tableName, rowKey, family, colunm, value);
	}

	@Override
	public Map<String,Long> increaseValue(String tableName, String rowKey,
			String family, Map<String,Long> keyValue) throws Exception {
		if(!hbaseBaseService.isOpenHbase())
			return Collections.emptyMap();
		Increment increment = new Increment(HbaseUtil.E.toByte(rowKey));
		for (Entry<String, Long> entry : keyValue.entrySet()) {
			increment.addColumn(HbaseUtil.E.toByte(family), HbaseUtil.E.toByte(entry.getKey()), entry
					.getValue());
		}
		
		Result result = hbaseBaseService.increment(tableName, increment);
		Map<String,Long> r = new HashMap<String, Long>();
		for (Entry<String, Long> entry : keyValue.entrySet()) {
			r.put(entry.getKey(), (Long)HbaseUtil.E.toType(result.getValue(HbaseUtil.E.toByte(family), HbaseUtil.E.toByte(entry.getKey())),long.class));
		}
		return r;
	}

	@Override
	public Map<String, Object> get(String tableName, String rowkey,Map<String,Class<?>> keys)
			throws Exception {
		if(!hbaseBaseService.isOpenHbase())
			return Collections.emptyMap();
		Get get = new Get(HbaseUtil.E.toByte(rowkey));
		for(Map.Entry<String,Class<?>> key : keys.entrySet()){
			String[] k = key.getKey().split(SEPARATOR);
			get.addColumn(HbaseUtil.E.toByte(k[0]), HbaseUtil.E.toByte(k[1]));	
		}
		
		Result rr = hbaseBaseService.get(tableName, get);
		Map<String, Object> result = new HashMap<String,Object>();
		for(Map.Entry<String,Class<?>> key : keys.entrySet()){
			String[] k = key.getKey().split(SEPARATOR);
			result.put(key.getKey(), HbaseUtil.E.toType(rr.getValue(HbaseUtil.E.toByte(k[0]), HbaseUtil.E.toByte(k[1])), key.getValue()));
		}
		return result;
	}
	
	@Override
	public Map<String,Map<String,Object>> getMap(String tableName,  String startRowKey, String endRowKey,
			Map<String,Class<?>> keys) throws Exception {
		return getMap(tableName, startRowKey, endRowKey, keys,null);
	}
	
	@Override
	public List<Object> getList(String tableName,  String startRowKey, String endRowKey,String family,String colunm,Class<?> clazz) throws Exception {
		return getList(tableName, startRowKey, endRowKey, family, colunm, clazz, null);
	}

	@Override
	public List<Object> getList(String tableName, String startRowKey,
			String endRowKey, String family, String colunm, Class<?> clazz,
			FilterList fl) throws Exception {
		if(!hbaseBaseService.isOpenHbase())
			return Collections.emptyList();
		Scan scan = new Scan(HbaseUtil.E.toByte(startRowKey), HbaseUtil.E.toByte(endRowKey));
		if(fl != null)
			scan.setFilter(fl);
		scan.addColumn(HbaseUtil.E.toByte(family), HbaseUtil.E.toByte(colunm));	
		ResultScanner rs = hbaseBaseService.getScanner(tableName, scan);
		List<Object> result = new ArrayList<Object>();
		try {
			for (Result rr = rs.next(); rr != null; rr = rs.next()) {
				result.add(HbaseUtil.E.toType(rr.getValue(HbaseUtil.E.toByte(family), HbaseUtil.E.toByte(colunm)), clazz));
			}
		} finally {
			rs.close();
		}
		return result;
	}

	@Override
	public Map<String, Map<String, Object>> getMap(String tableName,
			String startRowKey, String endRowKey, Map<String, Class<?>> keys,
			FilterList fl) throws Exception {
		if(!hbaseBaseService.isOpenHbase())
			return Collections.emptyMap();
		Scan scan = new Scan(HbaseUtil.E.toByte(startRowKey), HbaseUtil.E.toByte(endRowKey));
		if(fl != null)
			scan.setFilter(fl);
		for(Map.Entry<String,Class<?>> key : keys.entrySet()){
			String[] k = key.getKey().split(SEPARATOR);
			scan.addColumn(HbaseUtil.E.toByte(k[0]), HbaseUtil.E.toByte(k[1]));	
		}
		ResultScanner rs = hbaseBaseService.getScanner(tableName, scan);
		Map<String,Map<String,Object>> result = new HashMap<String,Map<String,Object>>();
		try {
			for (Result rr = rs.next(); rr != null; rr = rs.next()) {
				Map<String, Object> map = new HashMap<String,Object>();
				for(Map.Entry<String,Class<?>> key : keys.entrySet()){
					String[] k = key.getKey().split(SEPARATOR);
					map.put(key.getKey(), HbaseUtil.E.toType(rr.getValue(HbaseUtil.E.toByte(k[0]), HbaseUtil.E.toByte(k[1])), key.getValue()));
				}
				result.put((String)HbaseUtil.E.toType(rr.getRow(), String.class), map);
			}
		} finally {
			rs.close();
		}
		return result;
	}

}