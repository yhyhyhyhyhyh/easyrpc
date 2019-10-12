package com.yh.rpc;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Request2ResponseContext {

    private static final Map<Long,ResponseFuture> requestMap = new ConcurrentHashMap();

    public static void addResponseFuture(long requestId,ResponseFuture responseFuture) {
        requestMap.put(requestId,responseFuture);
    }

    public static void finishResponse(long requestId) {
        requestMap.remove(requestId);
    }

    public static ResponseFuture getResponseFuture(long requestId) {
        return requestMap.get(requestId);
    }
}
