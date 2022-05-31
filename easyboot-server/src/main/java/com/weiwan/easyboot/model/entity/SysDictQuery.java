package com.weiwan.easyboot.model.entity;

import com.weiwan.easyboot.annotation.SortField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典
 *
 * @author easyboot-generator 2020年12月26日 下午3:45:28
 */
@Data
@SortField({"updateTime:t.update_time"})
public class SysDictQuery extends PageQuery {

    /**
     * 字典类型
     */
    @ApiModelProperty(value = "字典类型")
    private String type;

    @ApiModelProperty(value = "编码")
    private String code;
}