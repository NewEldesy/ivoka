package com.ivoka.api.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setInitialSize(5);
        ds.setMaxTotal(20);
        ds.setMaxIdle(10);
        ds.setMinIdle(5);
        ds.setMaxWaitMillis(10000);
        ds.setValidationQuery("SELECT 1");
        ds.setTestOnBorrow(true);
        ds.setTestWhileIdle(true);
        ds.setTimeBetweenEvictionRunsMillis(30000);
        return ds;
    }
}
