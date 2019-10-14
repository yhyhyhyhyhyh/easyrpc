package com.yh.remoting;


import com.yh.EasyRpcClientCache;
import com.yh.RemotingException;
import com.yh.protocol.RemotingCommand;
import com.yh.protocol.RpcResult;
import com.yh.registry.JdbcRegistryCenter;
import com.yh.registry.LoadBalancingStrategy;
import com.yh.registry.RandomStrategy;
import com.yh.registry.RegistryCenter;
import com.yh.rpc.Request;
import com.yh.rpc.Request2ResponseContext;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EasyRpcClient {

    private NettyClient nettyClient;

    private String instanceName;

    private LoadBalancingStrategy loadBalancingStrategy;

    private RegistryCenter registryCenter;

    private Map<String,Object> instance;

    public EasyRpcClient getInstance(String instanceName, DataSource dataSource) throws InterruptedException {
        EasyRpcClient client = EasyRpcClientCache.getClient(instanceName);
        if(client == null) {
            client =  new EasyRpcClient(instanceName,dataSource);
            EasyRpcClientCache.addClient(instanceName,client);
            return client;
        }
        return client;
    }

    public EasyRpcClient getInstance(String instanceName,RegistryCenter registryCenter,LoadBalancingStrategy loadBalancingStrategy) throws InterruptedException {
        EasyRpcClient client = EasyRpcClientCache.getClient(instanceName);
        if(client == null) {
            client =  new EasyRpcClient(instanceName,registryCenter,loadBalancingStrategy);
            EasyRpcClientCache.addClient(instanceName,client);
            return client;
        }
        return client;
    }

    public EasyRpcClient(String instanceName, DataSource dataSource) throws InterruptedException {
//        registryCenter = new JdbcRegistryCenter(dataSource);
//        loadBalancingStrategy = new RandomStrategy();
        this.instanceName = instanceName;
        connectToServer(instanceName);
    }

    private EasyRpcClient(String instanceName,RegistryCenter registryCenter,LoadBalancingStrategy loadBalancingStrategy) throws InterruptedException {
        this.registryCenter = registryCenter;
        this.loadBalancingStrategy = loadBalancingStrategy;
        this.instanceName = instanceName;
        connectToServer(instanceName);
    }

    private void connectToServer(String instanceName) {
        Map<String,Object> instance = doConnectToServer(new ArrayList<Map<String, Object>>());
        this.instance = instance;
    }



    private Map<String,Object> doConnectToServer(List<Map<String,Object>> instanceList) {
        //Map<String,Object> instance = loadBalancingStrategy.loadBalanceing(instanceList);
        Map<String,Object> instance = new HashMap<String,Object>();
        instance.put("ip","127.0.0.1");
        instance.put("port","8085");
        try {
            if(instance == null || instance.isEmpty()) {
                throw new RemotingException("实例不存在可用的主机");
            }
            this.nettyClient = new NettyClient((String)instance.get("ip"),Integer.parseInt((String)instance.get("port")));
            nettyClient.doConnect();
            return instance;
        } catch (Exception e) {
            if(instance != null && !instance.isEmpty()) {
                instanceList.remove(instance);
                return doConnectToServer(instanceList);
            } else {
                throw new RemotingException("实例不存在可用的主机");
            }
        }
    }

    public String getInstanceProperty(String key) {
        Object val = instance.get(key);
        if( val == null) {
            return "";
        } else {
            return (String) val;
        }
    }

    public Boolean isAvailable() {
        return nettyClient.isAvailable();
    }

    public RpcResult sendRequest(RemotingCommand remotingCommand,int waitMills) {
        String token = getInstanceProperty("token");
        Request request = new Request();
        request.setRemotingCommand(remotingCommand);
        request.setWaitMillSeconds(waitMills);
        request.setToken(token);
        nettyClient.writeAndFlush(request);
        return request.getRpcResult();
    }

    public RpcResult sendRequest(RemotingCommand remotingCommand) {
        String token = getInstanceProperty("token");
        Request request = new Request();
        request.setRemotingCommand(remotingCommand);
        request.setToken(token);
        nettyClient.writeAndFlush(request);
        Request2ResponseContext.addResponseFuture(request.getRequestId(),request.getResponseFuture());
        return request.getRpcResult();
    }

}
