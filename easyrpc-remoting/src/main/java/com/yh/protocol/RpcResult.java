package com.yh.protocol;

import java.io.Serializable;

public class RpcResult implements Serializable {

    //返回结果
    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public RpcResult(Object result) {
        this.result = result;
    }
}
