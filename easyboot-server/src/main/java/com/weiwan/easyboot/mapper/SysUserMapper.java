package com.weiwan.easyboot.mapper;

import com.weiwan.easyboot.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户信息
 * @author easyboot-generator 2021年1月16日 下午11:55:54
 */
@Repository
@Mapper
public interface SysUserMapper extends CrudMapper<SysUser, Integer> {

}