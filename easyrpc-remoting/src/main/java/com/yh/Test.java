package com.yh;

import com.yh.protocol.Call;
import com.yh.protocol.ParameterHolder;
import com.yh.protocol.RemotingCommand;
import com.yh.protocol.RpcResult;
import com.yh.remoting.EasyRpcClient;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by admin on 2019/10/15.
 */
public class Test {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

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
    }
}
class TestRunnable implements Callable<Integer> {


    private DataSource dataSource;

    public TestRunnable(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Integer call() throws Exception {
        long start = System.currentTimeMillis();
        int count = 0;
        while (true) {
            try {
                if(System.currentTimeMillis() - start>60*1000) {
                    break;
                }
                EasyRpcClient client =  EasyRpcClient.getInstance("fpdk-api",dataSource);
                RemotingCommand command = new RemotingCommand();
                command.setCallType(Call.VIRTUAL);
                command.setArgs(new ParameterHolder(new Object[]{"201609080941191421"}));
                command.setMethodName("getFpdkByDjbh");
                command.setClassName("com.aisino.projects.cmcct.fpdkgl.service.FpdkglService");
                command.setBeanName("fpdkglServiceImp");
                command.setIocBean(Boolean.TRUE);
                RpcResult result = client.sendRequest(command);
                //System.out.println(result.getResult());
                count++;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return count;
    }
}