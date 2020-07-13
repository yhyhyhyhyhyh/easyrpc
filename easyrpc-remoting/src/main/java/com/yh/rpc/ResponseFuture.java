package com.yh.rpc;

import com.yh.RemotingException;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResponseFuture implements Serializable{

    private volatile Response response;

    private Long requestId;

    private final transient Lock lock = new ReentrantLock();

    private final transient Condition hasResponse = lock.newCondition();

    public ResponseFuture(Long requestId) {
        this.requestId = requestId;
    }

    public Response get(int wait) {
        try {
            long start = System.currentTimeMillis();
            lock.lock();
            while(response == null) {
                hasResponse.await(wait, TimeUnit.MICROSECONDS);
                if(response == null&&System.currentTimeMillis()-start>wait) {
                    response = new Response(requestId,new RemotingException("响应超时"));
                }
            }
        } catch (Exception e) {
            response = new Response(requestId,new RemotingException("响应超时"));
        } finally {
            Request2ResponseContext.finishResponse(requestId);
            lock.unlock();
        }
        return response;
    }

    public void received() {
        if( response != null) {
            return;
        }
        lock.lock();
        try {
            hasResponse.signal();
        } finally {
            lock.unlock();
        }
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }
}
