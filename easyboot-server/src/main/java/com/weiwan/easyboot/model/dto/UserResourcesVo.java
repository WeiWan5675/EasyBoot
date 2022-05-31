package com.weiwan.easyboot.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.weiwan.easyboot.model.dto.vue.VueRouteMenu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 前端需要的用户数据结构
 */
@ApiModel(value = "Vue菜单及权限")
@Data
@AllArgsConstructor
public class UserResourcesVo {
    @ApiModelProperty(value = "角色")
    private Set<String> roles;
    @ApiModelProperty(value = "权限")
    private Set<String> permissions;
    // 树结构
    @ApiModelProperty(value = "树形菜单")
    private List<VueRouteMenu> routes = new ArrayList<>();

}
