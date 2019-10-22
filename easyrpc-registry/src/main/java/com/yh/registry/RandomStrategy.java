package com.yh.registry;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomStrategy implements LoadBalancingStrategy{


    @Override
    public Map<String, Object> loadBalanceing(List<Map<String, Object>> instanceList) {
        if( instanceList == null || instanceList.size() == 0) {
            return new HashMap<>();
        }
        return instanceList.get((int)(Math.random()*(instanceList.size()-1)));
    }
}
