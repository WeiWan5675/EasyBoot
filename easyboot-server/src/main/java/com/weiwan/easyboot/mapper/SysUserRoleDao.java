package com.weiwan.easyboot.mapper;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwan.easyboot.model.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户-角色
 *
 * @author easyboot-generator 2021年1月26日 下午11:14:43
 */
@Repository
@Mapper
public interface SysUserRoleDao extends BaseMapper<SysUserRole> {

    List<Integer> selectRoleIdsByUserId(@NotNull Integer userId);

    List<Integer> selectUserIdsByRoleId(@NotNull Integer roleId);

    int deleteByUser(@NotEmpty Integer... userIds);

    int deleteByRoleAndUser(@NotNull @Param("roleId") Integer roleId, @NotEmpty @Param("userIds") Integer... userIds);

    int deleteByRole(@NotNull Integer... roleIds);

    int insert(@Valid @NotEmpty SysUserRole... sysUserRoles);
}