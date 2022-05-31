package com.weiwan.easyboot.controller.system;

import javax.validation.Valid;

import com.weiwan.easyboot.common.DefaultCodeMsgBundle;
import com.weiwan.easyboot.common.Result;
import com.weiwan.easyboot.controller.BaseController;
import com.weiwan.easyboot.model.PageWrapper;
import com.weiwan.easyboot.service.SysLoginLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import com.weiwan.easyboot.model.entity.SysLoginLog;
import com.weiwan.easyboot.model.entity.SysLoginLogQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * 登录日志
 *
 * @author easyboot-generator 2021年4月18日 上午1:30:18
 */
@Api(tags = "登录日志")
@RestController
@RequestMapping("/sys/login_log")
@RequiredArgsConstructor
public class SysLoginLogController extends BaseController {

    private final SysLoginLogService sysLoginLogService;

    @ApiOperation(value = "主键查询")
    @PreAuthorize("hasAuthority('sys:login_log:query')")
    @GetMapping("/query/{id}")
    public Result<SysLoginLog> query(@PathVariable Integer id) {
        SysLoginLog sysLoginLog = sysLoginLogService.find(id);
        return Result.ok(sysLoginLog);
    }

    @ApiOperation(value = "分页查询")
    @PreAuthorize("hasAuthority('sys:login_log:query')")
    @PostMapping("/query")
    public Result<PageWrapper<SysLoginLog>> query(@Valid @RequestBody SysLoginLogQuery condition) {
        PageWrapper<SysLoginLog> pageWrapper =
            sysLoginLogService.find(condition, condition.getPage(), condition.getPageSize());
        return Result.ok(pageWrapper);
    }

    @ApiOperation(value = "删除")
    @PreAuthorize("hasAuthority('sys:login_log:delete')")
    @PostMapping(value = "/delete")
    public Result delete(@RequestParam Integer id) {
        int count = sysLoginLogService.delete(id);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.DELETE_ERROR, count);
    }
}
