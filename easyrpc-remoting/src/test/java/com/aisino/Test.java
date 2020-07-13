package com.aisino;

import com.yh.balance.IpHashStrategy;
import com.yh.protocol.Call;
import com.yh.protocol.ParameterHolder;
import com.yh.protocol.RemotingCommand;
import com.yh.protocol.RpcResult;
import com.yh.remoting.EasyRpcClient;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Created by admin on 2019/10/15.
 */
public class Test {

    /*public static void main(String[] args) throws InterruptedException, ExecutionException {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("dev");
        dataSource.setPassword("dev123456");
        dataSource.setUrl("jdbc:mysql://172.30.11.70:3308/fpdk?useUnicode=true&characterEncoding=utf8&autoReconnect=true&tinyInt1isBit=false");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<FutureTask> list = new ArrayList<>();
        for(int i = 0;i<10;i++) {
            FutureTask task = new FutureTask<Integer>(new TestRunnable(dataSource));
            list.add(task);
            executorService.execute(task);
        }
        int sum = 0;
        for(FutureTask callable : list) {
            int singleSum = (Integer) callable.get();
            sum += singleSum;
            System.out.println("单个线程调用次数"+singleSum);
        }
        System.out.println("调用次数:"+sum);
    }*/

    @org.junit.Test
    public void test() throws InterruptedException {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("dev");
        dataSource.setPassword("dev123456");
        dataSource.setUrl("jdbc:mysql://172.30.11.70:3308/fpdk?useUnicode=true&characterEncoding=utf8&autoReconnect=true&tinyInt1isBit=false");
        EasyRpcClient client = null;
        try {
            client = EasyRpcClient.getInstance("fpdk-api",dataSource,new IpHashStrategy(),"123");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i =0;i<1000;i++) {
            Thread.sleep(3000L);
            RemotingCommand command = new RemotingCommand();
            TestModel model = new TestModel();
            model.setParam1("111");
            command.setCallType(Call.VIRTUAL);
            command.setArgs(new ParameterHolder(new Object[]{model}));
            command.setMethodName("test");
            command.setClassName("com.aisino.projects.cmcct.app.RpcComponent");
            command.setBeanName("rpcComponent");
            command.setIocBean(Boolean.TRUE);
            command.setVersion("1.0.0");
            RpcResult result = client.sendRequest(command);
            System.out.println(((TestModel)result.getResult()).getParam1());
        }
    }
}
