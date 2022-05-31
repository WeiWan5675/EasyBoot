package com.weiwan.easyboot.model.entity;

import java.util.Date;

import com.weiwan.easyboot.annotation.SortField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

/**
 * 生成案例
 *
 * @author easyboot-generator 2021年3月16日 上午1:16:00
 */
@Data
@SortField({"inputText:t.input_text"})
public class SysDemoQuery extends PageQuery {

    /**
     * 文本
     */
    @ApiModelProperty(value = "文本")
    private String inputText;
    /**
     * 单选
     */
    @ApiModelProperty(value = "单选")
    private String inputRadio;

    @ApiModelProperty(value = "多选")
    private String inputCheckbox;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

}