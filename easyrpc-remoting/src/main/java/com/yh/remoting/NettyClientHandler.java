package com.yh.remoting;

import com.yh.protocol.RpcResult;
import io.netty.channel.ChannelHandlerContext;

public interface NettyClientHandler {

    void messageRecived(ChannelHandlerContext ctx, RpcResult rpcResult);

}
