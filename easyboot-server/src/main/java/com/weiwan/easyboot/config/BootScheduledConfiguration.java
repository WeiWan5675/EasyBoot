package com.weiwan.easyboot.config;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import lombok.RequiredArgsConstructor;

/**
 * 定时配置可选
 * 
 * @see <a>https://docs.spring.io/spring-boot/docs/2.4.5/reference/htmlsingle/#boot-features-task-execution-sch</a>
 *      如需要使用异步，springboot 要求显示的用{@code @EnableScheduling}
 */
@Configuration
@RequiredArgsConstructor
public class BootScheduledConfiguration implements SchedulingConfigurer {

    private static Logger logger = LoggerFactory.getLogger(BootScheduledConfiguration.class);

    private final BootProperties bootProperties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        BootProperties.ScheduledProperties scheduled = bootProperties.getScheduled();
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            new ScheduledThreadPoolExecutor(scheduled.getCorePoolSize());
        scheduledThreadPoolExecutor.setMaximumPoolSize(scheduled.getMaxPoolSize());
        scheduledThreadPoolExecutor.setKeepAliveTime(scheduled.getKeepAliveTime(), TimeUnit.SECONDS);
        scheduledThreadPoolExecutor.setRejectedExecutionHandler((r, executor) -> {
            logger.error("Scheduled exception:scheduled rejected");
        });
        taskRegistrar.setScheduler(scheduledThreadPoolExecutor);
    }
}
