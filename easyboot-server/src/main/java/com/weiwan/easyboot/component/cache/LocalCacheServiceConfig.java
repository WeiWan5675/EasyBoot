package com.weiwan.easyboot.component.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/31 20:15
 * @Package: com.weiwan.easyboot.component.cache
 * @ClassName: LocalCacheServiceConfig
 * @Description:
 **/
@Configuration
public class LocalCacheServiceConfig {

    @Bean
    public LocalCacheManager localCacheManager() {
        return LocalCacheManager.getLocalCacheManager();
    }


}
