package com.weiwan.easyboot.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
        BootProperties.DatabaseProperties databaseProperties = bootProperties.getDatabase();
        String url = databaseProperties.getUrl();
        String username = databaseProperties.getUsername();
        String password = databaseProperties.getPassword();
        String schema = databaseProperties.getSchema();
        HikariDataSource dataSource = DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .type(HikariDataSource.class)
                .build();
        dataSource.setSchema(schema);
        return dataSource;
    }

}
