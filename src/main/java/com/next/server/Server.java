package com.next.server;

import com.next.network.SelectDecode;
import com.next.network.tcp.DataObjectDecode;
import com.next.network.tcp.DataObjectEncode;
import com.next.network.tcp.NettyServerHandler;
import com.next.network.websocket.WebSocketFrameHandler;
import com.next.network.websocket.WebSocketIndexPageHandler;
import com.next.network.websocket.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final String WEBSOCKET_PATH = "/websocket";
    private final static Server server = new Server();


    public final static Server getInstance(){

        return server;
    }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private List<ChannelHandler> tcpHandler;
    private List<ChannelHandler> websocketHandler;

    public void init(int port) {
        initTcpHandler();
        initWebSocketHandler();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.ALLOCATOR,
                    PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR,
                            PooledByteBufAllocator.DEFAULT);
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("selectDecode", new SelectDecode());
                }
            });
//            b.childHandler(new WebSocketServerInitializer(null));

            ChannelFuture f = b.bind(port).sync();
            // f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void initTcpHandler() {
        tcpHandler = new ArrayList<>();
        tcpHandler.add(new DataObjectDecode(CharsetUtil.UTF_8));
        tcpHandler.add(new DataObjectEncode(CharsetUtil.UTF_8));
        tcpHandler.add(new NettyServerHandler());
    }

    public void initWebSocketHandler(){
        websocketHandler = new ArrayList<>();
        websocketHandler.add(new HttpServerCodec());
        websocketHandler.add(new HttpObjectAggregator(65536));
        websocketHandler.add(new WebSocketServerCompressionHandler());
        websocketHandler.add(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        websocketHandler.add(new WebSocketIndexPageHandler(WEBSOCKET_PATH));
        websocketHandler.add(new WebSocketFrameHandler());
    }

    public List<ChannelHandler> getTcpHandler() {
        return tcpHandler;
    }



    public List<ChannelHandler> getWebsocketHandler() {
        return websocketHandler;
    }


    public static void main(String[] args) {
        Server.server.init(8080);
    }
}
