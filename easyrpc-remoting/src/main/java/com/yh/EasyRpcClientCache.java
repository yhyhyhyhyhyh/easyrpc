package com.yh;


import com.yh.remoting.EasyRpcClient;

import java.util.concurrent.ConcurrentHashMap;

public class EasyRpcClientCache {

    private static ConcurrentHashMap<String, EasyRpcClient> clientMap = new ConcurrentHashMap();

    public static EasyRpcClient getClient(String instanceName) {
        EasyRpcClient client = clientMap.get(instanceName);
        return client;
    }

    public static Boolean addClient(String instanceName,EasyRpcClient client) {
        clientMap.put(instanceName,client);
        return Boolean.TRUE;
    }
}
