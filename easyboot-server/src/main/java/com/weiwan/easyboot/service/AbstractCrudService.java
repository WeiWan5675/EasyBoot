package com.weiwan.easyboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weiwan.easyboot.model.PageWrapper;
import com.weiwan.easyboot.security.SecurityUtils;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.weiwan.easyboot.mapper.CrudMapper;
import com.weiwan.easyboot.model.entity.AbstractQuery;
import com.weiwan.easyboot.model.entity.BaseEntity;
import com.weiwan.easyboot.utils.IdGen;

/**
 * 增删改查service
 *
 * @author xiaozhennan
 */
public abstract class AbstractCrudService<D extends CrudMapper<T, PK>, T extends BaseEntity<PK>, PK>
    extends AbstractTransactionService {

    @SuppressWarnings("all")
    @Autowired
    protected D d;

    /**
     * 根据主键查询
     *
     * @param pk
     * @return
     */
    @Transactional(readOnly = true)
    public T find(@NotNull PK pk) {
        return this.d.selectByPrimaryKey(pk);
    }

    /**
     * 更具条件返回一个
     *
     * @param query
     *            可以为null
     * @return
     */
    @Transactional(readOnly = true)
    public T findOne(AbstractQuery query) {
        List<T> ts = this.find(query);
        Assert.isTrue(ts.size() <= 1,
            "Expected one result (or null) to be returned by findOne(), but found: " + ts.size());
        return ts.isEmpty() ? null : ts.get(0);
    }

    /**
     * 自定义条件查询
     *
     * @param query
     * @return
     */
    @Transactional(readOnly = true)
    public List<T> find(Page<T> page, AbstractQuery query) {
        return this.d.selectByQuery(page, query);
    }

    /**
     * 自定义条件查询
     *
     * @param query
     * @return
     */
    @Transactional(readOnly = true)
    public List<T> find(AbstractQuery query) {
        return this.d.selectByQuery(query);
    }

    /**
     * 分页查询
     *
     * @param condition
     * @param pageNum
     * @param pageSize
     * @param count
     * @return
     */
    @Transactional(readOnly = true)
    public PageWrapper<T> find(AbstractQuery condition, int pageNum, int pageSize, boolean count) {
        Page<T> page = new Page<>(pageNum, pageSize, count);
        List<T> list = this.find(page, condition);
        PageWrapper<T> pageWrapper = new PageWrapper<T>(list, page.getTotal(), pageNum, pageSize);
        return pageWrapper;
    }

    /**
     * 分页查询
     * @param condition
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageWrapper<T> find(AbstractQuery condition, int pageNum, int pageSize) {
        Page<T> page = new Page(pageNum, pageSize, true);
        List<T> list = this.find(page, condition);
        PageWrapper<T> pageWrapper = new PageWrapper<T>(list, page.getTotal(), pageNum, pageSize);
        return pageWrapper;
    }

    /**
     * 插入支持批量
     *
     * @param records
     * @return
     */
    public int save(@NotEmpty T... records) {
        Arrays.stream(records).forEach((t) -> {
            // 当为空且是字符串类型时候，默认为其生成主键
            if (null == t.getId() && t.getId() instanceof String) {
                t.setId((PK)IdGen.uuid());
            }
            t.setCreateBy(SecurityUtils.getUserContextId());
            t.setUpdateBy(SecurityUtils.getUserContextId());
            t.setCreateTime(new Date());
            t.setUpdateTime(t.getCreateTime());
        });
        return this.d.insert(records);
    }

    /**
     * 按主键选择性更新，默认自动修改更新时间及更新人
     *
     * @param record
     * @return
     */
    public int updateSelective(@NotNull T record) {
        record.setUpdateTime(new Date());
        record.setUpdateBy(SecurityUtils.getUserContextId());
        return this.d.updateByPrimaryKeySelective(record);
    }

    /**
     * 按主键全量更新,默认自动修改更新时间及更新人
     *
     * <pre>
     * 默认不更新创建时间及创建时间
     * </pre>
     *
     * @param record
     * @return
     */
    public int update(@NotNull T record) {
        record.setUpdateTime(new Date());
        record.setUpdateBy(SecurityUtils.getUserContextId());
        return this.d.updateByPrimaryKey(record);
    }

    /**
     * 按主键删除，一般业务在逻辑上保证不会删除，如果非要删除建议使用逻辑删除
     *
     * <pre>
     * 该方法不做权限控制
     * </pre>
     *
     * @param pks
     * @return
     */
    public int delete(@NotEmpty PK... pks) {
        return this.d.deleteByPrimaryKey(pks);
    }
}
