package io.netty.example.myExample.write.server;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.myExample.write.Person;

/**
 * Create By xzz on 2020/4/23
 */
public class ServerIntegerHandler extends SimpleChannelInboundHandler<Integer> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Integer msg) throws Exception {
        System.out.println("收到客户端信息：" + msg);
        //响应客户端
        ctx.channel().writeAndFlush(new Person());
    }
}
