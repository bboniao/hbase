package com.bboniao.hbase.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.service.impl.*;
import com.bboniao.hbase.util.Constant;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * test
 * Created by bboniao on 11/7/14.
 */
public class Test {

    private static final String VID = "vid";
    private static final String CVID = "cvid";

    private BatchGetService batchGetService;

    public Test(BatchGetService batchGetService) {
        this.batchGetService = batchGetService;
    }

    public static void main(String[] args) {
        BatchGetService batchGetService;
        switch (args[1]) {
            case "1":
                batchGetService = new AsyncHbaseBatchGetServiceImpl();
                break;
            case "2":
                batchGetService = new AsyncJdkBatchGetServiceImpl();
                break;
            case "3":
                batchGetService = new DeferredBatchGetServiceImpl();
                break;
            case "4":
                batchGetService = new HystrixBatchGetServiceImpl();
                break;
            default:
                batchGetService = new NativeBatchGetServiceImpl();
                break;
        }
        new Test(batchGetService).output(args[0]);
    }

    public void output(String path) {
        String read;
        BufferedReader br = null;
        int count = 0;
        List<GetItem> gets = new ArrayList<>(100);
        try {
            File file = new File(path);
            FileReader fileread = new FileReader(file);
            br = new BufferedReader(fileread);
            while ((read = br.readLine()) != null) {
                GetItem g = processGet(read);
                if (g == null) {
                    continue;
                }
                gets.add(g);
                count++;
                if (count %100 == 0) {
                    this.batchGetService.batch(gets);
                    gets = new ArrayList<>(100);
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

    private GetItem processGet(String value) {
        String[] line = value.split(Constant.TAB);
        if (line.length < 2) {
            return null;
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

        return new GetItem(Constant.HTABLE, rk, Constant.F, null, 1);
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

}
