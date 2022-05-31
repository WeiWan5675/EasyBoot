package com.weiwan.easyboot.model.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件
 *
 * @author easyboot-generator 2021年1月2日 上午1:04:41
 */
@ApiModel
@Data
public class SysFile extends BaseEntity<String> {

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank
    @Size(max = 200)
    private String name;

    @ApiModelProperty(value = "文件类型", required = true)
    @NotBlank
    @Size(max = 100)
    private String contentType;

    @ApiModelProperty(value = "文件大小B", required = true)
    @NotNull
    private Integer fileSize;

    @ApiModelProperty(value = "相对路径", required = true)
    @NotBlank
    @Size(max = 200)
    private String relativePath;

    // 以下为自定义字段
    @ApiModelProperty(value = "访问路径")
    private String url;
}