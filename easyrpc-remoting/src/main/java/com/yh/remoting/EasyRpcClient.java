package com.yh.remoting;


import com.yh.EasyRpcClientCache;
import com.yh.MixAll;
import com.yh.RemotingException;
import com.yh.balance.LoadBalancingStrategy;
import com.yh.balance.RandomStrategy;
import com.yh.protocol.RemotingCommand;
import com.yh.protocol.RpcResult;
import com.yh.registry.JdbcRegistryCenter;
import com.yh.registry.RegistryCenter;
import com.yh.registry.model.Instance;
import com.yh.rpc.Request;
import com.yh.rpc.Request2ResponseContext;
import com.yh.rpc.Response;
import com.yh.rpc.ShutdownRequest;
import io.netty.channel.Channel;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EasyRpcClient {

    /**
     * 客户端
     */
    private List<NettyClient> nettyClients = new CopyOnWriteArrayList<>();
    /**
     * 实例名称
     */
    private String instanceName;
    /**
     * 负载均衡策略
     */
    private LoadBalancingStrategy loadBalancingStrategy;
    /**
     * 注册中心
     */
    private RegistryCenter registryCenter;
    /**
     * 是否已经关闭
     */
    private volatile Boolean isShutdown = Boolean.FALSE;
    /**
     * 定时轮询是否有新的节点注册
     */
    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    /**
     * 根据该时间搓判断是否存在新的节点
     */
    private volatile long lastIntervalMills = 0;
    /**
     * 单独提供调用token,优先级高于生成的token
     */
    private String providedToken;

    public static EasyRpcClient getInstance(String instanceName, DataSource dataSource,LoadBalancingStrategy strategy,String providedToken) throws InterruptedException {
        EasyRpcClient client = EasyRpcClientCache.getClient(instanceName);
        if(client == null) {
            synchronized (EasyRpcClient.class) {
                client = EasyRpcClientCache.getClient(instanceName);
                if(client == null) {
                    client =  new EasyRpcClient(instanceName,dataSource,providedToken);
                    //若负载均衡策略不为空，则采用传入的负载均衡策略，否则使用默认的负载均衡策略
                    if(strategy != null) {
                        client.loadBalancingStrategy = strategy;
                    }
                    EasyRpcClientCache.addClient(instanceName,client);
                }
            }
        }
        return client;
    }

    public static EasyRpcClient getInstance(String instanceName,RegistryCenter registryCenter,LoadBalancingStrategy loadBalancingStrategy,String providedToken) throws InterruptedException {
        EasyRpcClient client = EasyRpcClientCache.getClient(instanceName);
        if(client == null) {
            synchronized (EasyRpcClient.class) {
                client = EasyRpcClientCache.getClient(instanceName);
                if(client == null) {
                    client =  new EasyRpcClient(instanceName,registryCenter,loadBalancingStrategy,providedToken);
                    EasyRpcClientCache.addClient(instanceName,client);
                }
            }
        }
        return client;
    }

    public static EasyRpcClient getInstanceFromCache(String instanceName) {
        return EasyRpcClientCache.getClient(instanceName);
    }

    private EasyRpcClient(String instanceName, DataSource dataSource,String providedToken) throws InterruptedException {
        registryCenter = new JdbcRegistryCenter(dataSource);
        loadBalancingStrategy = new RandomStrategy();
        this.instanceName = instanceName;
        this.providedToken = providedToken;
        connectToProvider(instanceName);
        setInterval();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    shutDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private EasyRpcClient(String instanceName,RegistryCenter registryCenter,LoadBalancingStrategy loadBalancingStrategy,String providedToken) throws InterruptedException {
        this.registryCenter = registryCenter;
        this.loadBalancingStrategy = loadBalancingStrategy;
        this.instanceName = instanceName;
        this.providedToken = providedToken;
        connectToProvider(instanceName);
        setInterval();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    shutDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private synchronized void connectToProvider(String instanceName) {
        //连接之前尝试清除无效连接
        removeUnAvailableNettyClients();
        doRefreshConnectToProvider(registryCenter.getHostListByInstanceName(instanceName));
    }



    private void doRefreshConnectToProvider(List<Instance> instanceList) {
        for(Instance instance : instanceList) {
            try {
                //若当前提供者已存在，直接跳过
                if(refreshHostIfExist(instance)) {
                    continue;
                }
                //若providedToken不为空，优先使用providedToken
                Boolean useProvidedToken = !StringUtils.isEmpty(providedToken);
                if(useProvidedToken) {
                    instance.setToken(providedToken);
                }
                NettyClient nettyClient = new NettyClient(instance.getIp(),instance.getPort(),instance.getToken(),instance.getWeight());
                nettyClient.doConnect();
                if(nettyClient.isAvailable()) {
                    long registTime = instance.getRegistTimestamp()/1000;
                    if(registTime>lastIntervalMills) {
                        lastIntervalMills = registTime;
                    }
                    nettyClients.add(nettyClient);
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    private Boolean refreshHostIfExist(Instance instance) {
        //若当前服务提供者已存在，并且可用，则返回TRUE，若不存在或不可用，返回FALSE
        for(NettyClient nettyClient : nettyClients) {
            if(nettyClient.getHostname().equals(instance.getIp())
                    &&nettyClient.getPort().equals(instance.getPort())
                    &&nettyClient.isAvailable()
                    &&nettyClient.getToken().equals(instance.getToken())) {
                //若权重发生了变化，更新权重
                if(!instance.getWeight().equals(nettyClient.getWeight())) {
                    nettyClient.setWeight(instance.getWeight());
                }
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 若至少存在一个服务节点可用，则客户端可用
     * @return
     */
    public Boolean isAvailable() {
        if(isShutdown) {
            return Boolean.FALSE;
        }
        if(CollectionUtils.isEmpty(nettyClients)) {
            return Boolean.FALSE;
        } else {
            for(NettyClient nettyClient : nettyClients) {
                if(nettyClient.isAvailable()) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    public RpcResult sendRequest(RemotingCommand remotingCommand, int waitMills) {
        if(!isAvailable()) {
            connectToProvider(instanceName);
        }
        if(CollectionUtils.isEmpty(nettyClients)) {
            throw new RemotingException("不存在可用的主机");
        }
        long retris = remotingCommand.getRetries();
        if(retris<0) {
            retris = 0;
        }
        Response response = null;
        for(int i =0;i<=retris;i++) {
            try {
                NettyClient nettyClient = loadBalancingStrategy.loadBalanceing(nettyClients);
                if(nettyClient == null) {
                    throw new RemotingException("不存在可用的主机");
                }
                String token = nettyClient.getToken();
                Request request = new Request();
                request.setRemotingCommand(remotingCommand);
                request.setWaitMillSeconds(waitMills);
                request.setToken(token);
                Request2ResponseContext.addResponseFuture(request.getRequestId(),request.getResponseFuture());
                nettyClient.writeAndFlush(request);
                response = request.getResponse();
                RpcResult rpcResult = response.getRpcResult();
                if(rpcResult != null) {
                    return rpcResult;
                }
                if(response.getException() != null) {
                    throw response.getException();
                }
            } catch (Exception e) {
                //若response中携带异常，则向外抛出该异常
                if(response != null&&response.getException()!=null) {
                    throw response.getException();
                }
                //若发送请求失败，且与服务提供者连接全部断开，尝试重新连接
                if(!isAvailable()) {
                    connectToProvider(instanceName);
                }
            }
        }
        return null;
    }

    public RpcResult sendRequest(RemotingCommand remotingCommand) {
        //默认超时时间1分钟
        return sendRequest(remotingCommand,1000*60);
    }


    /**
     * 移除不可用的客户端
     */
    private synchronized void removeUnAvailableNettyClients() {
        List<NettyClient> penddingClients = new ArrayList<>();
        for(NettyClient nettyClient : nettyClients) {
            if(!nettyClient.isAvailable()) {
                penddingClients.add(nettyClient);
            }
        }
        if(!CollectionUtils.isEmpty(penddingClients)) {
            nettyClients.removeAll(penddingClients);
        }
    }

    /**
     * 轮询查询是否有新的节点
     */
    private void setInterval() {
        /**
         * 若服务提供者信息注册在db，则定时轮询db是否存在新注册的节点
         */
        if(registryCenter instanceof JdbcRegistryCenter) {
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<Instance> newInstance = ((JdbcRegistryCenter) registryCenter).intervalQuery(instanceName,lastIntervalMills);
                        if(!CollectionUtils.isEmpty(newInstance)) {
                            lastIntervalMills = newInstance.get(0).getRegistTimestamp();
                            connectToProvider(instanceName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },0, MixAll.QUERY_INTERVAL, TimeUnit.SECONDS);
        }
    }

    public synchronized void shutDown() throws InterruptedException {
        if(!isShutdown) {
            scheduledExecutorService.shutdown();
            for(NettyClient nettyClient : nettyClients) {
                if(nettyClient.isAvailable()) {
                    Channel channel = nettyClient.getChannel();
                    channel.writeAndFlush(new ShutdownRequest());
                    nettyClient.getChannel().closeFuture().sync();
                }
            }
            isShutdown = Boolean.TRUE;
        }
    }
}
