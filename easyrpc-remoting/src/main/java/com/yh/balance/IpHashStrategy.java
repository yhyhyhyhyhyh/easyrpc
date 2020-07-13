package com.yh.balance;

import com.yh.remoting.NettyClient;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @ClassName IpHashStrategy
 * @Description ipHash策略
 * @Author yh
 * @Date 2019/12/2 10:09
 * @Version 1.0
 */
public class IpHashStrategy implements LoadBalancingStrategy{

    private NettyClient cachedClient;

    @Override
    public NettyClient loadBalanceing(List<NettyClient> instanceList) {
        try {
            if(cachedClient!=null&&cachedClient.isAvailable()) {
                return cachedClient;
            }
            byte[] digest = md5(InetAddress.getLocalHost().getHostAddress());
            long hash = hash(digest,0);
            long index = -1L;
            do {
                long result = hash%instanceList.size();
                if(instanceList.get((int)result).isAvailable()) {
                    index = result;
                } else {
                    instanceList.remove((int)result);
                }
            } while (index == -1&&instanceList.size()>0);
            if(index != -1) {
                cachedClient = instanceList.get((int)index);
                return cachedClient;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private long hash(byte[] digest, int number) {
        return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                | (digest[0 + number * 4] & 0xFF))
                & 0xFFFFFFFFL;
    }

    private byte[] md5(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.reset();
        byte[] bytes = null;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.update(bytes);
        return md5.digest();
    }
}
