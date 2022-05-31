package com.weiwan.easyboot.model.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 组织机构
 *
 * @author easyboot-generator 2021年1月12日 下午10:54:44
 */
@Data
public class SysDeptQuery extends PageQuery {

    /**
     * 父部门
     */
    @ApiModelProperty(value = "父部门")
    private Integer parentId;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String name;

    /**
     * like 查询 /a/
     */
    @ApiModelProperty(value = "父部门,like查询eg:/a/")
    private String parentIds;
}