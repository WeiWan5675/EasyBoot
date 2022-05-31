package com.weiwan.easyboot.service;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.weiwan.easyboot.mapper.SysDictMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weiwan.easyboot.model.entity.SysDict;
import com.weiwan.easyboot.model.entity.SysDictQuery;

/**
 * 字典
 *
 * @author easyboot-generator 2020年12月26日 上午1:50:03
 */
@Service
public class SysDictService extends AbstractCrudService<SysDictMapper, SysDict, Integer> {

    @Transactional(readOnly = true)
    public SysDict findByTypeAndCode(@NotBlank String type, @NotBlank String code) {
        SysDictQuery condition = new SysDictQuery();
        condition.setType(type);
        condition.setCode(code);
        return this.findOne(condition);
    }

    @Transactional(readOnly = true)
    public List<String> findTypes() {
        return this.d.selectTypes();
    }

    @Transactional(readOnly = true)
    public List<SysDict> findByType(String type) {
        SysDictQuery condition = new SysDictQuery();
        condition.setType(type);
        return this.find(condition);
    }
}
