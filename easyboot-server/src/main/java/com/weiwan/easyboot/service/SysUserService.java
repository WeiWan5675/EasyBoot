package com.weiwan.easyboot.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.weiwan.easyboot.mapper.SysUserMapper;
import com.weiwan.easyboot.mapper.SysUserRoleDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.weiwan.easyboot.model.entity.SysUser;
import com.weiwan.easyboot.model.entity.SysUserQuery;
import com.weiwan.easyboot.model.entity.SysUserRole;

import lombok.RequiredArgsConstructor;

/**
 * 用户信息
 *
 * @author easyboot-generator 2021年1月16日 下午11:55:54
 */
@Service
@RequiredArgsConstructor
public class SysUserService extends AbstractCrudService<SysUserMapper, SysUser, Integer> {

    private final SysUserRoleDao sysUserRoleDao;

    @Transactional(readOnly = true)
    @Override
    public SysUser find(@NotNull Integer integer) {
        SysUser sysUser = super.find(integer);
        if (null != sysUser) {
            sysUser.setRoleIds(sysUserRoleDao.selectRoleIdsByUserId(sysUser.getId()));
        }
        return sysUser;
    }

    @Transactional(readOnly = true)
    public SysUser findByUsername(@NotEmpty String username) {
        SysUserQuery sysUserQuery = new SysUserQuery();
        sysUserQuery.setUsername(username);
        return this.findOne(sysUserQuery);
    }

    @Transactional(readOnly = true)
    public SysUser findByOpenId(@NotEmpty String openId) {
        SysUserQuery sysUserQuery = new SysUserQuery();
        sysUserQuery.setOpenId(openId);
        return this.findOne(sysUserQuery);
    }

    @Transactional(readOnly = true)
    public SysUser findByMobile(@NotEmpty String mobile) {
        SysUserQuery sysUserQuery = new SysUserQuery();
        sysUserQuery.setMobile(mobile);
        return this.findOne(sysUserQuery);
    }

    @Override
    public int updateSelective(@NotNull SysUser record) {
        int cnt = super.updateSelective(record);
        if (cnt != 0) {
            sysUserRoleDao.deleteByUser(record.getId());
            this.saveUserRoles(record.getRoleIds(), record.getId());
        }
        return cnt;
    }

    @Override
    public int delete(@NotEmpty Integer... userIds) {
        int cnt = super.delete(userIds);
        if (cnt != 0) {
            sysUserRoleDao.deleteByUser(userIds);
        }
        return cnt;
    }

    public int save(@NotNull SysUser record) {
        int cnt = super.save(record);
        this.saveUserRoles(record.getRoleIds(), record.getId());
        return cnt;
    }

    private void saveUserRoles(List<Integer> roleIds, Integer userId) {
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<SysUserRole> sysUserRoles =
                roleIds.stream().map(roleId -> new SysUserRole(userId, roleId)).collect(Collectors.toList());
            int count = sysUserRoleDao.insert(sysUserRoles.toArray(new SysUserRole[]{}));
            logger.info("saved {} t_user_role userId={}", count, userId);
        }
    }
}
