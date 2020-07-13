package com.yh.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
public class SpringMvcRpcInterceptorRegistry extends RpcInterceptorRegistry {
    @Autowired(required = false)
    List<WebMvcConfigurer> configurers;
    public SpringMvcRpcInterceptorRegistry() {
        if (!CollectionUtils.isEmpty(configurers)) {
            for (WebMvcConfigurer configurer : configurers) {
                configurer.addInterceptors(this);
            }
        }
    }
}
