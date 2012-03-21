package com.ocloud.hadoop;

import java.io.IOException;
import java.util.TreeMap;

public interface MapFileService {

	public void save(String uri,TreeMap<String,byte[]> entry) throws IOException;
	
	public byte[] get(String uri,String key) throws IOException;
	
}
