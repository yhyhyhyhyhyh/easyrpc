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
        String selectSql = "select * from global_rpc where instance_name=?";
        return jdbcTemplate.queryForList(selectSql,new Object[]{instanceName});
    }

    @Override
    public synchronized Boolean registInstance(Instance instance) {
        String selectSql = "select ip from global_rpc where instance_name=? and ip=? and port=?";
        Map<String,Object> map = jdbcTemplate.queryForMap(selectSql,new Object[]{instance.getInstanceName(),instance.getIp(),instance.getPort()});
        if(map==null || map.isEmpty()) {
            String insertSql = "insert into global_rpc(instance_name,hostname,ip,port,last_heartbeat,regist_time,create_time) " +
                    "values (?,?,?,?,now(),now(),now())";
            jdbcTemplate.update(insertSql,new Object[]{instance.getInstanceName(),instance.getHostname(),instance.getIp(),instance.getPort()});
        } else {
            String updateSql = "update global_rpc sethostname=?,last_heartbeat=now(),,regist_time=now() " +
                    "where instance_name=? and ip=? and port=?";
            jdbcTemplate.update(updateSql,new Object[]{instance.getInstanceName(),instance.getHostname(),instance.getIp(),instance.getPort()});
        }
        return Boolean.TRUE;
    }
}
