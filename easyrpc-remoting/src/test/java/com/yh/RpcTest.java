package com.yh;

import static org.junit.Assert.assertTrue;

import com.yh.protocol.Call;
import com.yh.protocol.ParameterHolder;
import com.yh.protocol.RemotingCommand;
import com.yh.protocol.RpcResult;
import com.yh.remoting.EasyRpcClient;
import com.yh.remoting.NettyClient;
import com.yh.remoting.NettyServer;
import org.apache.commons.dbcp2.BasicDataSource;
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

    @Test
    public void testClient() throws InterruptedException {
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dataSource.setUsername("dev");
//        dataSource.setPassword("dev123456");
//        dataSource.setUrl("jdbc:mysql://172.30.11.70:3308/fpdk?useUnicode=true&characterEncoding=utf8&autoReconnect=true&tinyInt1isBit=false");
//        EasyRpcClient client =  EasyRpcClient.getInstance("fpdk-api",dataSource,null);
//        Thread.sleep(100000000L);
    }
}
