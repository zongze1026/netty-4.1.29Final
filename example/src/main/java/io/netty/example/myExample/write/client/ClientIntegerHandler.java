package io.netty.example.myExample.write.client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.myExample.write.Person;

/**
 * Create By xzz on 2020/4/23
 */
public class ClientIntegerHandler extends SimpleChannelInboundHandler<Person> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Person msg) throws Exception {
        System.out.println("读取到服务器的数据=" + msg.getName()+":"+msg.getAge());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i=0;i<1;i++){
            ctx.channel().writeAndFlush(100);
        }
    }
}
