package com.weiwan.easyboot.model.entity;

import com.weiwan.easyboot.annotation.SortField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色表
 *
 * @author easyboot-generator 2021年1月25日 上午12:14:26
 */
@Data
@SortField({"updateTime:t.update_time"})
public class SysRoleQuery extends PageQuery {

    /**
     * 角色编码
     */
    @ApiModelProperty(value = "角色编码")
    private String code;
    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String name;
    /**
     * 数据范围:0:全部，1：本部门，2：本部门及以下，3.下级部门，4.指定部门，5.本人
     */
    @ApiModelProperty(value = "数据范围:0:全部，1：本部门，2：本部门及以下，3.下级部门，4.指定部门，5.本人")
    private Integer dataScope;

    @ApiModelProperty(value = "状态1启用0停用")
    private Integer status;

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

}