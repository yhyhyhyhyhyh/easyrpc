package com.yh.remoting;

import com.yh.MarshallingCodeCFactory;
import com.yh.RemotingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;


import java.util.concurrent.Executors;

public class NettyClient {

    private String hostname;

    private Integer port;

    private volatile Channel channel;

    private Bootstrap clientBootstrap;

    public NettyClient(String hostname,Integer port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void doConnect() throws InterruptedException {
        if(hostname == null || port == null || port<1 || port>65534) {
            throw new RemotingException(String.format("不合法的hostname或端口.hostname:%s,端口%d",hostname,port));
        }
        EventLoopGroup group = null;
        try {
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)//
                    .option(ChannelOption.SO_BACKLOG,128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_RCVBUF, 4 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 4 * 1024)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder())
                                    .addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                                    //.addLast(new IdleStateHandler());
                                    //.addLast(new ClientMessageHandler());
                        }
                    });
            ChannelFuture cf =  bootstrap.connect(hostname,port).sync();
            this.channel = cf.channel();
        } finally {
            if(group != null) {
                //咋搞
                group.shutdownGracefully();
            }
        }
    }

}
