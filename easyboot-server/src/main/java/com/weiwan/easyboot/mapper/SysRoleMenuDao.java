package com.weiwan.easyboot.mapper;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwan.easyboot.model.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 角色-菜单
 *
 * @author easyboot-generator 2021年2月7日 上午1:11:14
 */
@Repository
@Mapper
public interface SysRoleMenuDao extends BaseMapper<SysRoleMenu> {

    List<Integer> selectMenuIdsByRoleId(@NotNull Integer roleId);

    List<Integer> selectRoleIdsByMenuId(@NotNull Integer menuId);

    int deleteByRole(@NotEmpty Integer... roleIds);

    int deleteByMenu(@NotNull Integer... menuIds);

    int insert(@Valid @NotEmpty SysRoleMenu... sysRoleMenus);
}