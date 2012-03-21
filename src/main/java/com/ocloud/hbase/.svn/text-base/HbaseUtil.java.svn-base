package com.ocloud.hbase;

import static org.apache.hadoop.hbase.util.Bytes.toBigDecimal;
import static org.apache.hadoop.hbase.util.Bytes.toBoolean;
import static org.apache.hadoop.hbase.util.Bytes.toDouble;
import static org.apache.hadoop.hbase.util.Bytes.toFloat;
import static org.apache.hadoop.hbase.util.Bytes.toInt;
import static org.apache.hadoop.hbase.util.Bytes.toLong;
import static org.apache.hadoop.hbase.util.Bytes.toShort;
import static org.apache.hadoop.hbase.util.Bytes.toBytes;



import java.math.BigDecimal;

import org.apache.hadoop.hbase.util.Bytes;
public enum HbaseUtil {

	E;
	
	/**
	 * 类型转换成字节
	 * @param obj
	 * @return
	 */
	public byte[] toByte(Object obj){
		if(obj == null)
			return null;
		if(obj instanceof Boolean)
			return toBytes((Boolean)obj);
		else if(obj instanceof Double)
			return toBytes((Double)obj);
		else if(obj instanceof Float)
			return toBytes((Float)obj);
		else if(obj instanceof Integer)
			return toBytes((Integer)obj);
		else if(obj instanceof String)
			return toBytes((String)obj);
		else if(obj instanceof BigDecimal)
			return toBytes((BigDecimal)obj);
		else if(obj instanceof Long)
			return toBytes((Long)obj);
		else if(obj instanceof Short)
			return toBytes((Short)obj);
		throw new IllegalArgumentException("type no support!");
	}
	
	/**
	 * 字节转换成指定类型
	 * @param obj
	 * @param clazz
	 * @return
	 */
	public Object toType(byte [] obj,Class<?> clazz){
		if(clazz == Boolean.class || clazz == boolean.class)
			return toBoolean(obj);
		else if(clazz == Double.class || clazz == double.class)
			return toDouble(obj);
		else if(clazz == Float.class || clazz == float.class)
			return toFloat(obj);
		else if(clazz == Integer.class || clazz == int.class)
			return toInt(obj);
		else if(clazz == String.class)
			return Bytes.toString(obj);
		else if(clazz == BigDecimal.class)
			return toBigDecimal(obj);
		else if(clazz == Long.class || clazz == long.class)
			return toLong(obj);
		else if(clazz == Short.class  || clazz == short.class)
			return toShort(obj);
		throw new IllegalArgumentException("type no support!");
	}
}
