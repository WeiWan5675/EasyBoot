package com.weiwan.easyboot.mapper;

import com.weiwan.easyboot.model.entity.SysParam;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author xiaozhennan
 */
@Repository
@Mapper
public interface SysParamMapper extends CrudMapper<SysParam, Integer> {}