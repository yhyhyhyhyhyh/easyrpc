package com.yh.expose;

import java.lang.annotation.*;

/**
 * @ClassName EasyRpcService
 * @Description TODO
 * @Author yh
 * @Date 2020-01-08 15:27
 * @Version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EasyRpcService {

    String version() default "1.0.0";
}
