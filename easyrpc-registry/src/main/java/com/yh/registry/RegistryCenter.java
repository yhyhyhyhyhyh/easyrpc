package com.yh.registry;


import com.yh.registry.model.Instance;

import java.util.List;

public interface RegistryCenter {

    /**
     * 根据实例名称获取实例hostName或者ip
     * @param instanceName
     * @return
     */
    List<Instance> getHostListByInstanceName(String instanceName);

    /**
     * 注册实例
     * @param instance
     * @return
     */
    Boolean registInstance(Instance instance);

    /**
     * 更新心跳时间
     * @param instance
     */
    void heartBeat(Instance instance);

    /**
     * 注销实例
     * @param instance
     */
    void unregistInstance(Instance instance);

    /**
     * 根据应用的心跳发送是否正常，判断是否使用该应用节点进行调度
     * @param ip
     * @param port
     * @return
     */
    int checkInstanceIsNormal(String ip,Integer port);

}
