package com.yh.remoting;

import com.yh.rpc.Request2ResponseContext;
import com.yh.rpc.Response;
import com.yh.rpc.ResponseFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

import java.net.SocketAddress;

public class NettyClientHandlerImpl extends ChannelHandlerAdapter implements NettyClientHandler{

    private NettyClient client;

    public NettyClientHandlerImpl(NettyClient client)  {
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if(msg instanceof Response) {
                Response response = (Response)msg;
                messageRecived(ctx,response);
            } else if( msg instanceof String) {
                //TODO 收集错误信息
            }
        } finally {
            ReferenceCountUtil.release(msg);
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
    public void messageRecived(ChannelHandlerContext ctx, Response response) {
        long requestId = response.getRequestId();
        ResponseFuture responseFuture = Request2ResponseContext.getResponseFuture(requestId);
        responseFuture.setResponse(response);
        responseFuture.Received();
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {

    }
}
