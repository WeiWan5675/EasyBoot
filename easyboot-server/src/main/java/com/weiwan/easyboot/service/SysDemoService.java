package com.weiwan.easyboot.service;

import javax.validation.constraints.NotBlank;

import com.weiwan.easyboot.mapper.SysDemoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weiwan.easyboot.model.entity.SysDemo;
import com.weiwan.easyboot.model.entity.SysDemoQuery;

/**
 * 生成案例
 *
 * @author easyboot-generator 2021年3月16日 上午1:16:00
 */
@Service
public class SysDemoService extends AbstractCrudService<SysDemoMapper, SysDemo, Integer> {

    @Transactional(readOnly = true)
    public SysDemo findByInputText(@NotBlank String inputText) {
        SysDemoQuery sysDemoQuery = new SysDemoQuery();
        sysDemoQuery.setInputText(inputText);
        return this.findOne(sysDemoQuery);
    }
}
