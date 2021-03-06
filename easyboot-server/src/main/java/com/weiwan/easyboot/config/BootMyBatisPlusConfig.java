package com.weiwan.easyboot.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.weiwan.easyboot.component.mybatis.DataAuthorityHandler;
import com.weiwan.easyboot.component.mybatis.MultiTenantLineHandler;
import com.weiwan.easyboot.model.enums.DatabaseType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ObjectUtils;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2021/8/21 15:36
 * @Package: com.weiwan.dsp.console.config
 * @ClassName: BootMyBatisPlusConfig
 * @Description: MyBatisPlusConfig
 **/

@Configuration
@EnableTransactionManagement
@Slf4j
public class BootMyBatisPlusConfig {

    @Autowired
    private MybatisPlusProperties properties;

    @Autowired
    private BootProperties bootProperties;

    @Autowired
    private DataAuthorityHandler dataAuthorityHandler;

    @Autowired
    private MultiTenantLineHandler multiTenantLineHandler;

    @Bean(name = "mybatisConfiguration")
    @ConfigurationProperties(prefix = "mybatis-plus.configuration")
    public MybatisConfiguration configuration() {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        mybatisConfiguration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        return mybatisConfiguration;
    }

    @Bean
    @ConfigurationProperties(prefix = "mybatis-plus.db-config")
    public GlobalConfig.DbConfig dbConfig() {
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIdType(IdType.AUTO);
        dbConfig.setTableUnderline(true);
        dbConfig.setCapitalMode(false);
        return dbConfig;
    }

    @Bean
    @ConfigurationProperties(prefix = "mybatis-plus.global-config")
    public GlobalConfig globalConfig(GlobalConfig.DbConfig dbConfig) {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDbConfig(dbConfig);
        globalConfig.setBanner(true);
        globalConfig.setSqlInjector(new DefaultSqlInjector());
        globalConfig.setEnableSqlRunner(true);
        return globalConfig;
    }


    @Bean("mybatisInterceptors")
    public Interceptor[] mybatisInterceptors() {
        List<Interceptor> interceptors = new ArrayList<>();
        MybatisPlusInterceptor PaginationInterceptor = new MybatisPlusInterceptor();
        BootProperties.DatabaseProperties database = bootProperties.getDatabase();
        Properties properties = new Properties();
        // ?????????????????????
        PaginationInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.getDbType(database.getType().name())));
        PaginationInterceptor.setProperties(properties);
        interceptors.add(PaginationInterceptor);

        // ???????????????????????????
        if (bootProperties.getDataAuthority().isEnable() && !bootProperties.getMultiTenant().isEnable()) {
            MybatisPlusInterceptor dataAuthorityInterceptor = new MybatisPlusInterceptor();
            DataPermissionInterceptor dataAuthorityInnerInterceptor = new DataPermissionInterceptor();
            dataAuthorityInnerInterceptor.setDataPermissionHandler(dataAuthorityHandler);
            dataAuthorityInterceptor.addInnerInterceptor(dataAuthorityInnerInterceptor);
        }
        // ????????????????????????
        if (bootProperties.getMultiTenant().isEnable() && !bootProperties.getDataAuthority().isEnable()) {
            MybatisPlusInterceptor tenantLineInterceptor = new MybatisPlusInterceptor();
            TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
            tenantLineInnerInterceptor.setTenantLineHandler(multiTenantLineHandler);
            tenantLineInterceptor.addInnerInterceptor(tenantLineInnerInterceptor);
            tenantLineInterceptor.setProperties(new Properties());
            interceptors.add(tenantLineInterceptor);
        }
        return interceptors.toArray(new Interceptor[interceptors.size()]);
    }


    @Bean("sqlSessionFactory")
    @DependsOn({"globalConfig", "dbConfig", "mybatisConfiguration", "mybatisInterceptors"})
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(MybatisConfiguration mybatisConfiguration, GlobalConfig globalConfig, Interceptor[] mybatisInterceptors, DataSource dataSource) throws IOException {
        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
        mybatisPlus.setDataSource(dataSource);
        mybatisPlus.setVfs(SpringBootVFS.class);
        //??????mapper????????????????????????, ????????????classpath??????db/${dbType}/mapper/*.xml
        DatabaseType type = bootProperties.getDatabase().getType();
        String dbType = type.name().toLowerCase();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = new Resource[0];
        try {
            resources = resolver.getResources("classpath:db/" + dbType + "/mapper/*.xml");
        } catch (IOException e) {
            log.warn("Can not found mapper xml resource");
        }
        if (resources.length != 0) {
            mybatisPlus.setMapperLocations(resources);
        }
        //?????????????????????
        if (!ObjectUtils.isEmpty(mybatisInterceptors)) {
            mybatisPlus.setPlugins(mybatisInterceptors);
        }
        // ??????MyBatisPlusConfiguration, ????????????????????????????????????
        mybatisPlus.setConfiguration(mybatisConfiguration);
        //????????????globalConfig
        mybatisPlus.setGlobalConfig(globalConfig);
        mybatisPlus.setDatabaseIdProvider(new VendorDatabaseIdProvider());
        if (StringUtils.isNotBlank(this.properties.getTypeAliasesPackage())) {
            mybatisPlus.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.isNotBlank(this.properties.getTypeHandlersPackage())) {
            mybatisPlus.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        return mybatisPlus;
    }


}
