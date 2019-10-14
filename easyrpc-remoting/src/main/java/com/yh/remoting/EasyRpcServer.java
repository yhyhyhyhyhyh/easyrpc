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

    private Integer port;

    public  EasyRpcServer(Integer port) {
        this.port = port;
        this.token = UUID.randomUUID().toString();
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
        this.nettyServer = new NettyServer(port,token,ac);
        nettyServer.start();
    }

}
