package com.weiwan.easyboot.mapper;

import com.weiwan.easyboot.model.entity.SysDemo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 生成案例
 * @author easyboot-generator 2021年3月16日 上午1:16:00
 */
@Repository
@Mapper
public interface SysDemoMapper extends CrudMapper<SysDemo, Integer> {

}