package com.yh.registry;


import com.yh.registry.model.Instance;

import java.util.List;
import java.util.Map;

public interface RegistryCenter {

    /**
     * 根据实例名称获取实例hostName或者ip
     * @param instanceName
     * @return
     */
    List<Map<String,Object>> gethostNameListByInstanceName(String instanceName);

    /**
     * 注册实例
     * @param instance
     * @return
     */
    Boolean registInstance(Instance instance);


}
