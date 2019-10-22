package com.yh.remoting;


import com.yh.MixAll;
import com.yh.RemotingException;
import com.yh.registry.JdbcRegistryCenter;
import com.yh.registry.RegistryCenter;
import com.yh.registry.model.Instance;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EasyRpcServer implements ApplicationContextAware {

    private NettyServer nettyServer;

    private String token;

    private ApplicationContext ac;

    private Integer port;

    private RegistryCenter registryCenter;

    private String instanceName;

    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    private Instance registedInstance;

    public  EasyRpcServer(String instanceName,Integer port, DataSource dataSource,Boolean isIoc) {
        this.port = port;
        this.instanceName = instanceName;
        this.registryCenter = new JdbcRegistryCenter(dataSource);
        this.token = UUID.randomUUID().toString();
    }

    public void start() throws UnknownHostException {
        //非spring环境下，通过该方法手动启动
        this.nettyServer = new NettyServer(port,this);
        nettyServer.start();
        regist();
        startHeartBeatTask();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                registryCenter.unregistInstance(registedInstance);
                scheduledExecutorService.shutdown();
            }
        }));
    }

    private void regist() throws UnknownHostException {
        Instance instance = new Instance();
        instance.setHostname((InetAddress.getLocalHost()).getHostName());
        instance.setInstanceName(instanceName);
        instance.setPort(port);
        instance.setToken(token);
        instance.setIp((InetAddress.getLocalHost()).getHostAddress());
        registryCenter.registInstance(instance);
        this.registedInstance = instance;
    }

    private void startHeartBeatTask() {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    registryCenter.heartBeat(registedInstance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0, MixAll.HEART_BEAT, TimeUnit.SECONDS);
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
        this.nettyServer = new NettyServer(port,this);
        nettyServer.start();
        try {
            regist();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        startHeartBeatTask();
    }

    public ApplicationContext getAc() {
        return ac;
    }


}
