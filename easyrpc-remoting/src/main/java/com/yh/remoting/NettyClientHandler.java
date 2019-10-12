package com.yh.remoting;

import com.yh.protocol.RpcResult;
import com.yh.rpc.Response;
import io.netty.channel.ChannelHandlerContext;

public interface NettyClientHandler {

    void messageRecived(ChannelHandlerContext ctx, Response response);

}
