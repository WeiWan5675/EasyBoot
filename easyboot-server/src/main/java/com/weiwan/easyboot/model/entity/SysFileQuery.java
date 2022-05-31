package com.weiwan.easyboot.model.entity;

import java.util.Collection;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.weiwan.easyboot.annotation.SortField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件
 *
 * @author easyboot-generator 2021年1月2日 上午1:19:53
 */
@Data
@SortField({"createTime:t.create_time"})
public class SysFileQuery extends PageQuery {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称(模糊匹配)")
    private String name;
    /**
     * 相对路径
     */
    @ApiModelProperty(value = "相对路径")
    private String relativePath;

    /**
     * 如果使用Date数组接收，需要添加{@code @JsonFormat(pattern = "yyyy-MM-dd")}
     */
    @NotEmpty
    @Size(min = 2, max = 2)
    @ApiModelProperty(value = "上传日期")
    private String[] createDateRange;

    @ApiModelProperty(value = "id查询")
    @Size(max = 20)
    private Collection<String> ids;

}