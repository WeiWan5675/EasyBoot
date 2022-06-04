package com.weiwan.easyboot.model;

/**
 * 错误码及响应信息结构
 *
 *
 * @author xiaozhennan
 */
public class CodeMsg {

    private String code;
    private String msg;

    public CodeMsg(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
