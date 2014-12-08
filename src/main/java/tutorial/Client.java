package tutorial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.hbase.generated.AddressBookProtos;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
                    ch.pipeline().addLast("decoder", new ProtobufDecoder(AddressBookProtos.Person.getDefaultInstance()));
                    ch.pipeline().addLast("handler", new ClientHandler());
                }
            });
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            Channel ch = bootstrap.connect("127.0.0.1", 8888).sync().channel();

// 控制台输入
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            for (;;) {
                String line = in.readLine();
                if (line == null || "".equals(line)) {
                    continue;
                }
                AddressBookProtos.Person.Builder builder = AddressBookProtos.Person.newBuilder();
                builder.setId(Integer.parseInt(line));
                builder.setName("");
                ch.writeAndFlush(builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
