package com.weiwan.easyboot.annotation;

import java.lang.annotation.*;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/30 16:40
 * @Package: com.weiwan.easyboot.annotation
 * @ClassName: DataAuthority
 * @Description: 数据权限注解
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataAuthority {
    boolean ignore() default false;
}

