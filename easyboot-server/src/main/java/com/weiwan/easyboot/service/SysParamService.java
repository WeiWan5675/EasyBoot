package com.weiwan.easyboot.service;

import javax.validation.constraints.NotBlank;

import com.weiwan.easyboot.mapper.SysParamMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weiwan.easyboot.model.entity.SysParam;
import com.weiwan.easyboot.model.entity.SysParamQuery;

/**
 * @author xiaozhennan
 */
@Service
public class SysParamService extends AbstractCrudService<SysParamMapper, SysParam, Integer> {

    @Cacheable(cacheNames = "SysParam", key = "#paramKey")
    @Transactional(readOnly = true)
    public SysParam findCacheByParamKey(@NotBlank String paramKey) {
        return this.findByParamKey(paramKey);
    }

    @Transactional(readOnly = true)
    public SysParam findByParamKey(@NotBlank String paramKey) {
        SysParamQuery sysParamQuery = new SysParamQuery();
        sysParamQuery.setParamKey(paramKey);
        return this.findOne(sysParamQuery);
    }

}
