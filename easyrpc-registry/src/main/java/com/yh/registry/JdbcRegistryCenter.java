package com.yh.registry;

import com.yh.registry.jdbc.JdbcTemplateRepository;
import com.yh.registry.model.Instance;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by admin on 2019/10/11.
 */
public class JdbcRegistryCenter implements RegistryCenter {

    private JdbcTemplate jdbcTemplate;

    private static final String CHECK_IS_NORMAL = "select count(1) from global_rpc where ip=? and port=? and last_heartbeat>SUBDATE(now(),interval ? second)";

    public static final Integer HEART_BEAT = 30;

    public JdbcRegistryCenter(DataSource dataSource) {
        this.jdbcTemplate = JdbcTemplateRepository.getInstance(dataSource);
    }

    @Override
    public List<Instance> getHostListByInstanceName(String instanceName) {
        String selectSql = "select id,instance_name,hostname,ip,weight," +
                "port,token,date_format(last_heartbeat,'%Y-%m-%d %T') last_heartbeat,date_format(regist_time,'%Y-%m-%d %T') regist_time," +
                "date_format(create_time,'%Y-%m-%d %T') create_time,status " +
                "from global_rpc where instance_name=? and status=1 and last_heartbeat>SUBDATE(now(),interval 90 second)";
        List<Map<String,Object>> list =  jdbcTemplate.queryForList(selectSql,new Object[]{instanceName});
        return Instance.ListMap2List(list);
    }

    @Override
    public synchronized Boolean registInstance(Instance instance) {
        String selectSql = "select count(*) from global_rpc where instance_name=? and ip=? and port=?";
        int i = jdbcTemplate.queryForObject(selectSql,new Object[]{instance.getInstanceName(),instance.getIp(),instance.getPort()},int.class);
        if(i == 0) {
            String insertSql = "insert into global_rpc(instance_name,hostname,ip,port,last_heartbeat,regist_time,create_time,status,token) " +
                    "values (?,?,?,?,now(),now(),now(),1,?)";
            jdbcTemplate.update(insertSql,new Object[]{instance.getInstanceName(),instance.getHostname(),instance.getIp(),instance.getPort(),instance.getToken()});
        } else {
            String updateSql = "update global_rpc set hostname=?,last_heartbeat=now(),regist_time=now(),status=1,token=? " +
                    "where instance_name=? and ip=? and port=?";
            jdbcTemplate.update(updateSql,new Object[]{instance.getHostname(),instance.getToken(),instance.getInstanceName(),instance.getIp(),instance.getPort()});
        }
        return Boolean.TRUE;
    }

    @Override
    public void heartBeat(Instance instance) {
        String updateSql = "update global_rpc set last_heartbeat=now() where instance_name=? and ip=? and port=? ";
        jdbcTemplate.update(updateSql,new Object[]{instance.getInstanceName(),instance.getIp(),instance.getPort()});
    }

    @Override
    public void unregistInstance(Instance instance) {
        String updateSql = "update global_rpc set status=0 " +
                " where instance_name=? and ip=? and port=?";
        jdbcTemplate.update(updateSql,new Object[]{instance.getInstanceName(),instance.getIp(),instance.getPort()});
    }

    @Override
    public int checkInstanceIsNormal(String ip, Integer port) {
        int tmp = new Random().nextInt(10)+ HEART_BEAT;
        return jdbcTemplate.queryForObject(CHECK_IS_NORMAL,new Object[]{ip,port,tmp},Integer.class);
    }

    public List<Instance> intervalQuery(String instanceName,long timestamp) {
        String selectSql = "select id,instance_name,hostname,ip," +
                "port,token,date_format(last_heartbeat,'%Y-%m-%d %T') last_heartbeat,date_format(regist_time,'%Y-%m-%d %T') regist_time," +
                "date_format(create_time,'%Y-%m-%d %T') create_time,status,weight " +
                "from global_rpc where instance_name=? and status=1 and last_heartbeat>SUBDATE(now(),interval 90 second) and UNIX_TIMESTAMP(regist_time)>? " +
                "order by regist_time desc";
        List<Map<String,Object>> list = jdbcTemplate.queryForList(selectSql,new Object[]{instanceName,timestamp});
        return Instance.ListMap2List(list);
    }
}
