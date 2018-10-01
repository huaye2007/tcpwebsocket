package com.next.network.tcp;

import com.next.network.tcp.DataObjectDecode;
import com.next.network.tcp.DataObjectEncode;
import com.next.network.tcp.NettyServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

public class TcpInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("decoder", new DataObjectDecode(CharsetUtil.UTF_8));
        pipeline.addLast("encoder", new DataObjectEncode(CharsetUtil.UTF_8));
        pipeline.addLast("handler", new NettyServerHandler());
    }
}
