package com.ocloud.hadoop.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.springframework.stereotype.Service;

import com.ocloud.hadoop.MapFileService;


@Service
public class MapFileServiceImpl implements MapFileService {
	
	private static final Configuration conf = new Configuration(true) ;
	
	@Override
	public void save(String uri,TreeMap<String,byte[]> entry) throws IOException{
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
	    MapFile.Writer writer = null;
	      writer = new MapFile.Writer(conf, fs, uri,
	    		  Text.class, BytesWritable.class);

	    try {	
	    	for(Entry<String,byte[]> e : entry.entrySet()){
				Text keyT = new Text(e.getKey());
			    BytesWritable valueT = new BytesWritable(e.getValue());
		       writer.append(keyT, valueT);
	    	}
	    } finally {
	      IOUtils.closeStream(writer);
	    }

	}
	
	@Override
	public byte[] get(String uri,String key) throws IOException{
		FileSystem fs = FileSystem.get(URI.create(uri), conf);

		Text keyT = new Text(key);
		BytesWritable valueT = new BytesWritable();
	    MapFile.Reader reader = null;
	    try {
	    	reader = new MapFile.Reader(fs,uri,conf);
	    	reader.get(keyT, valueT);
	    } finally {
	      IOUtils.closeStream(reader);
	    }
	    return valueT.getBytes();
	}
}
