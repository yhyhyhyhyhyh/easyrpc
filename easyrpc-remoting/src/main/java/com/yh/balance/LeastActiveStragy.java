package com.yh.balance;

import com.yh.remoting.NettyClient;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @ClassName LeastActiveStragy
 * @Description 最少调用优先策略
 * @Author yh
 * @Date 2019/12/2 10:09
 * @Version 1.0
 */
public class LeastActiveStragy implements LoadBalancingStrategy{

    private Map<NettyClient,AtomicInteger> countMap = new ConcurrentHashMap<>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private ExecutorService singleExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1),
            new ThreadPoolExecutor.AbortPolicy());

    @Override
    public NettyClient loadBalanceing(List<NettyClient> instanceList) {
        Set<NettyClient> keySet = countMap.keySet();
        final List<NettyClient> unAvailableClient = new LinkedList<>();
        Integer min = Integer.MIN_VALUE;
        NettyClient leastClient = null;
        //若发现新的客户端，则将客户端加入map,并直接返回新客户端
        for(NettyClient nettyClient : instanceList) {
            if(nettyClient.isAvailable() && countMap.get(nettyClient) == null) {
                countMap.put(nettyClient,new AtomicInteger(0));
                min = 0;
                leastClient = nettyClient;
            }
        }
        if(min == 0) {
            return leastClient;
        }
        //遍历出调用次数最少的客户端,若遍历到不可用的客户端，则加入待移除的列表
        try {
            readWriteLock.readLock().lock();
            for(NettyClient client :keySet) {
                if(!client.isAvailable()) {
                    unAvailableClient.add(client);
                    continue;
                }
                int clientCount = countMap.get(leastClient).get();
                if(clientCount<min) {
                    min = clientCount;
                    leastClient = client;
                }
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        if(!CollectionUtils.isEmpty(unAvailableClient)) {
            singleExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        readWriteLock.writeLock().lock();
                        for(NettyClient client : unAvailableClient) {
                            countMap.remove(client);
                        }
                    } finally {
                        readWriteLock.writeLock().unlock();
                    }
                }
            });
        }
        return leastClient;
    }

}
