package com.yh.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName SpringConfiguration
 * @Description TODO
 * @Author yh
 * @Date 2020-01-09 11:12
 * @Version 1.0
 */
@Configuration
@ConditionalOnProperty(name = {"easyrpc.interface.base.package"})
public class SpringConfiguration {

    @Value("${easyrpc.interface.base.package}")
    private String basePackage;

    @Bean
    public InterfaceCallProccessor interfaceCallProccessor() {
        return new InterfaceCallProccessor(basePackage);
    }
}
