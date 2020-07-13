package com.yh.registry.model;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Instance {

    private static final ThreadLocal<SimpleDateFormat> smf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private Long id;

    private String hostname;

    private String ip;

    private String instanceName;

    private Integer port;

    private String token;

    private Date lastHearBeat;

    private Date registTime;

    private Date createTime;

    private Integer status;

    private Long registTimestamp;

    private Integer weight;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLastHearBeat() {
        return lastHearBeat;
    }

    public void setLastHearBeat(Date lastHearBeat) {
        this.lastHearBeat = lastHearBeat;
    }

    public Date getRegistTime() {
        return registTime;
    }

    public void setRegistTime(Date registTime) {
        this.registTime = registTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getRegistTimestamp() {
        return registTimestamp;
    }

    public void setRegistTimestamp(Long registTimestamp) {
        this.registTimestamp = registTimestamp;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public static List<Instance> ListMap2List(List<Map<String,Object>> list) {
        try {
            List<Instance> instances = new ArrayList<>();
            for(Map<String,Object> map : list) {
                Instance instance = new Instance();
                Object id = map.get("id");
                instance.setId(id == null?null:Long.parseLong(((Integer)id).toString()));
                Object ip = map.get("ip");
                instance.setIp(ip == null?null:(String)ip);
                Object hostname = map.get("hostname");
                instance.setHostname(hostname == null?null:(String)hostname);
                Object instanceName = map.get("instance_name");
                instance.setInstanceName(instanceName == null?null:(String)instanceName);
                Object port = map.get("port");
                instance.setPort(port == null?null:(Integer) port);
                Object token = map.get("token");
                instance.setToken(token == null?null:(String)token);
                Object lastHeartBeat = map.get("last_heartbeat");
                instance.setLastHearBeat(lastHeartBeat == null?null:smf.get().parse((String)lastHeartBeat));
                Object registTime = map.get("regist_time");
                instance.setLastHearBeat(registTime == null?null:smf.get().parse((String)registTime));
                instance.setRegistTimestamp(instance.getLastHearBeat().getTime()/1000);
                Object createTime = map.get("create_time");
                instance.setLastHearBeat(createTime == null?null:smf.get().parse((String)createTime));
                Object status = map.get("status");
                instance.setStatus((status == null ? null : (Integer)status));
                Object weight = map.get("weight");
                instance.setWeight((weight == null ? null : (Integer)weight));
                instances.add(instance);
            }
            return instances;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
