package com.yh.balance;


import com.yh.remoting.NettyClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName RoundRobinStragy
 * @Description 轮询策略
 * @Author yh
 * @Date 2019/12/2 10:09
 * @Version 1.0
 */
public class RoundRobinStragy implements LoadBalancingStrategy{

    private static AtomicInteger atIndex = new AtomicInteger(-1);

    @Override
    public NettyClient loadBalanceing(List<NettyClient> instanceList) {
        try {
            for(int i = 0,len = instanceList.size();i<len;i++) {
                NettyClient client = instanceList.get(getNextIndex(len));
                if(client.isAvailable()) {
                    return client;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized int getNextIndex(int mod) {
        int index = atIndex.incrementAndGet();
        return index%mod;
    }
}
