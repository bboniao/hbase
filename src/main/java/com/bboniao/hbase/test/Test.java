package com.bboniao.hbase.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.service.impl.*;
import com.bboniao.hbase.util.AsyncHbaseUtil;
import com.bboniao.hbase.util.Constant;
import com.bboniao.hbase.util.HtableUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * test
 * Created by bboniao on 11/7/14.
 */
public class Test {

    private static final String VID = "vid";
    private static final String CVID = "cvid";

    private BatchGetService batchGetService;

    private AtomicLong hit = new AtomicLong();
    private AtomicLong all = new AtomicLong();

    public Test(BatchGetService batchGetService) {
        this.batchGetService = batchGetService;
    }

    public static void main(String[] args) throws Exception {

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadPoolExecutor te = (ThreadPoolExecutor)AsyncThreadPoolFactory.ASYNC_HBASE_THREAD_POOL;
                for (int i = 0; i < 1000000; i++) {
                    System.out.println("thread poll size is: " + (te.getTaskCount() - te.getCompletedTaskCount()));
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/



//        args = new String[]{"/Users/bboniao/Desktop/test.txt", "5"};
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
            case "5":
                batchGetService = new NativeBatchGetServiceImpl();
                break;
            case "6":
                batchGetService = new AsyncJdkBatchGetServiceImplV1();
                break;
            default:
                batchGetService = new AsyncJdkBatchGetServiceImplV2();
                break;
        }
        System.out.println("start");
        long time = System.currentTimeMillis();
        Test t = new Test(batchGetService);
        t.output(args[0]);
        System.out.println(System.currentTimeMillis() - time);
        t.genResult();
        if ("1".equals(args[1])) {
            AsyncHbaseUtil.I.close();
        }
        if ("5".equals(args[1])) {
            HtableUtil.I.close();
        }
    }

    public void genResult() {
        System.out.println("hit: " + this.hit.get() + ", all: " + this.all.get());
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
                count++;
                if (count <= 100) {
                    Get gg = new Get(g.getRowkey());gg.addFamily(g.getFamily());
                    HtableUtil.I.getHtable(Constant.HTABLE).get(gg);
                    continue;
                }
                gets.add(g);
                if (count %100 == 0) {
                    Map<String, Map<String,String>> l = this.batchGetService.batch(gets);
                    hit.addAndGet(l.size());
                    all.addAndGet(gets.size());
                    gets = new ArrayList<>(100);
                    System.out.println(count);
                }
            }
            Map<String, Map<String,String>> l = this.batchGetService.batch(gets);
            hit.addAndGet(l.size());
            all.addAndGet(gets.size());
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
