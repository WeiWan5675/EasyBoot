package com.weiwan.easyboot.controller.system;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.weiwan.easyboot.component.file.FileService;
import com.weiwan.easyboot.controller.BaseController;
import com.weiwan.easyboot.model.PageWrapper;
import com.weiwan.easyboot.model.entity.SysFile;
import com.weiwan.easyboot.model.entity.SysFileQuery;
import com.weiwan.easyboot.service.SysFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.weiwan.easyboot.model.DefaultCodeMsgBundle;
import com.weiwan.easyboot.model.Result;
import com.weiwan.easyboot.component.file.FileInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

/**
 * 文件
 *
 * @author easyboot-generator 2021年1月2日 上午1:04:41
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/sys/file")
@RequiredArgsConstructor
public class SysFileController extends BaseController {

    private final SysFileService sysFileService;
    private final FileService fileService;

    @ApiOperation(value = "主键查询")
    @PreAuthorize("hasAuthority('sys:file:query')")
    @GetMapping("/query/{id}")
    public Result<SysFile> query(@PathVariable String id) {
        SysFile sysFile = sysFileService.find(id);
        return Result.ok(sysFile);
    }

    @ApiOperation(value = "分页查询")
    @PreAuthorize("hasAuthority('sys:file:query')")
    @PostMapping("/query")
    public Result<PageWrapper<SysFile>> query(@Valid @RequestBody SysFileQuery query) {
        PageWrapper<SysFile> pageWrapper =
            sysFileService.find(query, query.getPage(), query.getPageSize());
        pageWrapper.getData().forEach((v) -> {
            v.setUrl(this.fileService.getUrl(v.getRelativePath()));
        });
        return Result.ok(pageWrapper);
    }

    /**
     * 通常图片展不需要文件名，可以省DB交互
     *
     * @param relativePaths
     * @param includeFileName
     * @return
     */
    @ApiOperation(value = "查询文件信息")
    @PostMapping("/info")
    public Result<List<FileInfo>> info(@NotBlank @ApiParam("相对路径,逗号分隔") @RequestParam String relativePaths,
        @ApiParam("是否包含文件名") @RequestParam(required = false) boolean includeFileName) {
        List<FileInfo> fileInfos = this.sysFileService.info(relativePaths, includeFileName);
        return Result.ok(fileInfos);
    }

    @ApiOperation(value = "单个上传")
    @PreAuthorize("hasAuthority('sys:file:upload')")
    @PostMapping(value = "/upload")
    public Result<FileInfo> upload(@NotNull @RequestParam MultipartFile file) throws IOException {
        // 开启图片压缩

        FileInfo fileInfo = sysFileService.upload(file.getOriginalFilename(), file.getContentType(), file.getSize(),
            file.getInputStream());
        return Result.ok(fileInfo);
    }

    @ApiOperation(value = "批量上传")
    @PreAuthorize("hasAuthority('sys:file:upload')")
    @PostMapping(value = "/upload_batch")
    public Result<List<FileInfo>> upload(@NotEmpty @RequestParam MultipartFile[] files) {
        List<FileInfo> fileInfos = Lists.newArrayList();
        Arrays.stream(files).forEach((file) -> {
            try {
                FileInfo fileInfo = sysFileService.upload(file.getOriginalFilename(), file.getContentType(),
                    file.getSize(), file.getInputStream());
                fileInfos.add(fileInfo);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return Result.ok(fileInfos);
    }

    @ApiOperation(value = "下载")
    @PreAuthorize("hasAuthority('sys:file:download')")
    @GetMapping(value = "/download")
    public void download(@NotBlank @RequestParam String id, HttpServletResponse response) throws IOException {
        SysFile sysFile = sysFileService.find(id);
        Assert.notNull(sysFile, "file record not exists");
        try (InputStream inputStream = fileService.download(sysFile.getRelativePath());
            OutputStream outputStream = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            BufferedInputStream bin = new BufferedInputStream(inputStream)) {

            // 设置响应类型 available 在流式下载不准确，设置后下载数据不全
            // response.setContentLength(inputStream.available());
            response.setContentType(sysFile.getContentType());
            response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(sysFile.getName(), StandardCharsets.UTF_8.name()));
            // 1M 一写
            byte[] buffer = new byte[1024 * 1024];
            int len = 0;
            while (-1 != (len = bin.read(buffer))) {
                bos.write(buffer, 0, len);
            }
        }

    }

    @ApiOperation(value = "删除")
    @PreAuthorize("hasAuthority('sys:file:delete')")
    @PostMapping(value = "/delete")
    public Result delete(@RequestParam String id) {
        SysFile sysFile = sysFileService.find(id);
        Assert.notNull(sysFile, "file record not exists");
        try {
            fileService.delete(sysFile.getRelativePath());
        } catch (IOException e) {
            logger.error("remove file:" + sysFile.getRelativePath() + " error", e);
        }
        int count = sysFileService.delete(id);
        return count == 1 ? Result.SUCCESS : Result.error(DefaultCodeMsgBundle.DELETE_ERROR, count);
    }

    @ApiOperation(value = "批量删除")
    @PreAuthorize("hasAuthority('sys:file:delete')")
    @PostMapping(value = "/delete_batch")
    public Result delete(@RequestParam String[] ids) {
        int successCount = 0;
        List<String> errorIds = new ArrayList<>(ids.length);
        for (String id: ids) {
            SysFile sysFile = sysFileService.find(id);
            Assert.notNull(sysFile, "file record not exists");
            try {
                fileService.delete(sysFile.getRelativePath());
            } catch (IOException e) {
                logger.error("remove file:" + sysFile.getRelativePath() + " error", e);
            }
            int count = sysFileService.delete(id);
            if (1 == count) { // 删除成功
                successCount ++;
            } else { // 删除失败
                errorIds.add(id + "：" + sysFile.getName());
            }
        }
        return successCount == ids.length
                ? Result.SUCCESS
                : Result.error(DefaultCodeMsgBundle.DELETE_ERROR,
                        "删除失败文件列表：\n" + StringUtils.join(errorIds, ",\n"));
    }

    @ApiOperation(value = "文件url前缀")
    @GetMapping(value = "/url_prefix")
    public Result<String> urlPrefix() {
        return Result.ok(fileService.getUrlPrefix());
    }
}
