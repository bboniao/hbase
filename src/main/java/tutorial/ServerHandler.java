package tutorial;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.hbase.generated.AddressBookProtos;

/**
 *
 * Created by bboniao on 11/18/14.
 */
public class ServerHandler extends SimpleChannelInboundHandler<AddressBookProtos.Person> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressBookProtos.Person msg) throws Exception {
        System.out.println("server:" + "channelRead:" + msg.getId());

        AddressBookProtos.Person.Builder person = Persons.addressBook.getPersonBuilder(msg.getId());
        ctx.writeAndFlush(person.build());
    }
}
