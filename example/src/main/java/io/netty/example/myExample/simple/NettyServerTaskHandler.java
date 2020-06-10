package io.netty.example.myExample.simple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

/**
 * Create By xzz on 2020/4/21
 * 自定义任务添加到NioEvenLoop的任务队列中去
 * 1. ctx.channel().eventLoop().execute 提交一个 普通任务
 * 2. ctx.channel().eventLoop().schedule 提交一个延时任务
 * <p>
 * 使用场景：服务端需要耗时的业务操作时，但是需要尽快返回结果给客户端
 * 而不是阻塞到任务处理完成后才返回
 */
public class NettyServerTaskHandler extends ChannelInboundHandlerAdapter {


    //假设有一个非常耗时的操作，我们需要尽快给客户端返回的话，那我们就可以
    //自己定义一个任务添加到任务队列中即可:
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送的消息：" + byteBuf.toString(Charset.defaultCharset()));
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelHandler is add");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
