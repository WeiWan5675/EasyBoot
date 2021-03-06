package com.weiwan.easyboot.component.file;

/**
 * 文件存储类型
 *
 * @author xiaozhennan
 */
public enum StoreType {
    /**
     * 默认为none
     */
    NONE,
    /**
     * 本地文件存储，使用本机磁盘或者共享存储，默认
     */
    LOCAL,
    /**
     * 阿里云oss存储
     */
    ALIYUN_OSS,
    /**
     * Minio对象存储
     */
    MINIO;
}
