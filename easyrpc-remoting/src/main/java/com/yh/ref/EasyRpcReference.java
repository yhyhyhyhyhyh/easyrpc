package com.yh.ref;

import java.lang.annotation.*;

/**
 * @ClassName EasyRpcReference
 * @Description TODO
 * @Author yh
 * @Date 2019/12/24 15:03
 * @Version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface EasyRpcReference {

    String instanceName() default "";

    String beanName() default "";

    String version() default "1.0.0";

    String retries() default "0";
}
