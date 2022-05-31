package com.weiwan.easyboot.mapper;

import com.weiwan.easyboot.model.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 组织机构
 * @author easyboot-generator 2021年1月12日 下午10:54:44
 */
@Repository
@Mapper
public interface SysDeptMapper extends CrudMapper<SysDept, Integer> {

}