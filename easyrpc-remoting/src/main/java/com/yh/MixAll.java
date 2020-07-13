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
     * 读超时
     */
    public static final Integer READ_TIMEOUT = 600;

    /**
     * 连接超时时间
     */
    public static final Integer CONNECTION_TIMEOUT = 5000;

    /**
     * 服务提供者心跳间隔
     */
    public static final Integer HEART_BEAT = 30;

    /**
     * 消费者轮询间隔
     */
    public static final Integer QUERY_INTERVAL = 30;
}
