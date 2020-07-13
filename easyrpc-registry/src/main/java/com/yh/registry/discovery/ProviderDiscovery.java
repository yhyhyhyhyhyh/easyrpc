package com.yh.registry.discovery;


import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public interface ProviderDiscovery {

    /**
     * 定时轮询服务提供者并返回新注册的节点信息
     * @param timestamp
     * @return
     */
    List<Map<String,Object>> intervalJdbcDiscovery(Long timestamp, DataSource dataSource);

    /**
     * 通过通知获取新注册的节点信息
     * @param object
     * @return
     */
    List<Map<String,Object>> callBackDiscovery(Object... object);
}
