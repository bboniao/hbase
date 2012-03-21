package com.ocloud.hadoop.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.ocloud.hadoop.MapFileService;

// 指定要使用的测试框架
@RunWith(SpringJUnit4ClassRunner.class)
// 指定配置文件的位置,可以配置多个
@ContextConfiguration({ "/spring/applicationContext.xml" })
// 如果测试已修改上下文（例如，更换一个bean定义），使用此批注。随后的测试提供了一个新的上下文。
// 有两种模式可选，一种是方法级别的，一种是类级别的，默认类级别的
// @TestExecutionListeners
// @IfProfileValue 需要研究一下是什么意思
// @ProfileValueSourceConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
// 配置事物
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class MapFileServiceImplTest {

	@Autowired
	private MapFileService mapFileService;

	private List<File> files;

	@Before
	public void before() {
		files = new ArrayList<File>();
		getFile("/data/Kugou", files);
	}

//	@Test
	public void saves() throws IOException {
		TreeMap<String,byte[]> entry = new TreeMap<String,byte[]>();
		for(File f : files){
			entry.put(f.getPath(), file2bytes(f));
		}
		mapFileService.save("/test/mapfile",  entry);
	}
	
	@Test
	public void show() throws IOException {
		/*for(File f : files){
			byte[] result = mapFileService.get("/test/mapfile", f.getPath());
			System.out.println(new String(result));
		}*/
		
		FileSystem fs = FileSystem.get(URI.create("/test/mapfile"), new Configuration(true));

		Text keyT = new Text();
		BytesWritable valueT = new BytesWritable();
	    MapFile.Reader reader = null;
	    long position=0;
	    try {
	    	reader = new MapFile.Reader(fs,"/test/mapfile",new Configuration(true));
	    	while(reader.next(keyT, valueT)){
	    	     System.out.printf("[%s]\t%s\n", ++position,  keyT);

	    	    }
	    } finally {
	      IOUtils.closeStream(reader);
	    }
	}
	
//	@Test
	public void get() throws IOException {
		for(File f : files){
			byte[] result = mapFileService.get("/test/mapfile", f.getPath());
			System.out.println(new String(result));
		}
		
	}
	
	public static File bytes2file(byte[] b, String path) { 
        File ret = null;  
        BufferedOutputStream stream = null;  
        try {  
            ret = new File(path);  
            FileOutputStream fstream = new FileOutputStream(ret);  
            stream = new BufferedOutputStream(fstream);  
            stream.write(b);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (stream != null) {  
                try {  
                    stream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return ret;  
    }  
	
	private byte[] file2bytes(File file) throws IOException{
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));        
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);        
       
        byte[] temp = new byte[1024];        
        int size = 0;        
        while ((size = in.read(temp)) != -1) {        
            out.write(temp, 0, size);        
        }        
        in.close();        
       
        byte[] content = out.toByteArray();   
        return content;
	}

	private void getFile(String dir,List<File> files) {
		File f = new File(dir);
		File[] fl = f.listFiles();
		for (int i = 0; i < fl.length; i++) {
			if (fl[i].isDirectory()) {
				getFile(fl[i].getAbsoluteFile().getPath(),files);
			} else {
				files.add(fl[i]);
			}
		}
	}
}
