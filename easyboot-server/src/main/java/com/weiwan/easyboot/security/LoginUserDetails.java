package com.weiwan.easyboot.security;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;

import com.weiwan.easyboot.model.dto.UserInfo;

/**
 * @author xiaozhennan
 */

public class LoginUserDetails implements org.springframework.security.core.userdetails.UserDetails {


    private UserInfo userInfo;
    private String username;
    private String passowrd;
    private boolean locked;
    private List<UserGrantedAuthority> authorities;


    public LoginUserDetails() {
    }

    public LoginUserDetails(UserInfo userInfo, String username, String passowrd, boolean locked, List<UserGrantedAuthority> authorities) {
        this.userInfo = userInfo;
        this.username = username;
        this.passowrd = passowrd;
        this.locked = locked;
        this.authorities = authorities;
    }

    public LoginUserDetails(UserInfo userInfo, String username, String password) {
        this.userInfo = userInfo;
        this.username = username;
        this.passowrd = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<UserGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getPassowrd() {
        return passowrd;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public String getPassword() {
        return passowrd;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassowrd(String passowrd) {
        this.passowrd = passowrd;
    }
}
