package com.bboniao.asynchbase;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.hbase.async.generated.ClientPB;
import org.hbase.async.generated.HBasePB;
import org.hbase.generated.AddressBookProtos;

/**
 *
 * Created by bboniao on 11/18/14.
 */
public class Client {
    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast("encoder", new ProtobufEncoder());
                    ch.pipeline().addLast("decoder", new ProtobufDecoder(ClientProtos.GetResponse.getDefaultInstance()));
                    ch.pipeline().addLast("handler", new ClientHandler());
                }
            });
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            Channel ch = bootstrap.connect("10.16.14.251", 60020).sync().channel();

// 控制台输入
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            for (;;) {
                String line = in.readLine();
                if (line == null || "".equals(line)) {
                    continue;
                }

                GetReq g = new GetReq(line.getBytes());
                ClientProtos.GetRequest builder = Util.buildGetRequest("rc_feature,,1415243541651.3d544fddce9b689c4f767a4de1561d9c.".getBytes(), g);
                ch.writeAndFlush(builder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
