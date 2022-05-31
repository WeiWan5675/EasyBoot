package com.weiwan.easyboot.mapper;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.weiwan.easyboot.model.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 菜单管理
 * 
 * @author easyboot-generator 2021年1月31日 上午12:21:33
 */
@Repository
@Mapper
public interface SysMenuMapper extends CrudMapper<SysMenu, Integer> {
    List<SysMenu> selectByUserId(@NotNull Integer userId);
}