package com.yh.protocol;

import java.io.Serializable;

public class RemotingCommand implements Serializable {

    //实例名称
    private String instanceId;
    //类名
    private String className;
    //方法名
    private String methodName;
    //方法调用类型
    private Call callType = Call.VIRTUAL;
    //方法入参
    private ParameterHolder args;
    //重试次数
    private Integer retries = 0;
    //若调用类型是virtual，则通过该参数判断是否从ioc容器中获取方法接收者
    private Boolean isIocBean = Boolean.FALSE;
    //若从ioc容器中获取方法接收者，则可根据beanName获取，若该字段为空，则根据类型获取
    private String beanName;
    //若类型是VIRTUAL，且实例构造器存在参数，则需要传入构造器参数
    private ParameterHolder constructorArgs = new ParameterHolder(null);


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

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Boolean isIocBean() {
        return isIocBean;
    }

    public void setIocBean(Boolean iocBean) {
        isIocBean = iocBean;
    }

    public ParameterHolder getArgs() {
        return args;
    }

    public void setArgs(ParameterHolder args) {
        this.args = args;
    }

    public Boolean getIocBean() {
        return isIocBean;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public ParameterHolder getConstructorArg() {
        return constructorArgs;
    }

    public void setConstructorArgs(ParameterHolder constructorArgs) {
        this.constructorArgs = constructorArgs;
    }
}
