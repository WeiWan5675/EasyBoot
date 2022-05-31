package com.weiwan.easyboot.mapper;

import com.weiwan.easyboot.model.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 文件
 *
 * @author easyboot-generator 2021年1月2日 上午1:04:41
 */
@Repository
@Mapper
public interface SysFileMapper extends CrudMapper<SysFile, String> {

}