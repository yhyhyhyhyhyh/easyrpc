package com.yh.refer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName References
 * @Description TODO
 * @Author yh
 * @Date 2019/12/24 15:07
 * @Version 1.0
 */
public class References {

    private static final ConcurrentMap<String,Reference> referencesMap = new ConcurrentHashMap(2<<8,0.75F);

    public static synchronized Reference getReference(String key) {
        return referencesMap.get(key);
    }

    public static void addReference(String key,Reference reference) {
        referencesMap.put(key,reference);
    }

}
