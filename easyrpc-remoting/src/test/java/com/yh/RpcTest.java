package com.yh;

import static org.junit.Assert.assertTrue;

import com.yh.protocol.Call;
import com.yh.protocol.ParameterHolder;
import com.yh.protocol.RemotingCommand;
import com.yh.protocol.RpcResult;
import com.yh.remoting.EasyRpcClient;
import com.yh.remoting.NettyClient;
import com.yh.remoting.NettyServer;
import org.junit.Test;


public class RpcTest
{

    @Test
    public void test() throws InterruptedException {
        /*EasyRpcClient client = new EasyRpcClient("test",null);
        System.out.println(client.isAvailable());
        RemotingCommand command = new RemotingCommand();
        command.setCallType(Call.VIRTUAL);
        command.setArgs(new ParameterHolder(new Object[]{"p1"}));
        command.setMethodName("hello");
        command.setClassName("com.yh.protocol.Test");
        RpcResult result = client.sendRequest(new RemotingCommand());
        System.out.println(result.getResult());*/
    }
}
