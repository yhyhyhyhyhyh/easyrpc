package com.yh;

public class MixAll {

    /**
     * tcp接收缓冲区大小
     */
    public static final Integer SO_RCVBUF = 4*1024;

    /**
     * tcp发送缓冲区大小
     */
    public static final Integer SO_SNDBUF = 4*1024;

    /**
     * listen函数中的backlog参数
     */
    public static final Integer SO_BACKLOG = 128;

    /**
     * TCP长连接保持秒数
     */
    public static final Integer KEEP_ALIVE = 600;
}
