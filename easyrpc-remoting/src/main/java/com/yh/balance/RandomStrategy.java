package com.yh.balance;


import com.yh.remoting.NettyClient;

import java.util.*;

/**
 * @ClassName RandomStrategy
 * @Description 随机策略
 * @Author yh
 * @Date 2019/12/2 10:09
 * @Version 1.0
 */
public class RandomStrategy implements LoadBalancingStrategy {


    @Override
    public NettyClient loadBalanceing(List<NettyClient> clientList) {
        Random random = new Random();
        while (clientList.size()>0) {
            try {
                //若随机的提供者可用，则返回
                int weightSum = addUpWeight(clientList);
                int sed = random.nextInt(weightSum);
                NettyClient result = null;
                for(NettyClient client : clientList) {
                    sed-=client.getWeight();
                    if(sed<0) {
                        result = client;
                        break;
                    }
                }
                if(result!=null&&result.isAvailable()) {
                    return result;
                } else if(result!=null){
                    clientList.remove(result);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    private int addUpWeight(List<NettyClient> clients) {
        int addUp = 0;
        for(NettyClient nettyClient : clients) {
            if(nettyClient.isAvailable()) {
                addUp += nettyClient.getWeight();
            }
        }
        return addUp;
    }
}
