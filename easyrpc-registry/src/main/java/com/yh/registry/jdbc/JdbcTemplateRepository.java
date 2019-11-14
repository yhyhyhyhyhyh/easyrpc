package com.yh.registry.jdbc;


import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcTemplateRepository {

    private static final Map<DataSource,JdbcTemplate> jdbcTemplateRepository = new ConcurrentHashMap<>();

    public static JdbcTemplate getInstance(DataSource dataSource) {
        if(jdbcTemplateRepository.get(dataSource) == null) {
            return addJdbcTemplateToRep(dataSource);
        } else {
            return jdbcTemplateRepository.get(dataSource);
        }
    }

    private static synchronized JdbcTemplate  addJdbcTemplateToRep(DataSource dataSource) {
        if(jdbcTemplateRepository.get(dataSource) == null) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplateRepository.put(dataSource,jdbcTemplate);
            return jdbcTemplate;
        } else {
            return jdbcTemplateRepository.get(dataSource);
        }
    }
}
