package com.bboniao.hbase.service.impl;

import com.bboniao.hbase.pojo.GetItem;
import com.bboniao.hbase.service.BatchGetService;
import com.bboniao.hbase.util.Constant;
import com.bboniao.hbase.util.HtableUtil;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 采用Hystrix request Collapsing 实现批量get
 * Created by bboniao on 11/5/14.
 */
public class HystrixBatchGetServiceImpl implements BatchGetService {
    @Override
    public List<String> batch(List<GetItem> getItems) {
        List<String> result = new ArrayList<>(getItems.size());

        for (GetItem item : getItems) {
            Get g = new Get(item.getRowkey());
            g.addFamily(item.getFamily());
            Future<String> f = new CommandCollapserGetValueForKey(g).queue();
            try {
                result.add(f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static class CommandCollapserGetValueForKey extends HystrixCollapser<List<String>, String, Get> {

        private final Get key;

        public CommandCollapserGetValueForKey(Get key) {
            this.key = key;
        }

        @Override
        public Get getRequestArgument() {
            return key;
        }

        @Override
        protected HystrixCommand<List<String>> createCommand(final Collection<CollapsedRequest<String, Get>> requests) {
            return new BatchCommand(requests);
        }

        @Override
        protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, Get>> requests) {
            int count = 0;
            for (CollapsedRequest<String, Get> request : requests) {
                request.setResponse(batchResponse.get(count++));
            }
        }

        private static final class BatchCommand extends HystrixCommand<List<String>> {
            private final Collection<CollapsedRequest<String, Get>> requests;

            private BatchCommand(Collection<CollapsedRequest<String, Get>> requests) {
                super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetValueForKey")));
                this.requests = requests;
            }

            @Override
            protected List<String> run() throws IOException {
                ArrayList<String> response = new ArrayList<>();
                for (CollapsedRequest<String, Get> request : requests) {
                    Result r = HtableUtil.I.getHtable(Constant.HTABLE).get(request.getArgument());
                    if (r != null && r.value() != null) {
                        response.add(new String(r.value()));
                    }
                }
                return response;
            }
        }
    }
}
