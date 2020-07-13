package com.yh.protocol;

import java.util.Map;

public class RestfulRemotingCommand extends RemotingCommand {
    private String ctlUrl;
    private String methodType;
    private String contentType;
    private Map<String,Object> paramsMap;
    private String paramJson;

    public String getCtlUrl() {
        return ctlUrl;
    }

    public void setCtlUrl(String ctlUrl) {
        this.ctlUrl = ctlUrl;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
}
