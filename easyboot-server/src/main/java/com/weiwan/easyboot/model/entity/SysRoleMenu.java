package com.weiwan.easyboot.model.entity;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 角色-菜单
 *
 */
@AllArgsConstructor
@Data
public class SysRoleMenu {

    /**
     * 角色
     */
    @NotNull
    private Integer roleId;

    /**
     * 菜单
     */
    @NotNull
    private Integer menuId;

}