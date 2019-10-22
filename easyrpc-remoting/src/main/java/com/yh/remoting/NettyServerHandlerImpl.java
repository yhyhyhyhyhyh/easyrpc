package com.yh.remoting;

import com.yh.ReflectUtil;
import com.yh.RemotingException;
import com.yh.protocol.Call;
import com.yh.protocol.ConnectRequest;
import com.yh.protocol.RemotingCommand;
import com.yh.protocol.RpcResult;
import com.yh.rpc.Request;
import com.yh.rpc.Response;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.net.SocketAddress;

/**
 * Created by admin on 2019/10/10.
 */
public class NettyServerHandlerImpl extends ChannelHandlerAdapter implements NettyServerHandler{

    private String token;

    private ApplicationContext ac;

    public NettyServerHandlerImpl(String token,ApplicationContext ac) {
        this.token = token;
        this.ac = ac;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Request) {
            Request request = (Request)msg;
            if(token.equals(request.getToken())) {
                messageRecived(ctx,(Request)msg);
            } else {
                ctx.writeAndFlush("token校验失败");
                ctx.channel().close();
            }
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
        Long requestId = request.getRequestId();
        try {
            RemotingCommand command = request.getRemotingCommand();
            Boolean isIoc = command.isIocBean();
            if(isIoc && command.getCallType() == Call.VIRTUAL) {
                if( ac == null) {
                    ctx.writeAndFlush(new Response(requestId,new RemotingException("不存在可用的ApplicationContext")));
                    return;
                }

                Object acceptor = command.getBeanName()==null || "".equals(command.getBeanName())?
                        ac.getBean(Class.forName(command.getClassName())):ac.getBean(command.getBeanName());
                Object result = ReflectUtil.doVirtualMethod(acceptor,command);
                ctx.writeAndFlush(new Response(requestId,new RpcResult(result)));
                return;
            } else if(command.getCallType() == Call.VIRTUAL) {
                Class acceptorClass = Class.forName(command.getClassName());
                Constructor acceptorConstructor = acceptorClass.getConstructor();
                Object acceptor = acceptorConstructor.newInstance(command.getConstructorArg().args == null?null:command.getConstructorArg().args);
                Object result = ReflectUtil.doVirtualMethod(acceptor,command);
                ctx.writeAndFlush(new Response(requestId,new RpcResult(result)));
            } else if(command.getCallType() == Call.STATIC) {
                Object result = ReflectUtil.doStaticMethod(Class.forName(command.getClassName()),command);
                ctx.writeAndFlush(new Response(requestId,new RpcResult(result)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.writeAndFlush(new Response(requestId,new RemotingException(e.getMessage())));
            return;
        } finally {
            //ctx.fireChannelRead(request);
        }
    }

    @Override
    public void messageRecived(ChannelHandlerContext ctx, ConnectRequest request) {
        if( request.getToken()==null || !request.getToken().equals(token)) {
            ctx.channel().disconnect();
        }
    }
}
