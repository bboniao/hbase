package com.bboniao.hbase.test;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.service.impl.*;
import com.bboniao.hbase.util.Constant;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * jmeter
 * Created by bboniao on 11/10/14.
 */
public class JMeter extends AbstractJavaSamplerClient {

    private List<GetItem> gets = new ArrayList<>(100);

    private int count = 0;

    private static Map<String, BatchGetService> batchGetService = new HashMap<>();

    static {
        batchGetService.put("1", new AsyncHbaseBatchGetServiceImpl());
        batchGetService.put("2", new AsyncJdkBatchGetServiceImpl());
        batchGetService.put("6", new AsyncJdkBatchGetServiceImplV1());
        batchGetService.put("7", new AsyncJdkBatchGetServiceImplV2());
        batchGetService.put("3", new DeferredBatchGetServiceImpl());
        batchGetService.put("4", new HystrixBatchGetServiceImpl());
        batchGetService.put("5", new NativeBatchGetServiceImpl());
        batchGetService.put("8", new NativeGetServiceImpl());
    }


    private  final AtomicLong hit = new AtomicLong();
    private  final AtomicLong all = new AtomicLong();



    @Override
    public void teardownTest(JavaSamplerContext context) {
        Map<String, Map<String,String>> map = batchGetService.get(context.getParameter("method")).batch(gets);
        hit.addAndGet(map.size());
        all.addAndGet(gets.size());

        System.out.println("hit: " + hit.get() + ", all: " + all.get());

        super.teardownTest(context);

    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument("rk", "");
        arguments.addArgument("method", "");
        return arguments;
    }
    @Override
    public SampleResult runTest(JavaSamplerContext context) {

        SampleResult sr = new SampleResult();
        // Start
        sr.sampleStart();
        count++;
        String rk = context.getParameter("rk");
        byte[] rowKey = getRowKey(rk);
        GetItem item = new GetItem(Constant.HTABLE, rowKey, Constant.F, null, 1);
        gets.add(item);
        try {
            if (count %100 == 0) {
                Map<String, Map<String,String>> map = batchGetService.get(context.getParameter("method")).batch(gets);
                hit.addAndGet(map.size());
                all.addAndGet(gets.size());
                gets = new ArrayList<>(100);
            }
            sr.setSuccessful(true);
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            sr.setSuccessful(false);
        } finally {
            sr.sampleEnd();
        }
        return sr;
    }

    private byte[] getRowKey(String rk) {
        String[] rks = rk.split(Constant.COLON);
        String vid = rks[1];
        return Bytes.toBytes(DigestUtils.md5Hex(vid) + Constant.VERTICAL_ORIGIN
                + Constant.VIDEO_TYPE_VER_ID + Constant.VERTICAL_ORIGIN + vid);
    }

    public static void main(String[] args) {

    }
}
