package com.weiwan.easyboot.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.weiwan.easyboot.mapper.SysLoginLogMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.weiwan.easyboot.model.entity.SysLoginLog;

/**
 * 登录日志
 *
 * @author easyboot-generator 2021年4月18日 上午1:30:18
 */
@Service
public class SysLoginLogService extends AbstractCrudService<SysLoginLogMapper, SysLoginLog, Integer> {

    @Async
    public void save(@Valid @NotNull SysLoginLog record) {
        super.save(record);
    }
}
