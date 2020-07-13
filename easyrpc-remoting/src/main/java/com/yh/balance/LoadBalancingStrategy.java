package com.yh.balance;


import com.yh.remoting.NettyClient;

import java.util.List;

/**
 * @ClassName LoadBalancingStrategy
 * @Description 负载均衡策略
 * @Author yh
 * @Date 2019/12/2 10:09
 * @Version 1.0
 */
public interface LoadBalancingStrategy {

    /**
     * 负载均衡策略
     * @param instanceList
     * @return
     */
    NettyClient loadBalanceing(List<NettyClient> instanceList);
}
