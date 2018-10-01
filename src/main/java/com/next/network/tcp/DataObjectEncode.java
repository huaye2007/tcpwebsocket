package com.next.network.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class DataObjectEncode extends MessageToByteEncoder<String> {
    private final Charset charset;

    public DataObjectEncode(Charset charset) {
        this.charset = charset;
    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, ByteBuf byteBuf){
        byte[] array = s.getBytes(charset);
        byteBuf.writeInt(array.length);
        byteBuf.writeBytes(array);
    }
}
