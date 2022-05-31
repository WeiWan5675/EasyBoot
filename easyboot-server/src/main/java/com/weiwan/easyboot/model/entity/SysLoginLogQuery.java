package com.weiwan.easyboot.model.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.weiwan.easyboot.annotation.SortField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录日志
 *
 * @author easyboot-generator 2021年4月18日 上午1:30:18
 */
@Data
@SortField({"userId:t.user_id", "userName:t.user_name", "loginTime:t.login_time", "ip:t.ip"})
public class SysLoginLogQuery extends PageQuery {

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String userName;
    /**
     * 登录结果
     */
    @ApiModelProperty(value = "登录结果")
    private Integer result;
    /**
     * IP地址
     */
    @ApiModelProperty(value = "IP地址")
    private String ip;

    @NotEmpty
    @Size(min = 2, max = 2)
    @ApiModelProperty(value = "登录日期")
    private String[] loginDateRange;

}