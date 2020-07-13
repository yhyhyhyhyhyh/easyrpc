package com.yh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName IocType2Bean
 * @Description TODO
 * @Author yh
 * @Date 2020-01-13 11:02
 * @Version 1.0
 */
public class IocType2Bean {

    private static final ConcurrentMap<Class,Object> map = new ConcurrentHashMap(1<<8,1F);

    public static Object getBean(Class clazz) {
        return map.get(clazz);
    }

    public static void put(Class key,Object value) {
        map.put(key,value);
    }
}
