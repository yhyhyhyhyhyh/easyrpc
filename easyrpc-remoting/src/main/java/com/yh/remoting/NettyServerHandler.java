package com.yh.remoting;


import com.yh.rpc.Request;
import com.yh.rpc.ShutdownRequest;
import io.netty.channel.ChannelHandlerContext;

public interface NettyServerHandler {

    void messageRecived(ChannelHandlerContext ctx, Request request);

    void messageRecived(ChannelHandlerContext ctx, ShutdownRequest request);
}
