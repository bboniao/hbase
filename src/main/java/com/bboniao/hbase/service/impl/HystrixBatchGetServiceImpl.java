package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.service.BatchGetServiceUtil;
import com.bboniao.hbase.util.Constant;
import com.bboniao.hbase.util.HtableUtil;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 采用Hystrix request Collapsing 实现批量get
 * Created by bboniao on 11/5/14.
 */
public class HystrixBatchGetServiceImpl implements BatchGetService {
    @Override
    public Map<String, Map<String,String>> batch(List<GetItem> getItems) {
        Map<String, Map<String,String>> result = new ConcurrentHashMap<>();
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            for (GetItem item : getItems) {
                Get g = new Get(item.getRowkey());
                g.addFamily(item.getFamily());
                Future<Result> f = new CommandCollapserGetValueForKey(g).queue();
                try {
                    BatchGetServiceUtil.dealLoop(new Result[]{f.get()}, result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            context.shutdown();
        }

        return result;
    }

    public class CommandCollapserGetValueForKey extends HystrixCollapser<List<Result>, Result, Get> {

        private final Get key;

        public CommandCollapserGetValueForKey(Get key) {
            this.key = key;
        }

        @Override
        public Get getRequestArgument() {
            return key;
        }

        @Override
        protected HystrixCommand<List<Result>> createCommand(final Collection<CollapsedRequest<Result, Get>> requests) {
            return new BatchCommand(requests);
        }

        @Override
        protected void mapResponseToRequests(List<Result> batchResponse, Collection<CollapsedRequest<Result, Get>> requests) {
            int count = 0;
            for (CollapsedRequest<Result, Get> request : requests) {
                request.setResponse(batchResponse.get(count++));
            }
        }

        private  final class BatchCommand extends HystrixCommand<List<Result>> {
            private final Collection<CollapsedRequest<Result, Get>> requests;

            private BatchCommand(Collection<CollapsedRequest<Result, Get>> requests) {
                super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("batch_get"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("batch_get"))
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("batch_get_pool"))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(500))
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(200)));
                this.requests = requests;
            }

            @Override
            protected List<Result> run() throws IOException {
                ArrayList<Result> response = new ArrayList<>();
                for (CollapsedRequest<Result, Get> request : requests) {
                    response.add(HtableUtil.I.getHtable(Constant.HTABLE).get(request.getArgument()));
                }
                return response;
            }
        }
    }
}
