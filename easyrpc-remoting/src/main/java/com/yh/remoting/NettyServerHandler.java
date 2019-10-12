package com.yh.remoting;


import com.yh.protocol.ConnectRequest;
import com.yh.protocol.RemotingCommand;
import com.yh.rpc.Request;
import io.netty.channel.ChannelHandlerContext;

public interface NettyServerHandler {

    void messageRecived(ChannelHandlerContext ctx, Request request);

    void messageRecived(ChannelHandlerContext ctx, ConnectRequest request);
}
