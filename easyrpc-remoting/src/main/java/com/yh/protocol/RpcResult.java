package com.yh.protocol;

public class RpcResult {

    //返回结果的json形式
    private String result;
    //返回结果的目标类
    private String responseClass;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(String responseClass) {
        this.responseClass = responseClass;
    }
}
