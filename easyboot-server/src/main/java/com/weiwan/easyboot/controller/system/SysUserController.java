package com.weiwan.easyboot.controller.system;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.weiwan.easyboot.component.file.FileService;
import com.weiwan.easyboot.model.PageWrapper;
import com.weiwan.easyboot.security.PasswordUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.weiwan.easyboot.security.lock.LoginLockService;
import com.weiwan.easyboot.security.SecurityUtils;
import com.weiwan.easyboot.model.enums.LockType;
import com.weiwan.easyboot.service.SysUserService;
import com.weiwan.easyboot.model.entity.SysUser;
import com.weiwan.easyboot.model.entity.SysUserQuery;
import com.weiwan.easyboot.model.DefaultCodeMsgBundle;
import com.weiwan.easyboot.model.Result;
import com.weiwan.easyboot.controller.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

/**
 * 用户信息
 *
 * @author easyboot-generator 2021年1月16日 下午11:55:54
 */
@Api(tags = "用户信息")
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {

    private final SysUserService sysUserService;
    private final FileService fileService;
    private final LoginLockService loginLockService;

    @ApiOperation(value = "主键查询")
    @PreAuthorize("hasAuthority('sys:user:query')")
    @GetMapping("/query/{id}")
    public Result<SysUser> query(@PathVariable Integer id) {
        SysUser sysUser = sysUserService.find(id);
        sysUser.setPhotoUrl(fileService.getUrl(sysUser.getPhoto()));
        return Result.ok(sysUser);
    }

    @ApiOperation(value = "分页查询")
    @PreAuthorize("hasAuthority('sys:user:query')")
    @PostMapping("/query")
    public Result<PageWrapper<SysUser>> query(@Valid @RequestBody SysUserQuery condition) {
        PageWrapper<SysUser> pageWrapper =
            sysUserService.find(condition, condition.getPage(), condition.getPageSize());
        pageWrapper.getData().forEach(sysUser -> {
            sysUser.setPhotoUrl(fileService.getUrl(sysUser.getPhoto()));
        });
        return Result.ok(pageWrapper);
    }

    @ApiOperation(value = "保存")
    @PreAuthorize("hasAuthority('sys:user:save')")
    @PostMapping(value = "/save")
    public Result save(@Valid @RequestBody SysUser sysUser) {
        sysUser.setPassword(PasswordUtil.encode(sysUser.getPassword()));
        int count = sysUserService.save(sysUser);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.SAVE_ERROR, count);
    }

    @ApiOperation(value = "更新")
    @PreAuthorize("hasAuthority('sys:user:update')")
    @PostMapping(value = "/update")
    public Result update(@Valid @RequestBody SysUser sysUser) {
        if (SecurityUtils.isSuperAdmin(sysUser.getId()) && !SecurityUtils.isSuperAdmin()) {
            return Result.error("只有系统管理员才能修改");
        }
        sysUser.setPassword(PasswordUtil.encode(sysUser.getPassword()));
        int count = sysUserService.updateSelective(sysUser);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.UPDATE_ERROR, count);
    }

    @ApiOperation(value = "删除")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @PostMapping(value = "/delete")
    public Result delete(@RequestParam Integer id) {
        if (SecurityUtils.isSuperAdmin(id)) {
            return Result.error("系统管理员不能删除");
        }
        int count = sysUserService.delete(id);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.DELETE_ERROR, count);
    }

    @ApiOperation(value = "检查登录名是否重复")
    @PreAuthorize("hasAuthority('sys:user:query')")
    @PostMapping(value = "/check_user_name")
    public Result<Boolean> checkUsername(@RequestParam(required = false) Integer id,
        @NotBlank @RequestParam String username) {
        SysUser sysUser = this.sysUserService.findByUsername(username);
        return Result.ok(null == sysUser || Objects.equals(sysUser.getId(), id));
    }

    @ApiOperation(value = "检查手机号是否重复")
    @PreAuthorize("hasAuthority('sys:user:query')")
    @PostMapping(value = "/check_mobile")
    public Result<Boolean> checkMobile(@RequestParam(required = false) Integer id,
        @NotBlank @RequestParam String mobile) {
        SysUser sysUser = this.sysUserService.findByMobile(mobile);
        return Result.ok(null == sysUser || Objects.equals(sysUser.getId(), id));
    }

    @ApiOperation(value = "解锁")
    @PreAuthorize("hasAuthority('sys:user:unlock')")
    @PostMapping(value = "/unlock")
    public Result unlock(@ApiParam("0:ip,1:用户名") @RequestParam @NotNull Integer type,
        @NotBlank @RequestParam String lockKey) {
        if (Objects.equals(type, LockType.IP.ordinal())) {
            loginLockService.clear(null, lockKey);
        } else if (Objects.equals(type, LockType.USERNAME.ordinal())) {
            loginLockService.clear(lockKey, null);
        }
        return Result.ok();
    }
}
