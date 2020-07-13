package com.yh.protocol;

import java.io.Serializable;

public enum Call implements Serializable {

    //调用方法类型
    VIRTUAL(0),
    STATIC(1),
    RESTFUL(2),
    INTERFACE(3);

    private Integer CALL;

    Call(Integer call){}
}
