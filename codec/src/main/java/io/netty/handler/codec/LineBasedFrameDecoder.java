/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ByteProcessor;

import java.util.List;

/**
 * A decoder that splits the received {@link ByteBuf}s on line endings.
 * <p>
 * Both {@code "\n"} and {@code "\r\n"} are handled.
 * For a more general delimiter-based decoder, see {@link DelimiterBasedFrameDecoder}.
 * 回车换行解码器；它是以回车或者换行来作为消息结束的标识的解码器
 */
public class LineBasedFrameDecoder extends ByteToMessageDecoder {

    /** Maximum length of a frame we're willing to decode.  */
    private final int maxLength; //行最大长度
    /** Whether or not to throw an exception as soon as we exceed maxLength. */
    private final boolean failFast; //是否立即抛出异常
    private final boolean stripDelimiter; //是否跳过分隔符

    /** True if we're discarding input because we're already over maxLength.  */
    private boolean discarding; //如果超过最大长度，是否抛弃字节数据
    private int discardedBytes; //记录抛弃的字节数量

    /** Last scan position. */
    private int offset;

    /**
     * Creates a new decoder.
     * @param maxLength  the maximum length of the decoded frame.
     *                   A {@link TooLongFrameException} is thrown if
     *                   the length of the frame exceeds this value.
     */
    public LineBasedFrameDecoder(final int maxLength) {
        this(maxLength, true, false);
    }

    /**
     * Creates a new decoder.
     * @param maxLength  the maximum length of the decoded frame.
     *                   A {@link TooLongFrameException} is thrown if
     *                   the length of the frame exceeds this value.
     * @param stripDelimiter  whether the decoded frame should strip out the
     *                        delimiter or not
     * @param failFast  If <tt>true</tt>, a {@link TooLongFrameException} is
     *                  thrown as soon as the decoder notices the length of the
     *                  frame will exceed <tt>maxFrameLength</tt> regardless of
     *                  whether the entire frame has been read.
     *                  If <tt>false</tt>, a {@link TooLongFrameException} is
     *                  thrown after the entire frame that exceeds
     *                  <tt>maxFrameLength</tt> has been read.
     *
     * @param maxLength 表示一行可以允许最大的字节数
     * @param stripDelimiter 该参数表示是否跳过分割符，默认为true
     * @param failFast 是否立即抛出异常；如果为true立即抛出异常，否则读取完毕抛出异常；默认false
     */
    public LineBasedFrameDecoder(final int maxLength, final boolean stripDelimiter, final boolean failFast) {
        this.maxLength = maxLength;
        this.failFast = failFast;
        this.stripDelimiter = stripDelimiter;
    }

    @Override
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
    }

    /**
     * Create a frame out of the {@link ByteBuf} and return it.
     *
     * @param   ctx             the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param   buffer          the {@link ByteBuf} from which to read data
     * @return  frame           the {@link ByteBuf} which represent the frame or {@code null} if no frame could
     *                          be created.
     */
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        //找出换行符的位置
        final int eol = findEndOfLine(buffer);
        //判断如果discarding为false进入if判断；也就是非丢弃模式
        if (!discarding) {
            //如果已经找到了换行符的位置
            if (eol >= 0) {
                final ByteBuf frame;
                //通过换行符的位置减去readerIndex计算出这次消息的长度
                final int length = eol - buffer.readerIndex();
                //计算出换行符的长度；如果是\n则长度1；如果是\r\n长度是2
                final int delimLength = buffer.getByte(eol) == '\r'? 2 : 1;

                //消息长度大于最大长度的话就会丢弃本次消息；maxLength通过构造方法传入设置
                if (length > maxLength) {
                    //通过设置byteBuf的读索引来丢弃当前读取的消息字节
                    buffer.readerIndex(eol + delimLength);
                    fail(ctx, length);
                    return null;
                }

                if (stripDelimiter) {
                    //读取byteBuf数据，不包含换行符
                    frame = buffer.readRetainedSlice(length);
                    //跳过换行符
                    buffer.skipBytes(delimLength);
                } else {
                    //读取包含换行符的数据
                    frame = buffer.readRetainedSlice(length + delimLength);
                }

                return frame;
            } else {
                //如果eol返回的是-1,没有找到换行符
                final int length = buffer.readableBytes();
                //如果byteBuf中的长度大于消息最大长度
                if (length > maxLength) {
                    discardedBytes = length;
                    //这里直接设置readIndex为该buffer的writerIndex；抛弃writerIndex之前的数据
                    buffer.readerIndex(buffer.writerIndex());
                    //标记为丢弃模式
                    discarding = true;
                    offset = 0;
                    if (failFast) {
                        fail(ctx, "over " + discardedBytes);
                    }
                }
                //如果没有超过消息最大长度，返回null
                return null;
            }
            //如果是丢弃模式进入else判断
        } else {
            //找到分隔符
            if (eol >= 0) {
                //记录丢弃的字节；discardedBytes(前面已经丢弃的字节)+当前丢弃的字节(eol - buffer.readerIndex())
                final int length = discardedBytes + eol - buffer.readerIndex();
                //计算换行符的长度
                final int delimLength = buffer.getByte(eol) == '\r'? 2 : 1;
                //丢弃字节
                buffer.readerIndex(eol + delimLength);
                discardedBytes = 0;
                //设置discarding为非丢弃模式
                discarding = false;
                if (!failFast) {
                    fail(ctx, length);
                }
            } else {
                //如果没有找到换行符；直接丢弃当前byteBuf中所有的数据
                discardedBytes += buffer.readableBytes();
                buffer.readerIndex(buffer.writerIndex());
            }
            return null;
        }
    }

    private void fail(final ChannelHandlerContext ctx, int length) {
        fail(ctx, String.valueOf(length));
    }

    private void fail(final ChannelHandlerContext ctx, String length) {
        ctx.fireExceptionCaught(
                new TooLongFrameException(
                        "frame length (" + length + ") exceeds the allowed maximum (" + maxLength + ')'));
    }

    /**
     * Returns the index in the buffer of the end of line found.
     * Returns -1 if no end of line was found in the buffer.
     *
     * 寻找分隔符\n，如果没有找到分隔符返回-1
     */
    private int findEndOfLine(final ByteBuf buffer) {
        int totalLength = buffer.readableBytes();
        //遍历byteBuf寻找\n分割符所在的位置
        int i = buffer.forEachByte(buffer.readerIndex() + offset, totalLength - offset, ByteProcessor.FIND_LF);
        if (i >= 0) {
            offset = 0;
            //找到\n分隔符后，需要判断一下是否是\r\n的分隔符；判断方法就是取到前一个字节判断是否是\r
            if (i > 0 && buffer.getByte(i - 1) == '\r') {
                i--;
            }
        } else {
            offset = totalLength;
        }
        return i;
    }
}
