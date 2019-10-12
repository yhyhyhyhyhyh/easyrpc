package com.yh.remoting;


import com.yh.RemotingException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.UUID;

public class EasyRpcServer implements ApplicationContextAware {

    private NettyServer nettyServer;

    private String token;

    private ApplicationContext ac;

    public  EasyRpcServer(Integer port) {
        this.nettyServer = new NettyServer(port);
        this.token = UUID.randomUUID().toString();
        nettyServer.start();
    }

    private void regist() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    private Object getBeanByClass(Class clazz) {
        if(ac == null) {
            throw new RemotingException("该服务提供者不支持IOC");
        }
        return ac.getBean(clazz);
    }

    private Object getBeanByName(String beanName) {
        if(ac == null) {
            throw new RemotingException("该服务提供者不支持IOC");
        }
        return ac.getBean(beanName);
    }
}
