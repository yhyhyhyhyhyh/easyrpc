package com.yh.remoting;


public class EasyRpcClient {

    private NettyClient nettyClient;

    private String instanceName;

    public EasyRpcClient() throws InterruptedException {
        this.nettyClient = new NettyClient(hostname,port);
        nettyClient.doConnect();
    }

}
