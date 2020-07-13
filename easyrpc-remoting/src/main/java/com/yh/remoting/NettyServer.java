package com.yh.remoting;

import com.yh.MarshallingCodeCFactory;
import com.yh.MixAll;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class NettyServer {


    private Integer port;

    private EventLoopGroup parentGroup = null;

    private EventLoopGroup childGroup = null;

    private EasyRpcServer server;

    public NettyServer(Integer port,EasyRpcServer server) {
        this.port = port;
        this.server = server;
    }

    public void start() throws InterruptedException {
        parentGroup = new NioEventLoopGroup();
        childGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(parentGroup,childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, MixAll.SO_BACKLOG)
                .option(ChannelOption.SO_RCVBUF, MixAll.SO_RCVBUF)
                .option(ChannelOption.SO_SNDBUF, MixAll.SO_SNDBUF)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder())
                                .addLast(MarshallingCodeCFactory.buildMarshallingDecoder())
                                //长连接保持10min
                                //.addLast(new ReadTimeoutHandler(MixAll.READ_TIMEOUT, TimeUnit.SECONDS))
                                .addLast(new NettyServerHandlerImpl(server.getToken(),server.getAc()));
                    }
                });
        ChannelFuture cf = bootstrap.bind(port).sync();
        cf.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(parentGroup != null) {
                    parentGroup.shutdownGracefully();
                }
                if(childGroup != null) {
                    childGroup.shutdownGracefully();
                }
            }
        });
    }
}
