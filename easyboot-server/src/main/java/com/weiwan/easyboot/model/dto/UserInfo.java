package com.weiwan.easyboot.model.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author xiaozhennan
 */
@ApiModel(value = "用户基本信息")
@Data
public class UserInfo implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "部门ID")
    private Integer deptId;
    @ApiModelProperty(value = "登录账号")
    private String username;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "部门名称")
    private String deptName;
    @ApiModelProperty(value = "头像存储地址")
    private String photo;
    @ApiModelProperty(value = "头像")
    private String photoUrl;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @JsonIgnore
    private String dsf;

    public UserInfo(Integer id, Integer deptId, String username, String name) {
        this.userId = id;
        this.deptId = deptId;
        this.username = username;
        this.name = name;
    }

    public UserInfo() {
    }

    public UserInfo(Integer userId, Integer deptId, String username, String name, String deptName, String photo, String photoUrl, String mobile, String email, String dsf) {
        this.userId = userId;
        this.deptId = deptId;
        this.username = username;
        this.name = name;
        this.deptName = deptName;
        this.photo = photo;
        this.photoUrl = photoUrl;
        this.mobile = mobile;
        this.email = email;
        this.dsf = dsf;
    }
}
