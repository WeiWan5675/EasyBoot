package com.weiwan.easyboot.exception;

/**
 * 运行时候异常
 *
 * @author xiaozhennan
 */
public class ServerRuntimeException extends RuntimeException {

    public ServerRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public ServerRuntimeException(String message) {
        super(message);
    }
}
