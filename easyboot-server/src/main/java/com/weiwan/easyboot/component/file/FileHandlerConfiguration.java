package com.weiwan.easyboot.component.file;

import com.weiwan.easyboot.component.file.handler.*;
import com.weiwan.easyboot.config.BootProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.RequiredArgsConstructor;

/**
 * 文件上传下载处理
 *
 * @author hdf
 */
@Configuration
@RequiredArgsConstructor
public class FileHandlerConfiguration {

    private final BootProperties bootProperties;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "easyboot.file.store-type", havingValue = "none")
    public FileHandler noneFileHandler() {
        return new NoneFileHandler();
    }

    @Bean
    @ConditionalOnProperty(name = "easyboot.file.store-type", havingValue = "local")
    public FileHandler localFileHandler() {
        return new LocalFileHandler(bootProperties.getFile());
    }

    @Bean
    @ConditionalOnProperty(name = "easyboot.file.store-type", havingValue = "aliyun_oss")
    public FileHandler aliyunOssFileHandler() {
        return new AliyunOssHandler(bootProperties.getFile());
    }

    @Bean
    @ConditionalOnProperty(name = "easyboot.file.store-type", havingValue = "minio")
    public FileHandler minioFileHandler() {
        return new MinioHandler(bootProperties.getFile());
    }
}
