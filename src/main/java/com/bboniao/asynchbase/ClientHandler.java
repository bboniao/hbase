package com.bboniao.asynchbase;

import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;

/**
 *
 * Created by bboniao on 11/18/14.
 */
public class ClientHandler extends SimpleChannelInboundHandler<ClientProtos.GetResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientProtos.GetResponse result) throws Exception {

        List<KV> list = Util.toResult(result.getResult());
        for (KV kv : list) {
            System.out.println(
                    new String(kv.getRow() + "," +
                    new String(kv.getFamily())  + "," +
                    new String(kv.getQulifier()) + "," +
                    kv.getTimestamp() + "," +
                    new String(kv.getValue())
            )

        );
        }
    }
}
