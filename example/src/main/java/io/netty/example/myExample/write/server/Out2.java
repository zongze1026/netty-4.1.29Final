package io.netty.example.myExample.write.server;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.example.myExample.write.Person;

/**
 * Create By xzz on 2020/6/15
 */
public class Out2 extends ChannelOutboundHandlerAdapter {


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Person person = (Person) msg;
        person.setName("toms");
        ctx.write(person);
    }

}
