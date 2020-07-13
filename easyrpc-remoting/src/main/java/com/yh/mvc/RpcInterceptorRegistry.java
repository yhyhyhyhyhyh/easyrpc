package com.yh.mvc;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.List;

/**
 * Created by yingmuxiaoge on 2019/11/17.
 */
public class RpcInterceptorRegistry extends InterceptorRegistry {

    /**
     * Return all registered interceptors.
     */
    public List<Object> getRpcInterceptors() {
        List<Object> objectList = super.getInterceptors();
        return objectList;
    }
}
