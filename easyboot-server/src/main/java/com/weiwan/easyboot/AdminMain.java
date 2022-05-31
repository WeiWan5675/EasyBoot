package com.weiwan.easyboot;


import com.weiwan.easyboot.component.YamlPropertySourceFactory;
import com.weiwan.easyboot.config.BootProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * 程序入口
 *
 * @author hdf
 */
@SpringBootApplication()
@EnableConfigurationProperties({BootProperties.class})
@ServletComponentScan(basePackages = {"com.weiwan.easyboot.web.*"})
@PropertySource(value = {"classpath:application-default.yaml"}, factory = YamlPropertySourceFactory.class)
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class AdminMain {

    public static void main(String[] args) {
        SpringApplication.run(AdminMain.class, args);
    }
}
