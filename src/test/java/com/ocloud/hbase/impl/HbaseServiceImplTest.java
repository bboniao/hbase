package com.ocloud.hbase.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.WritableByteArrayComparable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.ocloud.hbase.HbaseService;
import com.ocloud.hbase.HbaseUtil;

// 指定要使用的测试框架
@RunWith(SpringJUnit4ClassRunner.class)
//指定配置文件的位置,可以配置多个
@ContextConfiguration({"/spring/applicationContext.xml"})
// 如果测试已修改上下文（例如，更换一个bean定义），使用此批注。随后的测试提供了一个新的上下文。
// 有两种模式可选，一种是方法级别的，一种是类级别的，默认类级别的
//@TestExecutionListeners
//@IfProfileValue 需要研究一下是什么意思
//@ProfileValueSourceConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
//配置事物
@TransactionConfiguration(transactionManager ="transactionManager",defaultRollback = false) 
public class HbaseServiceImplTest {
    

	@Autowired
	private HbaseService hbaseService;

//	@Test
	public void insert() throws Exception {
		Map<String, Map<String, Object>> keyValue1 = new HashMap<String, Map<String, Object>>();
		Map<String, Object> keyValue = new HashMap<String, Object>();
		keyValue.put("log:cc", "cc");
		keyValue.put("log:dd", "dd");
		keyValue1.put("test2", keyValue);
		hbaseService.insert("test", keyValue1);

		Map<String, Object> keyValue2 = new HashMap<String, Object>();
		keyValue2.put("aa", "aa");
		keyValue2.put("bb", "bb");
		hbaseService.insert("test", "test1", "log", keyValue2);

		hbaseService.insert("test", "test3", "log", "ee", "ee");
	}

//	@Test
	public void get() throws Exception {
		String rr = (String) hbaseService.get("test", "test3", "log", "ee",
				String.class);
		Assert.assertEquals("ee", rr);

		Map<String, Class<?>> keys = new HashMap<String, Class<?>>();
		keys.put("log:aa", String.class);
		keys.put("log:bb", String.class);
		Map<String, Object> re = hbaseService.get("test", "test1", keys);
		Assert.assertEquals("aa", re.get("log:aa"));
		Assert.assertEquals("bb", re.get("log:bb"));
	}

//	@Test
	public void exists() throws Exception {
		Assert.assertEquals(true,
				hbaseService.exists("test", "test3", "log", "ee"));
	}

//	@Test
	public void increaseValue() throws Exception {
		hbaseService.increaseValue("test", "test4", "log", "count", 10L);

		Map<String, Long> keyValue = new HashMap<String, Long>();
		keyValue.put("count1", 10L);
		keyValue.put("count2", 10L);
		hbaseService.increaseValue("test", "test4", "log", keyValue);
	}

//	@Test
	public void getMap() throws Exception {
		Map<String, Class<?>> keys = new HashMap<String, Class<?>>();
		keys.put("log:ee", String.class);

		Map<String, Map<String, Object>> rr = hbaseService.getMap("test",
				"test1", "test9", keys);
		Assert.assertEquals(1, rr.size());
		Assert.assertArrayEquals(new String[] { "test3" }, rr.keySet()
				.toArray());
		for (Map.Entry<String, Map<String, Object>> e : rr.entrySet()) {
			for (Map.Entry<String, Object> en : e.getValue().entrySet()) {
				Assert.assertEquals("ee", en.getValue());
			}
		}
	}

//	@Test
	public void getList() throws Exception {

		List<Object> rr = hbaseService.getList("test", "test1", "test9", "log",
				"ee", String.class);
		Assert.assertEquals(1, rr.size());
		for (Object en : rr) {
			Assert.assertEquals("ee", en.toString());
		}
	}
	
	@Test
	public void getMapWithFilter() throws Exception {
		Map<String, Class<?>> keys = new HashMap<String, Class<?>>();
		keys.put("kpi:value", Double.class);
		
		FilterList fl =new FilterList();
		fl.addFilter(new SingleColumnValueFilter(HbaseUtil.E.toByte("kpi"), HbaseUtil.E.toByte("value"), CompareFilter.CompareOp.GREATER ,HbaseUtil.E.toByte(1.0) ));
		
		WritableByteArrayComparable rowComparator =new RegexStringComparator("TRF12B");
		RowFilter rf = new RowFilter(CompareFilter.CompareOp.EQUAL,rowComparator);
		fl.addFilter(rf);
		
		Map<String, Map<String, Object>> rr = hbaseService.getMap("tidal",
				"1_426407410_TRF12B_0", "1_426407410_TRF12B_9", keys,fl);
		for (Map.Entry<String, Map<String, Object>> e : rr.entrySet()) {
			Assert.assertTrue(e.getKey().contains("TRF12B"));
			for (Map.Entry<String, Object> en : e.getValue().entrySet()) {
				Assert.assertTrue(Double.parseDouble(en.getValue().toString()) > 1.0);
			}
		}
	}

}
