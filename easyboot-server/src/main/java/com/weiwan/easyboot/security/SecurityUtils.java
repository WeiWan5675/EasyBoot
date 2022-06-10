package com.weiwan.easyboot.security;

import com.weiwan.easyboot.model.dto.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

public class SecurityUtils {

    public static final Integer SUPER_ADMIN_USER_ID = 1;
    public static final Integer ANONYMOUS_USER_ID = -1;

    public static Integer getUserId() {
        UserInfo user = getUser();
        Assert.notNull(user, "thread context don't contain any user");
        return user.getUserId();
    }

    public static UserInfo getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 匿名也是Authenticated = true
        if (null != authentication && authentication.isAuthenticated()) {
            // 如果是匿名，返回的是个字符串
            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUserDetails) {
                LoginUserDetails userDetails = (LoginUserDetails)principal;
                return userDetails.getUserInfo();
            }
        }
        return null;
    }

    public static boolean isSuperAdmin(Integer userId) {
        return Objects.equals(userId, SUPER_ADMIN_USER_ID);
    }

    public static boolean isSuperAdmin() {
        return isSuperAdmin(getUserId());
    }

    /**
     * 数据权限sql
     *
     * @return
     */
    public static String getDsf() {
        Optional<UserInfo> user = Optional.ofNullable(getUser());
        return user.isPresent() ? user.get().getDsf() : null;
    }

    public static Integer getUserContextId(){
        UserInfo user = SecurityUtils.getUser();
        return null != user ? user.getUserId() : SecurityUtils.ANONYMOUS_USER_ID;
    }
}