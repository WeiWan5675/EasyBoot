package com.weiwan.easyboot.model.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典
 *
 * @author easyboot-generator 2020年12月26日 上午1:50:03
 */
@ApiModel
@Data
public class SysDict extends BaseEntity<Integer> {

    @ApiModelProperty(value = "字典类型", required = true)
    @NotBlank
    @Size(max = 50)
    private String type;

    @ApiModelProperty(value = "编码", required = true)
    @NotBlank
    @Size(max = 50)
    private String code;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank
    @Size(max = 50)
    private String name;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull
    private Integer sort;


}