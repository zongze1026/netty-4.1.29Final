package io.netty.example.myExample.write;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Create By xzz on 2020/6/15
 */
public class PersonEncoder extends MessageToByteEncoder<Person> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Person msg, ByteBuf out) throws Exception {
        byte[] bytes = msg.getName().getBytes("utf-8");
        out.writeInt(bytes.length+4);
        out.writeInt(msg.getAge());
        out.writeBytes(bytes);
    }
}
