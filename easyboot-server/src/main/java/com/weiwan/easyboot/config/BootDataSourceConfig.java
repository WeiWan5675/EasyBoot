package com.weiwan.easyboot.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author: xiaozhennan
 * @Date: 2022/5/29 18:15
 * @ClassName: BootDatabaseConfig
 * @Description: 数据库连接池相关配置
 **/

@Configuration
@RequiredArgsConstructor()
public class BootDataSourceConfig {

    private final BootProperties bootProperties;

    @Bean()
    @ConfigurationProperties(prefix = "easyboot.database.hikari")
    public HikariDataSource dataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        BootProperties.DatabaseProperties database = bootProperties.getDatabase();
        String url = database.getUrl();
        String username = database.getUsername();
        String password = database.getPassword();
        String schema = database.getSchema();
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setUsername(username);
        hikariDataSource.setPassword(password);
        hikariDataSource.setSchema(schema);
        return hikariDataSource;
    }

}
