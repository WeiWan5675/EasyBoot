package com.weiwan.easyboot;


import com.weiwan.easyboot.config.BootProperties;
import com.weiwan.easyboot.utils.YamlPropertySourceFactory;
import com.weiwan.easyboot.web.WebConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * 程序入口
 *
 * @author xiaozhennan
 */
@SpringBootApplication()
@PropertySource(value = {"classpath:application-default.yaml"}, factory = YamlPropertySourceFactory.class)
@EnableAsync
@EnableOpenApi
@EnableConfigurationProperties({BootProperties.class})
@ServletComponentScan(basePackages = {"com.weiwan.easyboot.web.*"}) // 开启servlet组件注解扫描
@MapperScan({"com.weiwan.easyboot.mapper"})  // 开启mapper组件注解扫描
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import({WebConfig.class, BeanValidatorPluginsConfiguration.class})
public class EasyBootApp {

    public static void main(String[] args) {
        SpringApplication.run(EasyBootApp.class, args);
    }
}

