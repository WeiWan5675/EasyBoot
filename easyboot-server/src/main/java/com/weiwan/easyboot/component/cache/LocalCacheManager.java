package com.weiwan.easyboot.component.cache;

import cn.hutool.cache.impl.TimedCache;
import io.minio.ObjectReadArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/31 18:05
 * @Package: com.weiwan.easyboot.component.cache
 * @ClassName: LocalCacheManager
 * @Description: 本地缓存管理者
 **/
public class LocalCacheManager {

    private static LocalCacheManager cacheManager;
    private LocalCacheStorage<String, Object> localCache;
    private static final Logger logger = LoggerFactory.getLogger(LocalCacheManager.class);

    private LocalCacheManager() {
    }

    public static LocalCacheManager getLocalCacheManager() {
        if (cacheManager == null) {
            synchronized (LocalCacheManager.class) {
                if (cacheManager == null) {
                    cacheManager = new LocalCacheManager();
                    cacheManager.init();
                }
            }
        }
        return cacheManager;
    }

    private void init() {
        // 这里默认使用hutool得TimedCache, 后边如果想扩展也可
        this.localCache = new TimedLocalCacheStorage();
        try {
            this.localCache.init();
        } catch (Exception e) {
            logger.error("Failed to initialize local cache");
        }
    }


    public LocalCacheStorage<String, Object> getCacheStorage() {
        return this.localCache;
    }

}
