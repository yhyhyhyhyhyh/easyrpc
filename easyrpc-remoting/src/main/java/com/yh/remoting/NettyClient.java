package com.yh.remoting;

import com.yh.MarshallingCodeCFactory;
import com.yh.MixAll;
import com.yh.RemotingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private String hostname;

    private Integer port;

    private volatile Channel channel;


    private EventLoopGroup group = null;

    public NettyClient(String hostname,Integer port) {
        this.hostname = hostname;
        this.port = port;
    }

    public synchronized void doConnect() throws InterruptedException {
        if(hostname == null || port == null || port<1 || port>65534) {
            throw new RemotingException(String.format("不合法的hostname或端口.hostname:%s,端口%d",hostname,port));
        }
        try {
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    //.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_RCVBUF, MixAll.SO_RCVBUF)
                    .option(ChannelOption.SO_SNDBUF, MixAll.SO_SNDBUF)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder())
                                    .addLast(MarshallingCodeCFactory.buildMarshallingDecoder())
                                    .addLast(new NettyClientHandlerImpl());
                        }
                    });
            ChannelFuture cf =  bootstrap.connect(hostname,port).sync();
            this.channel = cf.channel();
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    shutdownEventLoopGroup();
                }
            }));
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        }
    }

    synchronized void shutdownEventLoopGroup() {
        if(!group.isShutdown()&&!group.isShuttingDown()) {
            group.shutdownGracefully();
        }
    }


    public Boolean isAvailable() {
        return channel.isActive();
    }

    public void writeAndFlush(Object object) {
        if(channel.isActive()) {
            channel.writeAndFlush(object);
        }
    }
}
