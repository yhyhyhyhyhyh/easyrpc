package com.yh.rpc;

import com.yh.protocol.RemotingCommand;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class Request implements Serializable{

    private RemotingCommand remotingCommand;

    private static final transient AtomicLong requestIdInc = new AtomicLong(0);

    private transient ResponseFuture responseFuture;

    private Long requestId;

    private int waitMillSeconds = 10*1000;

    private String token;

    public Request() {
        this.requestId = requestIdInc.incrementAndGet();
        this.responseFuture = new ResponseFuture(requestId);
    }

    public Request(int waitMillSeconds) {
        this.waitMillSeconds = waitMillSeconds;
    }

    public RemotingCommand getRemotingCommand() {
        return remotingCommand;
    }

    public void setRemotingCommand(RemotingCommand remotingCommand) {
        this.remotingCommand = remotingCommand;
    }

    public ResponseFuture getResponseFuture() {
        return responseFuture;
    }

    public void setResponseFuture(ResponseFuture responseFuture) {
        this.responseFuture = responseFuture;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Response getResponse() {
        return responseFuture.get(waitMillSeconds);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public int getWaitMillSeconds() {
        return waitMillSeconds;
    }

    public void setWaitMillSeconds(int waitMillSeconds) {
        this.waitMillSeconds = waitMillSeconds;
    }
}
