package com.weiwan.easyboot.security;

import com.weiwan.easyboot.component.file.FileService;
import com.weiwan.easyboot.model.dto.UserInfo;
import com.weiwan.easyboot.model.entity.SysMenu;
import com.weiwan.easyboot.model.entity.SysRole;
import com.weiwan.easyboot.model.entity.SysUser;
import com.weiwan.easyboot.model.enums.DataScope;
import com.weiwan.easyboot.model.enums.EntityStatus;
import com.weiwan.easyboot.model.enums.LockType;
import com.weiwan.easyboot.security.lock.LoginLockService;
import com.weiwan.easyboot.service.SysUserService;
import com.weiwan.easyboot.utils.IpUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 用户加载逻辑
 *
 * @author xiaozhennan
 */
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private LoginUserService loginUserService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private FileService fileService;
    @Autowired
    private LoginLockService loginLockService;


    @SneakyThrows
    @Override
    public LoginUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String remoteIp = IpUtil.getRemoteIp(request);
        boolean ipLocked = loginLockService.getIpLockStrategy().isLocked(remoteIp);
        if (ipLocked) {
            throw new LockedException(LockType.IP.name());
        }
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("username is empty");
        }

        // 获取登录标识,这里可以区分登录类型
        final String type = request.getParameter("type");
        SysUser user = null;
        if (StringUtils.isNotEmpty(type)) {
            user = sysUserService.findByUsername(username);
            if (null == user) {
                throw new UsernameNotFoundException("user not found");
            }
            username = user.getUsername();
            user.setPassword(PasswordUtil.NONE_PASSWORD);
        } else {
            boolean locked = loginLockService.getUsernameLockStrategy().isLocked(username);
            if (locked) {
                throw new LockedException(LockType.USERNAME.name());
            }
            user = sysUserService.findByUsername(username);
            if (null == user) {
                throw new UsernameNotFoundException(username + "  not found");
            }
        }

        if (EntityStatus.INVALID.status() == user.getStatus()) {
            throw new DisabledException(username + " disabled");
        }

        // 角色
        List<SysRole> userRoles = loginUserService.findRolesByUserId(user.getId());

        UserInfo userInfo = new UserInfo(user.getId(), user.getDeptId(), user.getUsername(), user.getName());
        userInfo.setDeptName(user.getDeptName());
        userInfo.setPhoto(fileService.getUrl(user.getPhoto()));
        userInfo.setDsf(this.getDsf(userRoles, user.getId(), user.getDeptId()));
        // 角色及权限信息登录成功后放入
        LoginUserDetails userDetails = new LoginUserDetails(userInfo, username, user.getPassword());
        userDetails.setAuthorities(getAuthorities(userRoles, user.getId()));
        return userDetails;
    }



    /**
     *
     * @return
     */
    private List<UserGrantedAuthority> getAuthorities(List<SysRole> userRoles, Integer userId) {
        List<UserGrantedAuthority> authorities = new ArrayList<>();

        userRoles.stream().filter(v -> StringUtils.isNotBlank(v.getCode())).forEach(v -> {
            authorities.add(new UserGrantedAuthority(v.getCode(), true));
        });
        List<SysMenu> userMenus = loginUserService.findMenusByUserId(userId);
        userMenus.stream().filter(v -> StringUtils.isNotBlank(v.getPermission())).forEach(v -> {
            authorities.add(new UserGrantedAuthority(v.getPermission()));
        });
        return authorities;
    }

    private String getDsf(List<SysRole> userRoles, Integer userId, Integer deptId) {
        if (SecurityUtils.isSuperAdmin(userId) || CollectionUtils.isEmpty(userRoles) || Objects.isNull(deptId)) {
            return null;
        }
        Optional<Integer> minOptional =
            userRoles.stream().map(SysRole::getDataScope).distinct().min(Integer::compareTo);
        if (!minOptional.isPresent()) {
            return null;
        }
        String defaultTableAliasPrefix = "";
        Integer dataScope = minOptional.get();
        if (Objects.equals(dataScope, DataScope.ALL.code())) { // 全部
            return null;
        } else if (Objects.equals(dataScope, DataScope.SELF_DEPT.code())) { // 本部门
            return " EXISTS (SELECT 1 FROM sys_user _dsf_sys_user WHERE _dsf_sys_user.dept_id = " + deptId
                + " " + defaultTableAliasPrefix + "create_by = _dsf_sys_user.create_by) ";
        } else if (Objects.equals(dataScope, DataScope.SELF_DEPT_AND_BELOW.code())) { // 本部门及以下
            return " EXISTS (SELECT 1 FROM sys_user _dsf_sys_user WHERE (_dsf_sys_user.dept_id = " + deptId
                + " OR _dsf_sys_user.dept_id like '%/" + deptId + "/%') AND  " + defaultTableAliasPrefix
                + "create_by = _dsf_sys_user.create_by) ";
        } else if (Objects.equals(dataScope, DataScope.SELF.code())) { // 本人
            return " " + defaultTableAliasPrefix + "create_by = " + userId + " ";
        }
        return null;
    }

}
