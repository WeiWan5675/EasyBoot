package com.weiwan.easyboot.mapper;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weiwan.easyboot.model.entity.AbstractQuery;
import com.weiwan.easyboot.model.entity.BaseEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 包含基本CRUD定义，DAO 完成基础字段验证
 *
 * @author hdf
 * @param <T>
 *            DB 实体
 * @param <PK>
 *            主键 快速开发所以只支持简单主键，即数值和字符
 */
public interface CrudMapper<T extends BaseEntity<PK>, PK> extends BaseMapper<T> {

    /**
     * 通用删除，实际线上一般不给删除权限，无意义，可以根据项目情况注释掉
     *
     * @param pks
     * @return
     */
    int deleteByPrimaryKey(@NotEmpty PK... pks);

    /**
     * 插入
     *
     * @param records
     * @return
     */
    int insert(@NotEmpty T... records);

    /**
     * 根据主键查询
     *
     * @param pk
     * @return
     */
    T selectByPrimaryKey(@NotNull PK pk);

    /**
     * 查询
     *
     * @param condition
     * @return
     */
    List<T> selectByQuery(Page<T> page, @Param("query") AbstractQuery condition);
    List<T> selectByQuery(@Param("query") AbstractQuery condition);
    /**
     * 选择性更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(@NotNull T record);

    /**
     * 主键全字段更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(@NotNull T record);
}