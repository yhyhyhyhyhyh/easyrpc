package com.yh.registry;


import java.util.List;
import java.util.Map;

public interface LoadBalancingStrategy {

    /**
     * 负载均衡策略
     * @param instanceList
     * @return
     */
    Map<String,Object> loadBalanceing(List<Map<String,Object>> instanceList);
}
