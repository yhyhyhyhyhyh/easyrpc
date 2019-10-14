package com.yh.protocol;

import com.yh.remoting.EasyRpcClient;

/**
 * Created by admin on 2019/10/14.
 */
public class Test {

    public String hello(String p) {
        return "hello " +p;
    }

    public static String hello(String p1,String p2) {
        return "hello " +p1 + "hello " + p2;
    }

    public static void main(String[] args) throws InterruptedException {
        EasyRpcClient client = new EasyRpcClient("test",null);
        System.out.println(client.isAvailable());
        RemotingCommand command = new RemotingCommand();
        command.setCallType(Call.VIRTUAL);
        command.setParameters(new ParameterHolder(new Object[]{"p1"}));
        command.setMethodName("hello");
        command.setClassName("com.yh.protocol.Test");
        RpcResult result = client.sendRequest(new RemotingCommand());
        System.out.println(result.getResult());
    }
}
