package com.yh.mvc;

import com.yh.remoting.EasyRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.List;

/**
 * Created by yingmuxiaoge on 2019/11/16.
 */
@Configuration
@ConditionalOnProperty(name = {"project.easyrpc.springboot"})
public class EasyRpcConfiguration implements ServletContextAware{
    private ServletContext servletContext;
    @Bean
    public RpcRequestMappingHandlerMapping rpcRequestMappingHandlerMapping() {
        return new RpcRequestMappingHandlerMapping(servletContext);
    }
    @Bean(name = "rpcInterceptorRegistry")
    public RpcInterceptorRegistry rpcInterceptorRegistry(@Autowired(required = false) List<WebMvcConfigurer> configurers) {
        RpcInterceptorRegistry rpcInterceptorRegistry = new RpcInterceptorRegistry();
        if (!CollectionUtils.isEmpty(configurers)) {
            for (WebMvcConfigurer configurer : configurers) {
                configurer.addInterceptors(rpcInterceptorRegistry);
            }
        }
        return rpcInterceptorRegistry;
    }
    @Bean
    public EasyRpcServer easyRpcServer(@Autowired DataSource dataSource, @Value("${project.easyrpc.instanceName}")String instanceName,@Value("${project.easyrpc.port}")int port) {
        EasyRpcServer easyRpcServer = new EasyRpcServer(instanceName,port,dataSource,null);
        return easyRpcServer;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
