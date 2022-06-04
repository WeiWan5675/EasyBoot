package com.weiwan.easyboot.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiwan.easyboot.model.Result;

/**
 * 返回json 格式respone
 *
 * @author xiaozhennan
 */
public abstract class AbstractJsonResponeHandler {

    @Autowired
    protected ObjectMapper objectMapper;

    protected void sendRespone(HttpServletResponse response, Result result) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String content = objectMapper.writeValueAsString(result);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(content);
            writer.flush();
        }
    }
}
