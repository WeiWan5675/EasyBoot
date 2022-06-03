package com.weiwan.easyboot.security;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.weiwan.easyboot.model.entity.*;
import com.weiwan.easyboot.component.file.FileService;
import com.weiwan.easyboot.service.AbstractTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.weiwan.easyboot.model.dto.UserAo;
import com.weiwan.easyboot.model.dto.UserInfo;
import com.weiwan.easyboot.service.SysMenuService;
import com.weiwan.easyboot.service.SysRoleService;
import com.weiwan.easyboot.service.SysUserService;
import com.weiwan.easyboot.model.enums.EntityStatus;

import lombok.RequiredArgsConstructor;

/**
 * @author xiaozhennan
 */
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService extends AbstractTransactionService {

    private final SysRoleService sysRoleService;
    private final SysMenuService sysMenuService;
    private final SysUserService sysUserService;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public List<SysRole> findRolesByUserId(@NotNull Integer userId) {
        if (Objects.equals(SecurityUtils.SUPER_ADMIN_USER_ID, userId)) {
            SysRoleQuery sysRoleQuery = new SysRoleQuery();
            sysRoleQuery.setStatus(EntityStatus.NORMAL.status());
            return sysRoleService.find(sysRoleQuery);
        }
        return sysRoleService.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<SysMenu> findMenusByUserId(@NotNull Integer userId) {
        if (Objects.equals(SecurityUtils.SUPER_ADMIN_USER_ID, userId)) {
            SysMenuQuery sysMenuQuery = new SysMenuQuery();
            sysMenuQuery.setStatus(EntityStatus.NORMAL.status());
            return sysMenuService.find(sysMenuQuery);
        }

        List<SysMenu> sysMenus = sysMenuService.findByUserId(userId);
        boolean match = sysMenus.stream().anyMatch(menu -> SysMenu.DEFAULT_HOME.equals(menu.getUrl()));
        if (!match) {
            SysMenuQuery sysMenuQuery = new SysMenuQuery();
            sysMenuQuery.setUrl(SysMenu.DEFAULT_HOME);
            SysMenu home = sysMenuService.findOne(sysMenuQuery);
            Assert.notNull(home, "系统菜单配置不正确，缺少" + SysMenu.DEFAULT_HOME + "，请联系管理员");
            sysMenus.add(home);
        }

        return sysMenus;
    }

    @Transactional(readOnly = true)
    public UserInfo userInfo(@NotNull Integer userId) {
        SysUser sysUser = sysUserService.find(userId);
        Assert.notNull(sysUser, "用户不存在");
        UserInfo userInfo =
            new UserInfo(sysUser.getId(), sysUser.getDeptId(), sysUser.getUsername(), sysUser.getName());
        userInfo.setDeptName(sysUser.getDeptName());
        userInfo.setPhotoUrl(fileService.getUrl(sysUser.getPhoto()));
        userInfo.setPhoto(sysUser.getPhoto());
        userInfo.setMobile(sysUser.getMobile());
        userInfo.setEmail(sysUser.getEmail());
        return userInfo;
    }

    public int saveUserInfo(@NotNull Integer userId, @NotNull UserAo userAo) {
        SysUser sysUser = this.sysUserService.find(userId);
        Assert.notNull(sysUser, "user must not null");
        sysUser.setName(userAo.getName());
        sysUser.setEmail(userAo.getEmail());
        sysUser.setPhoto(userAo.getPhoto());
        return this.sysUserService.update(sysUser);
    }

    public boolean updatePassword(@NotNull Integer userId, @NotEmpty String oldPassword, @NotEmpty String newPassword) {
        SysUser sysUser = this.sysUserService.find(userId);
        Assert.notNull(sysUser, "user must not null");
        boolean matches = AdminPasswordEncoder.matches(oldPassword, sysUser.getPassword());
        if (matches) {
            sysUser.setPassword(AdminPasswordEncoder.encode(newPassword));
            this.sysUserService.update(sysUser);
            return true;
        }
        return false;
    }
}
