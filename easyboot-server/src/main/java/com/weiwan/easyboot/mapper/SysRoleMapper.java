package com.weiwan.easyboot.mapper;

import com.weiwan.easyboot.model.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 角色表
 * @author easyboot-generator 2021年1月25日 上午12:14:26
 */
@Repository
@Mapper
public interface SysRoleMapper extends CrudMapper<SysRole, Integer> {

}