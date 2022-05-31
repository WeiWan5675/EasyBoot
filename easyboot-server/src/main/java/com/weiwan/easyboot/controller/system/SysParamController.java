package com.weiwan.easyboot.controller.system;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.weiwan.easyboot.controller.BaseController;
import com.weiwan.easyboot.model.PageWrapper;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.weiwan.easyboot.service.SysParamService;
import com.weiwan.easyboot.model.entity.SysParam;
import com.weiwan.easyboot.model.entity.SysParamQuery;
import com.weiwan.easyboot.common.DefaultCodeMsgBundle;
import com.weiwan.easyboot.common.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @author hdf
 */
@Api(tags = "系统参数")
@RestController
@RequestMapping("/sys/param")
@RequiredArgsConstructor
public class SysParamController extends BaseController {

    private final SysParamService sysParamService;

    @ApiOperation(value = "主键查询")
    @PreAuthorize("hasAuthority('sys:param:query')")
    @GetMapping("/query/{id}")
    public Result<SysParam> query(@PathVariable Integer id) {
        SysParam sysParam = sysParamService.find(id);
        Assert.notNull(sysParam, id + " not exists");
        sysParam.setParamValue(StringEscapeUtils.unescapeHtml4(sysParam.getParamValue()));
        return Result.ok(sysParam);
    }

    @ApiOperation(value = "分页查询")
    @PreAuthorize("hasAuthority('sys:param:query')")
    @PostMapping("/query")
    public Result<PageWrapper<SysParam>> query(@Valid @RequestBody SysParamQuery condition) {
        PageWrapper<SysParam> pageWrapper =
            sysParamService.find(condition, condition.getPage(), condition.getPageSize());
        return Result.ok(pageWrapper);
    }

    @ApiOperation(value = "保存")
    @PreAuthorize("hasAuthority('sys:param:save')")
    @PostMapping(value = "/save")
    public Result save(@Valid @RequestBody SysParam sysParam) {
        int count = sysParamService.save(sysParam);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.SAVE_ERROR, count);
    }

    @ApiOperation(value = "更新")
    @PreAuthorize("hasAuthority('sys:param:update')")
    @PostMapping(value = "/update")
    public Result update(@Valid @RequestBody SysParam sysParam) {
        int count = sysParamService.updateSelective(sysParam);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.UPDATE_ERROR, count);
    }

    @ApiOperation(value = "删除")
    @PreAuthorize("hasAuthority('sys:param:delete')")
    @PostMapping(value = "/delete")
    public Result delete(@RequestParam Integer id) {
        int count = sysParamService.delete(id);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.DELETE_ERROR, count);
    }

    @ApiOperation(value = "检查是否重复")
    @PreAuthorize("hasAuthority('sys:param:query')")
    @PostMapping(value = "/check_param_key")
    public Result<Boolean> checkParamKey(@RequestParam(required = false) Integer id,
        @NotBlank @RequestParam String paramKey) {
        SysParam sysParam = this.sysParamService.findByParamKey(paramKey);
        return Result.ok(null == sysParam || Objects.equals(sysParam.getId(), id));
    }
}
