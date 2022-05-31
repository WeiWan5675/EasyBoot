package com.weiwan.easyboot.model.entity;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色
 *
 * @author easyboot-generator 2021年1月25日 上午12:14:26
 */
@ApiModel(value = "角色")
@Data
public class SysRole extends BaseEntity<Integer> {

    @ApiModelProperty(value = "角色编码", required = true)
    @NotBlank
    @Size(max = 50)
    private String code;

    @ApiModelProperty(value = "角色名称", required = true)
    @NotBlank
    @Size(max = 50)
    private String name;

    @ApiModelProperty(value = "数据范围:0:全部，10：本部门，20：本部门及以下，30.本人", required = true)
    @NotNull
    private Integer dataScope;

    @ApiModelProperty(value = "角色菜单")
    private List<Integer> menuIds;

}