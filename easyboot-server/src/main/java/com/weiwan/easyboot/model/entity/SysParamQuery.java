package com.weiwan.easyboot.model.entity;

import com.weiwan.easyboot.annotation.SortField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hdf
 */
@Data
@SortField({"updateTime:t.update_time"})
public class SysParamQuery extends PageQuery {

    /**
     * 名字
     */
    @ApiModelProperty(value = "参数名称")
    private String name;
    /**
     * 唯一键
     */
    @ApiModelProperty(value = "唯一键")
    private String paramKey;

    @ApiModelProperty(value = "状态")
    private Integer status;

}