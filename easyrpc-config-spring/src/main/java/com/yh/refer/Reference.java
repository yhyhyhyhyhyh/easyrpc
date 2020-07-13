package com.yh.refer;

import com.yh.ref.EasyRpcReference;
import java.lang.reflect.Proxy;

/**
 * @ClassName Reference
 * @Description TODO
 * @Author yh
 * @Date 2019/12/24 15:18
 * @Version 1.0
 */
public class Reference {

    private EasyRpcReference easyRpcReference;

    private Class interfaceClass;

    private Object refProxy;

    public Object getRefProxy() {
        return refProxy;
    }

    private void setRefProxy(Object refProxy) {
        this.refProxy = refProxy;
    }

    public Reference(EasyRpcReference easyRpcReference, Class clazz ) {
        this.interfaceClass = clazz;
        this.easyRpcReference = easyRpcReference;
        this.refProxy = createProxy();
    }

    private Object createProxy() {
        return Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class[]{interfaceClass},new RpcInvocation(easyRpcReference,interfaceClass));
    }
}
