package com.next.network.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class TcpClient {

    public static void init() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("decoder", new DataObjectDecode(CharsetUtil.UTF_8));
                    ch.pipeline().addLast("encoder", new DataObjectEncode(CharsetUtil.UTF_8));
                    ch.pipeline().addLast("handler", new TcpClientHandler());
                }
            });

            ChannelFuture f = b.connect("127.0.0.1", 8080).sync(); // (5)
            f.awaitUninterruptibly();
            f.channel().writeAndFlush("测试代码");
            f.channel().writeAndFlush("测试代码2");
            Thread.sleep(10000);
            f.channel().writeAndFlush("测试代码3");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        	finally {
//        		workerGroup.shutdownGracefully();
//        	}
    }

    public static void main(String[] args) {
        init();
    }
}
