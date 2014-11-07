package com.bboniao.hbase.input;

import com.bboniao.hbase.util.Constant;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.hadoop.hbase.util.Bytes.toBytes;

/**
 * 插入数据
 */
public class HbaseInput {

    private static final String VID = "vid";
    private static final String CVID = "cvid";

    private HTableInterface allHtable;
    private List<Put> list;
    private int index;

    public HbaseInput() {
        try {
            allHtable = new HTable(HBaseConfiguration.create(), "rc_feature");
            allHtable.setAutoFlushTo(false);
            allHtable.setWriteBufferSize(10 * 1024 * 1024);
            list = new ArrayList<>(Constant.BATCH_SIZE);
            index = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void input(String path) {
        String read;
        BufferedReader br = null;
        int count = 0;
        try {
            File file = new File(path);
//            File file = new File(getPath("part-m-00004.txt"));
            FileReader fileread = new FileReader(file);
            br = new BufferedReader(fileread);
            while ((read = br.readLine()) != null) {
                process(read);
                count++;
                if (count %1000 == 0) {
                    System.out.println(count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void process(String value) throws IOException {
        String[] line = value.split(Constant.TAB);
        if (line.length < 2) {
            return;
        }

        Map<String, String> map = new HashMap<>();
        for (String s : line) {
            String[] kv = s.split(Constant.COLON);
            if (kv.length < 2) {
                continue;
            }
            map.put(kv[0], kv[1]);
        }

        /** 导入hbase */
        byte[] rk = getRowKey(map);
        Put p = getPut(rk, map);
        if (!p.isEmpty()) {
            this.list.add(p);
            index++;
            if (index % Constant.BATCH_SIZE == 0) {
                this.allHtable.put(list);
                list = new ArrayList<>(Constant.BATCH_SIZE);
            }
        }
    }

    private byte[] getRowKey(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        String cvid = map.get(CVID);
        String vid = map.get(VID);
        if (vid != null && cvid != null) {
            sb.append(vid).append(Constant.UNDERSCORE).append(cvid);
        } else {
            if (vid == null) {
                sb.append(cvid);
            }
            if (cvid == null) {
                sb.append(vid);
            }
        }
        return Bytes.toBytes(DigestUtils.md5Hex(sb.toString()) + Constant.VERTICAL_ORIGIN
                + Constant.VIDEO_TYPE_VER_ID + Constant.VERTICAL_ORIGIN + sb.toString());
    }

    private Put getPut(byte[] rk, Map<String, String> map) {
        Put p = new Put(rk);
        p.setDurability(Durability.SKIP_WAL);
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (e.getKey().equals(VID) || e.getKey().equals(CVID)) {
                continue;
            }
            p.add(Constant.F, toBytes(e.getKey()), toBytes(e.getValue()));
        }
        return p;
    }

    /*private String getPath(String fileName) throws IOException {
        String url = "";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader != null) {
            URL u = classLoader.getResource(fileName);
            if (u != null) {
                url = u.getPath();
            }
        }
        if (url.contains("%20")) {
            url = url.replace("%20", " ");
        }

        if (url == null) {
            throw new FileNotFoundException("找不到路径");
        }
        return url;
    }*/

    public static void main(String[] args) {
        new HbaseInput().input(args[0]);
    }
}

