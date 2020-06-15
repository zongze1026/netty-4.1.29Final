package io.netty.example.myExample.write;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import java.util.List;

/**
 * Create By xzz on 2020/6/15
 */
public class PersonDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int length = in.readableBytes();
        if (length >= 4) {
            int len = in.readInt();
            if (in.readableBytes() >= len) {
                Person person = new Person();
                person.setAge(in.readInt());
                person.setName(in.readBytes(len - 4).toString(CharsetUtil.UTF_8));
                out.add(person);
            } else {
                in.resetReaderIndex();
            }
        }


    }
}
