package com.yh;


import com.yh.protocol.RemotingCommand;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class ReflectUtil {

    public static Object doVirtualMethod(Object acceptor,RemotingCommand command) {
        try {
            Object[] parameters = command.getParameters().parameters;
            Class[] parameterClass = new Class[parameters == null?0:parameters.length];
            for(int i = 0,len=parameterClass.length;i<len;i++) {
                parameterClass[i] = parameters[i].getClass();
            }
            Method method = ReflectionUtils.findMethod(acceptor.getClass(),command.getMethodName(),parameterClass);
            return method.invoke(acceptor,parameters);
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        }
    }

    public static Object doStaticMethod(Class clazz,RemotingCommand command) {
        try {
            Object[] parameters = command.getParameters().parameters;
            Class[] parameterClass = new Class[parameters == null?0:parameters.length];
            for(int i = 0,len=parameterClass.length;i<len;i++) {
                parameterClass[i] = parameters[i].getClass();
            }
            Method method = ReflectionUtils.findMethod(clazz,command.getMethodName(),parameterClass);
            return method.invoke(null,parameters);
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        }
    }
}
