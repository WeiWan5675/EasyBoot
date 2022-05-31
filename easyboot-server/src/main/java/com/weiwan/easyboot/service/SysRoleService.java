package com.weiwan.easyboot.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.weiwan.easyboot.mapper.SysRoleMapper;
import com.weiwan.easyboot.mapper.SysRoleMenuDao;
import com.weiwan.easyboot.mapper.SysUserRoleDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.weiwan.easyboot.model.dto.RoleAssignAo;
import com.weiwan.easyboot.model.enums.EntityStatus;
import com.weiwan.easyboot.model.entity.SysRole;
import com.weiwan.easyboot.model.entity.SysRoleQuery;
import com.weiwan.easyboot.model.entity.SysRoleMenu;
import com.weiwan.easyboot.model.entity.SysUserRole;

import lombok.RequiredArgsConstructor;

/**
 * 角色表
 *
 * @author easyboot-generator 2021年1月25日 上午12:14:26
 */
@RequiredArgsConstructor
@Service
public class SysRoleService extends AbstractCrudService<SysRoleMapper, SysRole, Integer> {

    private final SysRoleMenuDao sysRoleMenuDao;
    private final SysUserRoleDao sysUserRoleDao;

    @Override
    public SysRole find(@NotNull Integer integer) {
        SysRole sysRole = super.find(integer);
        if (null != sysRole) {
            // 放入菜单
            sysRole.setMenuIds(sysRoleMenuDao.selectMenuIdsByRoleId(sysRole.getId()));
        }
        return sysRole;
    }

    @Transactional(readOnly = true)
    public SysRole findByCode(@NotBlank String code) {
        SysRoleQuery sysRoleQuery = new SysRoleQuery();
        sysRoleQuery.setCode(code);
        return this.findOne(sysRoleQuery);
    }

    @Transactional(readOnly = true)
    public SysRole findByName(@NotBlank String name) {
        SysRoleQuery sysRoleQuery = new SysRoleQuery();
        sysRoleQuery.setName(name);
        return this.findOne(sysRoleQuery);
    }

    @Transactional(readOnly = true)
    public List<SysRole> findAll() {
        return this.find(new SysRoleQuery());
    }

    public int save(@NotNull SysRole record) {
        int count = super.save(record);
        saveRoleMenus(record.getMenuIds(), record.getId());
        return count;
    }

    @Override
    public int updateSelective(@NotNull SysRole record) {
        sysRoleMenuDao.deleteByRole(record.getId());
        this.saveRoleMenus(record.getMenuIds(), record.getId());
        return super.updateSelective(record);
    }

    private void saveRoleMenus(List<Integer> menuIds, Integer roleId) {
        if (!CollectionUtils.isEmpty(menuIds)) {
            List<SysRoleMenu> sysRoleMenus =
                menuIds.stream().map(menuId -> new SysRoleMenu(roleId, menuId)).collect(Collectors.toList());
            int saveRoleMenuCount = sysRoleMenuDao.insert(sysRoleMenus.toArray(new SysRoleMenu[]{}));
            logger.debug("saved {} sys_role_menu roleId={}", roleId, saveRoleMenuCount);
        }
    }

    @Override
    public int delete(@NotNull Integer... roleIds) {
        sysRoleMenuDao.deleteByRole(roleIds);
        sysUserRoleDao.deleteByRole(roleIds);
        return super.delete(roleIds);
    }

    public int assign(@Valid @NotNull RoleAssignAo roleAssignAo) {
        if (roleAssignAo.getAddUser()) {
            List<SysUserRole> sysUserRoles = Arrays.stream(roleAssignAo.getUserIds())
                .map(userId -> new SysUserRole(userId, roleAssignAo.getRoleId())).collect(Collectors.toList());
            sysUserRoleDao.insert(sysUserRoles.toArray(new SysUserRole[]{}));
        } else {
            sysUserRoleDao.deleteByRoleAndUser(roleAssignAo.getRoleId(), roleAssignAo.getUserIds());
        }
        return 0;
    }

    /**
     * 查询用户有效的角色
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<SysRole> findByUserId(@NotNull Integer userId) {
        SysRoleQuery sysRoleQuery = new SysRoleQuery();
        sysRoleQuery.setStatus(EntityStatus.NORMAL.status());
        sysRoleQuery.setUserId(userId);
        return this.find(sysRoleQuery);
    }
}
