package com.aisino;

import static org.junit.Assert.assertTrue;

import com.yh.mvc.ContentTypeEnum;
import com.yh.protocol.Call;
import com.yh.protocol.RestfulRemotingCommand;
import com.yh.protocol.RpcResult;
import com.yh.remoting.EasyRpcClient;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


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
        command.setClassName("com.aisino.protocol.Test");
        RpcResult result = client.sendRequest(new RemotingCommand());
        System.out.println(result.getResult());*/
    }

    @Test
    public void testClient() throws InterruptedException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("dev");
        dataSource.setPassword("dev123456");
        dataSource.setUrl("jdbc:mysql://172.30.11.70:3308/fpdk?useUnicode=true&characterEncoding=utf8&autoReconnect=true&tinyInt1isBit=false");
        EasyRpcClient client =  EasyRpcClient.getInstance("fpdk-api",dataSource,null,null);
        Thread.sleep(100000000L);
    }
    @Test
    public void testRestfulClient() throws InterruptedException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("dev");
        dataSource.setPassword("dev123456");
        dataSource.setUrl("jdbc:mysql://172.30.11.70:3308/fpdk?useUnicode=true&characterEncoding=utf8&autoReconnect=true&tinyInt1isBit=false");
        EasyRpcClient client =  EasyRpcClient.getInstance("fpdkV1",dataSource,null,null);
        RestfulRemotingCommand command = new RestfulRemotingCommand();
        command.setCtlUrl("/remote/allow/pc/rpc2.do");
        command.setMethodType("POST");
        command.setCallType(Call.RESTFUL);
        command.setContentType(ContentTypeEnum.X_WWW_FORM_URLENCODED.getVal());
        Map<String,Object> paramMap = new HashMap<>(2);
        paramMap.put("name","ymxg");
        command.setParamsMap(paramMap);
        RpcResult rpcResult = client.sendRequest(command);
        while (rpcResult == null || rpcResult.getResult() == null) {
            Thread.sleep(200L);
        }
        System.out.println(rpcResult.getResult().toString());
    }
}
