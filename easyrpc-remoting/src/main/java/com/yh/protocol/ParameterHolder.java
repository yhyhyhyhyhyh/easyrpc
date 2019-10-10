package com.yh.protocol;


import java.io.Serializable;

public class ParameterHolder implements Serializable{

    public Object[] parameters;

    public ParameterHolder(Object[] parameters) {
        this.parameters = parameters;
    }

}
