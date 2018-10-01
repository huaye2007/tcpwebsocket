package com.next.network.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class DataObjectDecode extends ByteToMessageDecoder {

    public final static int DATA_LENGTH = 4;
    private final Charset charset;

    public DataObjectDecode(Charset charset) {
        this.charset = charset;
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf bytebuf, List<Object> list) throws Exception {
        int readByteLength = bytebuf.readableBytes();
        if (readByteLength < DATA_LENGTH) {
            return;
        }

        bytebuf.markReaderIndex();
        int length = bytebuf.readInt();
        if (bytebuf.readableBytes() < length) {
            bytebuf.resetReaderIndex();
            return;
        }
        byte[] array = new byte[length];
        bytebuf.readBytes(array);
        list.add(new String(array, charset));
    }
}
