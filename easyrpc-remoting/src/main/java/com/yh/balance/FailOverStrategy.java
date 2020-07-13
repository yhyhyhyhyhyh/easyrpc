package com.yh.balance;

import com.yh.registry.JdbcRegistryCenter;
import com.yh.remoting.NettyClient;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 故障转移策略
 *
 * @author yjx
 * @date 2019-12-18-15:37
 */
public class FailOverStrategy implements LoadBalancingStrategy {
    private JdbcRegistryCenter jdbcRegistryCenter;

    public FailOverStrategy(JdbcRegistryCenter jdbcRegistryCenter) {
        this.jdbcRegistryCenter = jdbcRegistryCenter;
    }

    @Override
    public NettyClient loadBalanceing(List<NettyClient> instanceList) {
        if (CollectionUtils.isEmpty(instanceList)) {
            return null;
        }
        int i=0;
        for (NettyClient nettyClient : instanceList) {
             if (i == instanceList.size() -1) {
                 return nettyClient;
             }
             Integer count = jdbcRegistryCenter.checkInstanceIsNormal(nettyClient.getHostname(),nettyClient.getPort());
             if (count != null && count.intValue()>0) {
                 return nettyClient;
             }
             i++;
        }
        return null;
    }
}
