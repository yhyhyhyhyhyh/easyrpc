package com.yh.rpc;

import com.yh.RemotingException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResponseFuture {

    private volatile Response response;

    private long requestId;

    private final Lock lock = new ReentrantLock();

    private final Condition hasResponse = lock.newCondition();

    public ResponseFuture(long requestId) {
        this.requestId = requestId;
    }

    public Response get(int wait) {
        try {
            long start = System.currentTimeMillis();
            lock.lock();
            while(response == null) {
                hasResponse.await(wait, TimeUnit.MICROSECONDS);
                if(response == null&&System.currentTimeMillis()-start>wait) {
                    response = new Response();
                    response.setRequestId(requestId);
                    response.setException(new RemotingException("响应超时"));
                }
            }
        } catch (Exception e) {
            response = new Response();
            response.setRequestId(requestId);
            response.setException(new RemotingException(e.getMessage()));
        } finally {
            Request2ResponseContext.finishResponse(requestId);
            lock.unlock();
        }
        return response;
    }

    public void Received() {
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
