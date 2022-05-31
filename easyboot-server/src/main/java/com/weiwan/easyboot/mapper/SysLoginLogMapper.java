package com.weiwan.easyboot.mapper;

import com.weiwan.easyboot.model.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 登录日志
 * @author easyboot-generator 2021年4月18日 上午1:30:18
 */
@Repository
@Mapper
public interface SysLoginLogMapper extends CrudMapper<SysLoginLog, Integer> {

}