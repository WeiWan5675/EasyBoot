package com.weiwan.easyboot.component.file;

import java.io.IOException;
import java.io.InputStream;

import com.weiwan.easyboot.service.AbstractBaseService;
import org.springframework.stereotype.Service;

import com.weiwan.easyboot.component.file.handler.FileHandler;

import lombok.AllArgsConstructor;

/**
 * 文件模块防腐层
 *
 * @author xiaozhennan
 */
@Service
@AllArgsConstructor
public class FileService extends AbstractBaseService {

    private final FileHandler fileHandler;

    public void upload(String relativePath, String contentType, InputStream in) throws IOException {
        this.fileHandler.upload(relativePath, contentType, in);
    }

    public InputStream download(String relativePath) throws IOException {
        return this.fileHandler.download(relativePath);
    }

    public void delete(String relativePath) throws IOException {
        this.fileHandler.delete(relativePath);
    }

    public String getUrl(String relativePath) {
        return this.fileHandler.getUrl(relativePath);
    }

    public String getId(String relativePath) {
        return this.fileHandler.getId(relativePath);
    }

    public String getUrlPrefix() {
        return this.fileHandler.getUrlPrefix();
    }

}
