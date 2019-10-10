package com.yh.protocol;


import java.io.Serializable;

public class ConnectRequest implements Serializable {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
