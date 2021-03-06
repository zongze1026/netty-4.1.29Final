package io.netty.example.myExample.codec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Create By xzz on 2020/4/21
 */
public class CodecServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new MyByteToIntegerDecoder());
        ch.pipeline().addLast(new MyIntegerToBytesEncoder());
        ch.pipeline().addLast(new ServerIntegerHandler());
    }
}