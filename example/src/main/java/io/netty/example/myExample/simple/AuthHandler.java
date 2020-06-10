package io.netty.example.myExample.simple;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 该handler是用于权限校验的handler
 * 有一种场景就是客户端连接进来我要先进行账户校验；一旦校验通过就将该handler删除
 */
public class AuthHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        checkPower("password");
        System.out.println("通过验证校验");
        ctx.pipeline().remove(this);
        ctx.fireChannelRead(msg);
    }

    private boolean checkPower(String password) {
        if ("password".equals(password)) {
            return true;
        }
        return false;

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("权限校验的处理器被移除");
    }
}
