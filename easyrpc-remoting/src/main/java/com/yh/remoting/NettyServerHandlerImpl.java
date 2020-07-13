package com.yh.remoting;

import com.yh.IocType2Bean;
import com.yh.ReflectUtil;
import com.yh.RemotingException;
import com.yh.expose.EasyRpcService;
import com.yh.mvc.RpcRequestMappingHandlerMapping;
import com.yh.protocol.Call;
import com.yh.protocol.RemotingCommand;
import com.yh.protocol.RpcResult;
import com.yh.rpc.Request;
import com.yh.rpc.Response;
import com.yh.rpc.ShutdownRequest;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.springframework.context.ApplicationContext;
import java.lang.reflect.Constructor;
import java.net.SocketAddress;
import java.util.Map;

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
            } else  {
                ctx.writeAndFlush( ctx.writeAndFlush(new Response(request.getRequestId(),new RemotingException("token校验失败，请确认token是否正确"))));
                ctx.channel().close();
            }
        } else if(msg instanceof ShutdownRequest) {
            messageRecived(ctx,(ShutdownRequest)msg);
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
            Class clazz = null;
            //@EasyRpcReference调用
            if(command.getCallType() == Call.INTERFACE) {
                if( ac == null) {
                    ctx.writeAndFlush(new Response(requestId,new RemotingException("不存在可用的ApplicationContext")));
                    return;
                }
                Object acceptor = null;
                if(!"".equals(command.getBeanName())&&command.getBeanName()!=null) {
                    acceptor = ac.getBean(command.getBeanName());
                } else {
                    clazz = ReflectUtil.findClass(command.getClassName());
                    acceptor = IocType2Bean.getBean(clazz);
                    if(acceptor == null) {
                        Map beanMap = ac.getBeansOfType(ReflectUtil.findClass(command.getClassName()),Boolean.FALSE,Boolean.TRUE);
                        if(beanMap.size() == 0) {
                            ctx.writeAndFlush(new Response(requestId,new RemotingException("不存在该类型的bean")));
                            return;
                        }
                        if(beanMap.size() == 1) {
                            String beanName = (String)beanMap.keySet().iterator().next();
                            acceptor = beanMap.get(beanName);
                            IocType2Bean.put(clazz,acceptor);
                        }
                    }
                }
                if(acceptor == null) {
                    ctx.writeAndFlush(new Response(requestId,new RemotingException("信息不足，存在多个此类型的bean,需指定BeanName")));
                    return;
                }
                //若通过interface调用，则校验@EasyRpcService注解的版本
                clazz = acceptor.getClass();
                Object easyRpcServiceObject = ReflectUtil.findAnnotation(clazz, EasyRpcService.class);
                if(easyRpcServiceObject == null) {
                    ctx.writeAndFlush(new Response(requestId,new RemotingException("请确认类是否已暴露")));
                    return;
                }
                EasyRpcService easyRpcService = (EasyRpcService)easyRpcServiceObject;
                String version = easyRpcService.version();
                if(!version.equals(command.getVersion())) {
                    ctx.writeAndFlush(new Response(requestId,new RemotingException(String.format("版本号不匹配。目标版本号:%s,提供版本号:%s",command.getVersion(),version))));
                    return;
                }
                Object result = ReflectUtil.doVirtualMethod(acceptor,command);
                ctx.writeAndFlush(new Response(requestId,new RpcResult(result)));
                return;
            } else if(command.getCallType()!=Call.RESTFUL) {
                //若不是restful api,则根据@EasyRpc注解校验版本信息，否则跳过@EasyRpc校验逻辑
               clazz = ReflectUtil.findClass(command.getClassName());
                //校验版本信息
                String version = ReflectUtil.getTargetVersion(clazz,command);
                if(version == null) {
                    ctx.writeAndFlush(new Response(requestId,new RemotingException("请确认该方法是否已暴露")));
                    return;
                }
                if(!version.equals(command.getVersion())) {
                    ctx.writeAndFlush(new Response(requestId,new RemotingException(String.format("版本号不匹配。目标版本号:%s,提供版本号:%s",command.getVersion(),version))));
                    return;
                }
            }
            Boolean isIoc = command.isIocBean();
            if(isIoc && command.getCallType() == Call.VIRTUAL) {
                if( ac == null) {
                    ctx.writeAndFlush(new Response(requestId,new RemotingException("不存在可用的ApplicationContext")));
                    return;
                }
                Object acceptor = command.getBeanName()==null || "".equals(command.getBeanName())?
                        ac.getBean(ReflectUtil.findClass(command.getClassName())):ac.getBean(command.getBeanName());
                Object result = ReflectUtil.doVirtualMethod(acceptor,command);
                ctx.writeAndFlush(new Response(requestId,new RpcResult(result)));
                return;
            } else if(command.getCallType() == Call.VIRTUAL) {
                Constructor acceptorConstructor = clazz.getConstructor();
                Object acceptor = acceptorConstructor.newInstance(command.getConstructorArg().args == null?null:command.getConstructorArg().args);
                Object result = ReflectUtil.doVirtualMethod(acceptor,command);
                ctx.writeAndFlush(new Response(requestId,new RpcResult(result)));
            } else if(command.getCallType() == Call.STATIC) {
                Object result = ReflectUtil.doStaticMethod(clazz,command);
                ctx.writeAndFlush(new Response(requestId,new RpcResult(result)));
            } else {
                RpcRequestMappingHandlerMapping customRequestMappingHandlerMapping = ac.getBean(RpcRequestMappingHandlerMapping.class);
                Object result = customRequestMappingHandlerMapping.doRpcHandler(command);
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
    public void messageRecived(ChannelHandlerContext ctx, ShutdownRequest request) {
        ctx.channel().disconnect();
    }
}
