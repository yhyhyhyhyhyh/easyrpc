package com.yh.protocol;

public class Parameter {

    //参数序列化为JSON字符串,
    private String parameterStr;
    //客户端反序列化目标类的全限定名
    private String parameterClass;

    public String getParameterStr() {
        return parameterStr;
    }

    public void setParameterStr(String parameterStr) {
        this.parameterStr = parameterStr;
    }

    public String getParameterClass() {
        return parameterClass;
    }

    public void setParameterClass(String parameterClass) {
        this.parameterClass = parameterClass;

        Parameter.class.getDeclaredClasses();
    }

}
