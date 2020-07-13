package com.yh;


import com.yh.expose.EasyRpc;
import com.yh.mvc.RestfulRpc;
import com.yh.protocol.RemotingCommand;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ReflectUtil {

    /**
     * 调用虚方法
     * @param acceptor
     * @param command
     * @return
     */
    public static Object doVirtualMethod(Object acceptor,RemotingCommand command) {
        try {
            Object[] parameters = command.getArgs().args;
            Class[] parameterClass = new Class[parameters == null?0:parameters.length];
            for(int i = 0,len=parameterClass.length;i<len;i++) {
                parameterClass[i] = parameters[i].getClass();
            }
            Method method = ReflectionUtils.findMethod(acceptor.getClass(),command.getMethodName(),parameterClass);
            method.setAccessible(true);
            return method.invoke(acceptor,parameters);
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        }
    }

    /**
     * 调用静态方法
     * @param clazz
     * @param command
     * @return
     */
    public static Object doStaticMethod(Class clazz,RemotingCommand command) {
        try {
            Object[] parameters = command.getArgs().args;
            Class[] parameterClass = new Class[parameters == null?0:parameters.length];
            for(int i = 0,len=parameterClass.length;i<len;i++) {
                parameterClass[i] = parameters[i].getClass();
            }
            Method method = ReflectionUtils.findMethod(clazz,command.getMethodName(),parameterClass);
            method.setAccessible(true);
            return method.invoke(null,parameters);
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        }
    }

    /**
     * 获取目标方法版本号
     * @param clazz
     * @param command
     * @return
     */
    public static String getTargetVersion(Class clazz,RemotingCommand command) {
        try {
            //从方法注解中匹配版本
            Object[] parameters = command.getArgs().args;
            Class[] parameterClass = new Class[parameters == null?0:parameters.length];
            for(int i = 0,len=parameterClass.length;i<len;i++) {
                parameterClass[i] = parameters[i].getClass();
            }
            Method method = ReflectionUtils.findMethod(clazz,command.getMethodName(),parameterClass);
            method.setAccessible(true);
            EasyRpc easyRpc = AnnotationUtils.getAnnotation(method,EasyRpc.class);
            if(easyRpc != null ) {
                return easyRpc.version();
            }
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        }
        return null;
    }

    /**
     * 判断是否为restFul方法
     * @param clazz
     * @param command
     * @return
     */
    public static Boolean isRestful(Class clazz,RemotingCommand command) {
        try {
            //判断方法是否存在@RestfulRpc
            Object[] parameters = command.getArgs().args;
            Class[] parameterClass = new Class[parameters == null?0:parameters.length];
            for(int i = 0,len=parameterClass.length;i<len;i++) {
                parameterClass[i] = parameters[i].getClass();
            }
            Method method = ReflectionUtils.findMethod(clazz,command.getMethodName(),parameterClass);
            method.setAccessible(true);
            RestfulRpc restfulRpc = AnnotationUtils.getAnnotation(method,RestfulRpc.class);
            return restfulRpc != null;
        } catch (Exception e) {
            throw new RemotingException(e.getMessage());
        }
    }

    /**
     * 获取类指定注解
     * @param clazz
     * @param annotationClass
     * @return
     */
    public static Object findAnnotation(Class clazz,Class<? extends Annotation> annotationClass) {
        return AnnotationUtils.findAnnotation(clazz,annotationClass);
    }

    /**
     * 以上线程上下文类加载器、ClassUtils类型的类加载器、启动类加载器的顺序获取类型对象
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> findClass(String className) throws ClassNotFoundException {
        ClassLoader classLoader =  ClassUtils.getDefaultClassLoader();
        return ClassUtils.forName(className,classLoader);
    }
}
