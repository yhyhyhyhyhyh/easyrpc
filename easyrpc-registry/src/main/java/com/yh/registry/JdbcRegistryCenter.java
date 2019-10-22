package com.yh.registry;

import com.yh.registry.model.Instance;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2019/10/11.
 */
public class JdbcRegistryCenter implements RegistryCenter {

    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    public JdbcRegistryCenter(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Map<String, Object>> gethostNameListByInstanceName(String instanceName) {
        String selectSql = "select * from global_rpc where instance_name=? and status=1 and last_heartbeat>SUBDATE(now(),interval 90 second)";
        return jdbcTemplate.queryForList(selectSql,new Object[]{instanceName});
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
}
