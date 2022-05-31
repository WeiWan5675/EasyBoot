package com.weiwan.easyboot.model.entity;

import com.weiwan.easyboot.annotation.SortField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 代码生成
 * @author easyboot-generator 2021年3月29日 下午11:27:05
 */
@Data
@SortField({"tableName:t.table_name"})
public class SysGenQuery extends PageQuery {

    /**
     * 表名
     */
    @ApiModelProperty(value = "表名")
    private String tableName;

}