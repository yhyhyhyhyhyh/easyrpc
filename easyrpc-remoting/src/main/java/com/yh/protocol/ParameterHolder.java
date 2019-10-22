package com.yh.protocol;


import java.io.Serializable;

public class ParameterHolder implements Serializable{

    public Object[] args;

    public ParameterHolder(Object[] args) {
        this.args = args;
    }

}
