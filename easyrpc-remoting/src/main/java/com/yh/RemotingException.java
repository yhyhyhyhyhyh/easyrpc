package com.yh;

import java.io.Serializable;

public class RemotingException extends RuntimeException implements Serializable{

    public RemotingException(String message) {
        super(message);
    }
}
