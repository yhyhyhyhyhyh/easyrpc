package com.yh.remoting;

import com.yh.protocol.ConnectRequest;
import com.yh.protocol.RemotingCommand;
import com.yh.rpc.Request;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

/**
 * Created by admin on 2019/10/10.
 */
public class NettyServerHandlerImpl extends ChannelHandlerAdapter implements NettyServerHandler{

    private String token;

    public NettyServerHandlerImpl(String token) {
        this.token = token;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Request) {
            messageRecived(ctx,(Request)msg);
        }
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
    public void messageRecived(ChannelHandlerContext ctx, Request request) {
        RemotingCommand command = request.getRemotingCommand();
        Boolean isIoc = command.isIocBean();

    }

    @Override
    public void messageRecived(ChannelHandlerContext ctx, ConnectRequest request) {
        if( request.getToken()==null || !request.getToken().equals(token)) {
            ctx.channel().disconnect();
        }
    }
}
