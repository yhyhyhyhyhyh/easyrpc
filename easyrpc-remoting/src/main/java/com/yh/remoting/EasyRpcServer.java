package com.yh.remoting;


import com.yh.MixAll;
import com.yh.registry.JdbcRegistryCenter;
import com.yh.registry.RegistryCenter;
import com.yh.registry.model.Instance;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EasyRpcServer implements ApplicationContextAware,SmartLifecycle {

    private NettyServer nettyServer;

    private String token;

    private String providedToken;

    private ApplicationContext ac;

    private Integer port;

    private RegistryCenter registryCenter;

    private String instanceName;

    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

    private Instance registedInstance;

    public  EasyRpcServer(String instanceName,Integer port, DataSource dataSource,String token) {
        this.port = port;
        this.instanceName = instanceName;
        this.registryCenter = new JdbcRegistryCenter(dataSource);
        this.token = UUID.randomUUID().toString();
        this.providedToken = token;
    }

    @Override
    public void start()   {
        //非spring环境下，通过该方法手动启动,spring环境下自动调用
        this.nettyServer = new NettyServer(port,this);
        try {
            nettyServer.start();
            regist();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        startHeartBeatTask();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                registryCenter.unregistInstance(registedInstance);
                scheduledExecutorService.shutdown();
            }
        }));
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
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
        if(StringUtils.isEmpty(providedToken)) {
            return token;
        } else {
            return providedToken;
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    public ApplicationContext getAc() {
        return ac;
    }


    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        //IGNORE
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
