package com.yh.rpc;


import com.yh.RemotingException;
import com.yh.protocol.RpcResult;

import java.io.Serializable;

public class Response implements Serializable{

    private RpcResult rpcResult;

    private long requestId;

    private RemotingException exception;

    public RpcResult getRpcResult() {
        return rpcResult;
    }

    public void setRpcResult(RpcResult rpcResult) {
        this.rpcResult = rpcResult;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public RemotingException getException() {
        return exception;
    }

    public void setException(RemotingException exception) {
        this.exception = exception;
    }

    public Response(long requestId, RemotingException exception) {
        this.requestId = requestId;
        this.exception = exception;
    }

    public Response( long requestId,RpcResult rpcResult) {
        this.rpcResult = rpcResult;
        this.requestId = requestId;
    }
}
