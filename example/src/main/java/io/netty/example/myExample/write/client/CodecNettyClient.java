package io.netty.example.myExample.write.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.myExample.write.IntEncoder;
import io.netty.example.myExample.write.PersonDecoder;
import io.netty.example.myExample.write.client.ClientIntegerHandler;

/**
 * Create By xzz on 2020/4/21
 */
public class CodecNettyClient {


    public static void main(String[] args) {

        //客户端只需要一个事件循环组
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);

        //创建启动辅助类对象
        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new PersonDecoder());
                            ch.pipeline().addLast(new IntEncoder());
                            ch.pipeline().addLast(new ClientIntegerHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8888).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }

    }


}
