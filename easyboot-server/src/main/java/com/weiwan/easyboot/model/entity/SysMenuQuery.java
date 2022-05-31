package com.weiwan.easyboot.model.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 菜单管理
 *
 * @author easyboot-generator 2021年1月31日 上午12:21:33
 */
@Data
public class SysMenuQuery extends PageQuery {

    /**
     * 上级
     */
    @ApiModelProperty(value = "上级")
    private Integer parentId;
    /**
     * 所有父级编号
     */
    @ApiModelProperty(value = "所有上级,like查询eg:/a/")
    private String parentIds;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "状态1启用，0停用")
    private Integer status;

    @ApiModelProperty(value = "地址查询")
    private String url;

}