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

    private String token;

    private Integer weight;

    private volatile Channel channel;

    private static EventLoopGroup group = new NioEventLoopGroup();



    public NettyClient(String hostname,Integer port,String token,Integer weight) {
        this.hostname = hostname;
        this.port = port;
        this.token = token;
        this.weight = weight;
    }

    public synchronized void doConnect() throws InterruptedException {
        if(hostname == null || port == null || port<1 || port>65534) {
            throw new RemotingException(String.format("不合法的hostname或端口.hostname:%s,端口%d",hostname,port));
        }
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    //.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_RCVBUF, MixAll.SO_RCVBUF)
                    .option(ChannelOption.SO_SNDBUF, MixAll.SO_SNDBUF)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, MixAll.CONNECTION_TIMEOUT)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder())
                                    .addLast(MarshallingCodeCFactory.buildMarshallingDecoder())
                                    .addLast(new NettyClientHandlerImpl(NettyClient.this));
                        }
                    });
            ChannelFuture cf =  bootstrap.connect(hostname,port).sync();
            this.channel = cf.channel();
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
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

    public String getHostname() {
        return this.hostname;
    }

    public Integer getPort() {
        return this.port;
    }

    protected String getToken() {
        return this.token;
    }

    protected Channel getChannel() {
        return this.channel;
    }

    public Integer getWeight() {
        return this.weight;
    }

    protected void setWeight(Integer weight) {
        this.weight = weight;
    }
}
