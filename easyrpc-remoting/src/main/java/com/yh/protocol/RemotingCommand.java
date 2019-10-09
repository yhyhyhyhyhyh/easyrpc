package com.yh.protocol;

public class RemotingCommand {

    //实例名称
    private String instanceId;
    //类名
    private String className;
    //方法调用类型
    private Call callType;
    //方法入参
    private Parameter[] parameters;
    //是否是广播
    private Boolean isBroadcast;
    //重试次数
    private Integer retries;
    //若调用类型是virtual，则通过该参数判断是否从ioc容器中获取方法接收者
    private Boolean isIocBean;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Call getCallType() {
        return callType;
    }

    public void setCallType(Call callType) {
        this.callType = callType;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Boolean getBroadcast() {
        return isBroadcast;
    }

    public void setBroadcast(Boolean broadcast) {
        isBroadcast = broadcast;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Boolean getIocBean() {
        return isIocBean;
    }

    public void setIocBean(Boolean iocBean) {
        isIocBean = iocBean;
    }
}
