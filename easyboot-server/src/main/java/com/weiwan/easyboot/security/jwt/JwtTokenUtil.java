package com.weiwan.easyboot.security.jwt;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiwan.easyboot.model.constant.Constants;
import com.weiwan.easyboot.security.LoginUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/6/5 22:08
 * @Package: com.weiwan.easyboot.security.jwt
 * @ClassName: JwtTokenUtil
 * @Description: Jwt工具类
 **/
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final String CLAIMS_ID_KEY = "id";
    private static final String CLAIMS_USERNAME_KEY = "username";
    private static final String CLAIMS_NICKNAME_KEY = "nickname";
    private static final String CLAIMS_USER_ROLES = "roles";
    private static final String CLAIMS_USER_DETAILS = "user_details";
    private static ObjectMapper mapper;

    public static void setObjectMapper(ObjectMapper objectMapper) {
        mapper = objectMapper;
    }

    public static String generateToken(LoginUserDetails user, String secretKey, long expirationTime, SignatureAlgorithm signatureAlgorithm) throws JsonProcessingException {
        //用于存储Payload中的信息
        Map<String, Object> claims = new HashMap<>();
        String username = user.getUsername();

        HashSet<String> roles = new HashSet<>();
        for (GrantedAuthority authority : user.getAuthorities()) {
            roles.add(authority.getAuthority());
        }
        //设置有效载荷(Payload)
        // 设置userid
        claims.put(CLAIMS_ID_KEY, user.getUserInfo().getUserId());
        // 设置username
        claims.put(CLAIMS_USERNAME_KEY, username);
        // 设置nickname
        claims.put(CLAIMS_NICKNAME_KEY, username);
        // 设置roles
        claims.put(CLAIMS_USER_ROLES, roles);
        // 设置user_details
        claims.put(CLAIMS_USER_DETAILS, mapper.writeValueAsString(user));
        //签发时间
        claims.put(Claims.ISSUED_AT, new Date());
        //过期时间
        claims.put(Claims.EXPIRATION, new Date(System.currentTimeMillis() + expirationTime));
        //使用JwtBuilder生成Token，其中需要设置Claims、过期时间，最后再
        String token = Jwts.builder()//获取DefaultJwtBuilder
                .setClaims(claims)//设置声明
                .setHeaderParam("type", Constants.TOKEN_TYPE)
                .setSubject(username)
                .signWith(signatureAlgorithm, TextCodec.BASE64.encode(secretKey))//使用指定加密方式和密钥进行签名
                .compact();//生成字符串类型的Token
        return token;
    }


    public static String getTokenValue(String token) {
        return token.replace(Constants.TOKEN_PREFIX, "");
    }

    public static boolean validateToken(String authToken, String secretKey) {
        Claims claims = parseToken(authToken, secretKey);
        if (claims != null) {
            return true;
        }
        return false;
    }

    public static String getSubject(String token, String secretKey) {
        return parseToken(token, secretKey).getSubject();
    }

    public static Claims parseToken(String token, String secretKey) {
        try {
            String tokenValue = getTokenValue(token);
            return Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(tokenValue).getBody();
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return null;
    }

    public static Date getExpiration(String token, String secretKey) {
        Claims claims = parseToken(token, secretKey);
        return claims.getExpiration();
    }


    /**
     * 判断Token是否过期
     * 使用Token有效载荷中的过期时间与当前时间进行比较
     *
     * @param token 要判断的Token字符串
     * @return 是否过期
     */
    private static boolean isTokenExpired(String token, String secretKey) {
        //从Token中获取有效载荷
        Claims claims = parseToken(token, secretKey);
        if (claims != null) {
            //从有效载荷中获取用户名
            Long expiredTime = claims.get(Claims.EXPIRATION, Long.class);
            if (expiredTime != null && expiredTime < System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }

    public static String getUserDetails(String token, String secretKey) {
        String element = getElement(token, CLAIMS_USER_DETAILS, secretKey, String.class);
        return element;
    }


    /**
     * 解析Token字符串并且从其中的有效载荷中获取指定Key的元素，获取的是指定泛型类型的元素
     *
     * @param token 解析哪个Token字符串并获取其中的有效载荷
     * @param key   有效载荷中元素的Key
     * @param clazz 指定获取元素的类型
     * @param <T>   元素的类型
     * @return 要获取的元素
     */
    public static <T> T getElement(String token, String key, String secretKey, Class<T> clazz) {
        T element;
        try {
            Claims claims = parseToken(token, secretKey);
            element = claims.get(key, clazz);
        } catch (JwtException e) {
            e.printStackTrace();
            return null;
        }
        return element;
    }

}
