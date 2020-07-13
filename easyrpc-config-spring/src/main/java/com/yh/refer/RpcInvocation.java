package com.yh.refer;

import com.yh.RemotingException;
import com.yh.protocol.Call;
import com.yh.protocol.ParameterHolder;
import com.yh.protocol.RemotingCommand;
import com.yh.ref.EasyRpcReference;
import com.yh.remoting.EasyRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @ClassName RpcInvocation
 * @Description TODO
 * @Author yh
 * @Date 2019-12-28 14:48
 * @Version 1.0
 */
public class RpcInvocation implements InvocationHandler{

    private EasyRpcReference easyRpcReference;

    private Class interfaceClass;

    public RpcInvocation(EasyRpcReference easyRpcReference,Class interfaceClass) {
        this.easyRpcReference = easyRpcReference;
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if("toString".equals(methodName)) {
            return interfaceClass.getName()+"$EasyRpcProxy";
        }
        EasyRpcClient client = EasyRpcClient.getInstanceFromCache(easyRpcReference.instanceName());
        if(client == null) {
            throw new RemotingException("客户端未初始化");
        }
        RemotingCommand command = new RemotingCommand();
        command.setClassName(interfaceClass.getName());
        command.setVersion(easyRpcReference.version());
        command.setArgs(new ParameterHolder(args));
        command.setBeanName(easyRpcReference.beanName());
        command.setCallType(Call.INTERFACE);
        command.setIocBean(Boolean.TRUE);
        command.setMethodName(methodName);
        command.setRetries("\\d".matches(easyRpcReference.retries())?Integer.parseInt(easyRpcReference.retries()):0);
        return client.sendRequest(command).getResult();
    }
}
