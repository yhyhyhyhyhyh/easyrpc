package com.yh.remoting;


import com.yh.protocol.ConnectRequest;
import com.yh.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

public interface NettyServerHandler {

    void messageRecived(ChannelHandlerContext ctx, RemotingCommand command);

    void messageRecived(ChannelHandlerContext ctx, ConnectRequest request);
}
