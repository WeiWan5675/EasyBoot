package com.weiwan.easyboot.mapper;

import java.util.List;

import com.weiwan.easyboot.model.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 字典
 *
 * @author easyboot-generator 2020年12月26日 上午1:50:03
 */
@Repository
@Mapper
public interface SysDictMapper extends CrudMapper<SysDict, Integer> {

    public List<String> selectTypes();
}