package com.yh.remoting;

import com.yh.MarshallingCodeCFactory;
import com.yh.MixAll;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.TimeUnit;

public class NettyServer {

    private String hostname;

    private Integer port;

    private EventLoopGroup parentGroup = null;

    private EventLoopGroup childGroup = null;

    public NettyServer(String hostname,Integer port) {
        this.hostname = hostname;
        this.port = port;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(parentGroup != null) {
                    parentGroup.shutdownGracefully();
                }
                if(childGroup != null) {
                    childGroup.shutdownGracefully();
                }
            }
        });
    }

    public void start() {
        try {
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
                                    .addLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                                    //.addLast(new ServerMessageHandler())
                            ;
                        }
                    });
            //ChannelFuture cf = bootstrap.bind(port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
