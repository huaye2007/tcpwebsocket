package com.next.network;

import com.next.network.tcp.DataObjectDecode;
import com.next.network.tcp.DataObjectEncode;
import com.next.network.tcp.NettyServerHandler;
import com.next.network.websocket.LineParser;
import com.next.network.websocket.WebSocketFrameHandler;
import com.next.network.websocket.WebSocketIndexPageHandler;
import com.next.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.AppendableCharSequence;

import java.util.List;

public class SelectDecode extends ByteToMessageDecoder {

    private final LineParser lineParser;


    public SelectDecode() {
        AppendableCharSequence seq = new AppendableCharSequence(128);
        lineParser = new LineParser(seq, 4096);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        ChannelPipeline pipeline = ctx.channel().pipeline();
        boolean skip = skipControlCharacters(in);
        List<ChannelHandler> list = null;
        //tcp
        if (!skip) {
            list = Server.getInstance().getTcpHandler();

        } else {
            //http
            list = Server.getInstance().getWebsocketHandler();
        }
        for (ChannelHandler ch : list) {
            pipeline.addLast(ch);
        }
        pipeline.remove(this);
    }

    private boolean skipControlCharacters(ByteBuf buffer) {
        buffer.markReaderIndex();
        buffer.markWriterIndex();
        boolean skiped = false;
        final int wIdx = buffer.writerIndex();
        int rIdx = buffer.readerIndex();
        while (wIdx > rIdx) {
            int c = buffer.getUnsignedByte(rIdx++);
            if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
                rIdx--;
                skiped = true;
                break;
            }
        }
        if (skiped) {
            AppendableCharSequence line = lineParser.parse(buffer);
            if (line == null) {
                skiped = false;
            }
        }
        buffer.readerIndex(rIdx);

        buffer.resetReaderIndex();
        buffer.resetWriterIndex();
        return skiped;
    }

}
