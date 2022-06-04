package com.weiwan.easyboot.controller.system;

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.weiwan.easyboot.model.PageWrapper;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.weiwan.easyboot.service.SysDemoService;
import com.weiwan.easyboot.model.entity.SysDemo;
import com.weiwan.easyboot.model.entity.SysDemoQuery;
import com.weiwan.easyboot.model.DefaultCodeMsgBundle;
import com.weiwan.easyboot.model.Result;
import com.weiwan.easyboot.controller.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * 生成案例
 *
 * @author easyboot-generator 2021年3月16日 上午1:16:00
 */
@Api(tags = "生成案例")
@RestController
@RequestMapping("/sys/demo")
@RequiredArgsConstructor
public class SysDemoController extends BaseController {

    private final SysDemoService sysDemoService;

    @ApiOperation(value = "主键查询")
    @PreAuthorize("hasAuthority('sys:demo:query')")
    @GetMapping("/query/{id}")
    public Result<SysDemo> query(@PathVariable Integer id) {
        SysDemo sysDemo = sysDemoService.find(id);
        Assert.notNull(sysDemo, "记录不存在");
        sysDemo.setRichText(StringEscapeUtils.unescapeHtml4(sysDemo.getRichText()));
        return Result.ok(sysDemo);
    }

    @ApiOperation(value = "分页查询")
    @PreAuthorize("hasAuthority('sys:demo:query')")
    @PostMapping("/query")
    public Result<PageWrapper<SysDemo>> query(@Valid @RequestBody SysDemoQuery condition) {
        PageWrapper<SysDemo> pageWrapper =
            sysDemoService.find(condition, condition.getPage(), condition.getPageSize());
        return Result.ok(pageWrapper);
    }

    @ApiOperation(value = "保存")
    @PreAuthorize("hasAuthority('sys:demo:save')")
    @PostMapping(value = "/save")
    public Result save(@Valid @RequestBody SysDemo sysDemo) {
        int count = sysDemoService.save(sysDemo);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.SAVE_ERROR, count);
    }

    @ApiOperation(value = "更新")
    @PreAuthorize("hasAuthority('sys:demo:update')")
    @PostMapping(value = "/update")
    public Result update(@Valid @RequestBody SysDemo sysDemo) {
        int count = sysDemoService.updateSelective(sysDemo);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.UPDATE_ERROR, count);
    }

    @ApiOperation(value = "删除")
    @PreAuthorize("hasAuthority('sys:demo:delete')")
    @PostMapping(value = "/delete")
    public Result delete(@RequestParam Integer id) {
        int count = sysDemoService.delete(id);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.DELETE_ERROR, count);
    }

    @ApiOperation(value = "检查是否重复")
    @PreAuthorize("hasAuthority('sys:demo:query')")
    @PostMapping(value = "/check_input_text")
    public Result<Boolean> checkInputText(@RequestParam(required = false) Integer id,
        @NotBlank @RequestParam String inputText) {
        SysDemo sysDemo = this.sysDemoService.findByInputText(inputText);
        return Result.ok(null == sysDemo || Objects.equals(sysDemo.getId(), id));
    }
}
