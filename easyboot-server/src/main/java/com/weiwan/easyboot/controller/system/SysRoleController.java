package com.weiwan.easyboot.controller.system;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.weiwan.easyboot.model.PageWrapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.weiwan.easyboot.model.dto.RoleAssignAo;
import com.weiwan.easyboot.service.SysRoleService;
import com.weiwan.easyboot.model.entity.SysRole;
import com.weiwan.easyboot.model.entity.SysRoleQuery;
import com.weiwan.easyboot.common.DefaultCodeMsgBundle;
import com.weiwan.easyboot.common.Result;
import com.weiwan.easyboot.controller.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * 角色管理
 *
 * @author easyboot-generator 2021年1月25日 上午12:14:26
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class SysRoleController extends BaseController {

    private final SysRoleService sysRoleService;

    @ApiOperation(value = "主键查询")
    @PreAuthorize("hasAuthority('sys:role:query')")
    @GetMapping("/query/{id}")
    public Result<SysRole> query(@PathVariable Integer id) {
        SysRole sysRole = sysRoleService.find(id);
        return Result.ok(sysRole);
    }

    @ApiOperation(value = "分页查询")
    @PreAuthorize("hasAuthority('sys:role:query')")
    @PostMapping("/query")
    public Result<PageWrapper<SysRole>> query(@Valid @RequestBody SysRoleQuery condition) {
        PageWrapper<SysRole> pageWrapper=
            sysRoleService.find(condition, condition.getPage(), condition.getPageSize());
        return Result.ok(pageWrapper);
    }

    @ApiOperation(value = "查询全部角色")
    @GetMapping("/query")
    public Result<List<SysRole>> query() {
        return Result.ok(sysRoleService.findAll());
    }

    @ApiOperation(value = "保存")
    @PreAuthorize("hasAuthority('sys:role:save')")
    @PostMapping(value = "/save")
    public Result save(@Valid @RequestBody SysRole sysRole) {
        int count = sysRoleService.save(sysRole);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.SAVE_ERROR, count);
    }

    @ApiOperation(value = "更新")
    @PreAuthorize("hasAuthority('sys:role:update')")
    @PostMapping(value = "/update")
    public Result update(@Valid @RequestBody SysRole sysRole) {
        int count = sysRoleService.updateSelective(sysRole);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.UPDATE_ERROR, count);
    }

    @ApiOperation(value = "删除")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @PostMapping(value = "/delete")
    public Result delete(@RequestParam Integer id) {
        int count = sysRoleService.delete(id);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.DELETE_ERROR, count);
    }

    @ApiOperation(value = "检查编码是否重复")
    @PreAuthorize("hasAuthority('sys:role:query')")
    @PostMapping(value = "/checkCode")
    public Result<Boolean> checkCode(@RequestParam(required = false) Integer id, @NotBlank @RequestParam String code) {
        SysRole sysRole = this.sysRoleService.findByCode(code);
        return Result.ok(null == sysRole || Objects.equals(sysRole.getId(), id));
    }

    @ApiOperation(value = "检查名称是否重复")
    @PreAuthorize("hasAuthority('sys:role:query')")
    @PostMapping(value = "/check_name")
    public Result<Boolean> checkName(@RequestParam(required = false) Integer id, @NotBlank @RequestParam String name) {
        SysRole sysRole = this.sysRoleService.findByName(name);
        return Result.ok(null == sysRole || Objects.equals(sysRole.getId(), id));
    }

    @ApiOperation(value = "角色分配")
    @PreAuthorize("hasAuthority('sys:role:assign')")
    @PostMapping(value = "/assign")
    public Result assign(@Valid @RequestBody RoleAssignAo roleAssignAo) {
        sysRoleService.assign(roleAssignAo);
        return Result.SUCCESS;
    }
}
