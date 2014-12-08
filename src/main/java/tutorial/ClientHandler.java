package tutorial;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.hbase.generated.AddressBookProtos;

/**
 *
 * Created by bboniao on 11/18/14.
 */
public class ClientHandler extends SimpleChannelInboundHandler<AddressBookProtos.Person> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressBookProtos.Person msg) throws Exception {
        System.out.println(msg.getId() + ", " +
                msg.getName() + ", " +
                msg.getEmail() + "," +
                msg.getPhone(0).getNumber() + ", " +
                msg.getPhone(0).getType());
    }
}
