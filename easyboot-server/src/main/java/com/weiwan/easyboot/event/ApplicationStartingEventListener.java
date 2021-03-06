package com.weiwan.easyboot.event;

import com.weiwan.easyboot.logging.MdcHelper;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

import org.springframework.context.annotation.Configuration;

/**
 * 监听spring boot 应用启动开始事件
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/reference/htmlsingle/#boot-features-application-events-and-listeners">boot-features-application-events-and-listeners</a>
 * @author xiaozhennan
 */
@Configuration
public class ApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        // 添加启动线程号
        MdcHelper.put();
    }
}
