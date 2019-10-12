package com.yh.rpc;


import com.yh.RemotingException;
import com.yh.protocol.RpcResult;

public class Response {

    private RpcResult rpcResult;

    private Long requestId;

    private RemotingException exception;

    public RpcResult getRpcResult() {
        return rpcResult;
    }

    public void setRpcResult(RpcResult rpcResult) {
        this.rpcResult = rpcResult;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public RemotingException getException() {
        return exception;
    }

    public void setException(RemotingException exception) {
        this.exception = exception;
    }
}
