package com.yh.remoting;

import com.yh.protocol.ConnectRequest;
import com.yh.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

/**
 * Created by admin on 2019/10/10.
 */
public class NettyServerHandlerImpl extends ChannelHandlerAdapter implements NettyServerHandler{

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);

    }

    @Override
    public void messageRecived(ChannelHandlerContext ctx, RemotingCommand command) {

    }

    @Override
    public void messageRecived(ChannelHandlerContext ctx, ConnectRequest request) {

    }
}
