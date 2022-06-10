package com.weiwan.easyboot.model.constant;

/**
 * @Author: xiaozhennan
 * @Date: 2022/6/9 21:53
 * @Package: com.weiwan.easyboot.model.constant
 * @ClassName: Constants
 * @Description:
 **/
public class Constants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_KEY = "Authorization";
    public static final long TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7L;
    public static final Object TOKEN_TYPE = "JWT";
}
