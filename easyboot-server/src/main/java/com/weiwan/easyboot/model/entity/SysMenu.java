package com.weiwan.easyboot.model.entity;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 菜单管理
 *
 * @author easyboot-generator 2021年1月31日 上午12:21:33
 */
@ApiModel
@Data
public class SysMenu extends BaseEntity<Integer> {

    public static final int MENU_TYPE_DIRECTORY = 1;
    public static final int MENU_TYPE_MENU = 2;
    public static final int MENU_TYPE_BUTTON = 3;

    public static final String TARGET_MAIN = "main";
    public static final String DEFAULT_HOME = "/db/mysql/mapper/home";

    @ApiModelProperty(value = "上级", required = true)
    @NotNull
    private Integer parentId = 0;

    @ApiModelProperty(value = "所有父节点，自动计算")
    // @NotBlank
    @Size(max = 255)
    private String parentIds;

    @ApiModelProperty(value = "名称", required = true)
    @NotBlank
    @Size(max = 50)
    private String name;

    @ApiModelProperty(value = "排序", required = true)
    @NotNull
    private Integer sort;

    @ApiModelProperty(value = "地址")
    @Size(max = 255)
    private String url;

    @ApiModelProperty(value = "目标地址main,_blank")
    @Size(max = 20)
    private String target;

    @ApiModelProperty(value = "1:目录2:菜单3:按钮", required = true)
    @NotNull
    private Integer type;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "权限标识")
    private String permission;

    // 扩展字段
    private List<SysMenu> children;

}