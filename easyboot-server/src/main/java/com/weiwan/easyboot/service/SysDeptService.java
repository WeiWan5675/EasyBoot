package com.weiwan.easyboot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.weiwan.easyboot.mapper.SysDeptMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.weiwan.easyboot.model.dto.Tree;
import com.weiwan.easyboot.model.entity.SysDept;
import com.weiwan.easyboot.model.entity.SysDeptQuery;
import com.weiwan.easyboot.utils.IdGen;
import com.weiwan.easyboot.utils.TreeHelper;

/**
 * 组织机构
 *
 * @author easyboot-generator 2021年1月12日 下午10:54:44
 */
@Service
public class SysDeptService extends AbstractCrudService<SysDeptMapper, SysDept, Integer> {

    @Transactional(readOnly = true)
    public SysDept findByNameAndParentId(@NotEmpty String name, @NotNull Integer parentId) {
        SysDeptQuery sysDeptQuery = new SysDeptQuery();
        sysDeptQuery.setName(name);
        sysDeptQuery.setParentId(parentId);
        return this.findOne(sysDeptQuery);
    }

    /**
     * 查询子节点
     *
     * @param parentId
     * @return
     */
    @Transactional(readOnly = true)
    public List<SysDept> findByParentId(@NotNull Integer parentId) {
        SysDeptQuery sysDeptQuery = new SysDeptQuery();
        sysDeptQuery.setParentId(parentId);
        return this.find(sysDeptQuery);
    }

    /**
     * 找到自己的所有孩子
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<SysDept> findAllChildren(@NotNull Integer id) {
        SysDeptQuery sysDeptQuery = new SysDeptQuery();
        sysDeptQuery.setParentIds(TreeHelper.getQueryParentIds(id));
        return this.find(sysDeptQuery);
    }

    public int save(@NotNull SysDept record) {
        this.resolveParentIds(record);
        return super.save(record);
    }

    @Override
    public int updateSelective(@NotNull SysDept record) {
        // 检查是否修改过父部门
        SysDept current = this.find(record.getId());
        if (Objects.equals(current.getParentId(), record.getParentId())) {
            return super.updateSelective(record);
        } else {
            String oldParentIds = current.getParentIds();
            this.resolveParentIds(record);

            this.findAllChildren(record.getId()).forEach((child) -> {
                child.setParentIds(child.getParentIds().replace(oldParentIds, oldParentIds));
                super.updateSelective(child);
            });
            return super.updateSelective(record);
        }
    }

    /**
     * 生成新的parentIds
     *
     * @param record
     */
    private void resolveParentIds(SysDept record) {
        // 处理parentIds
        Integer parentId = record.getParentId();
        Assert.notNull(parentId, "parentId must be not null");
        if (parentId == TreeHelper.DEFAULT_PARENT_ID) {
            record.setParentIds(TreeHelper.DEFAULT_PARENT_IDS);
        } else {
            SysDept parent = this.find(parentId);
            Assert.notNull(parent, "父部门不存在");
            record.setParentIds(TreeHelper.getCurrentParentIds(record.getId(), parentId, parent.getParentIds()));
        }
    }

    /**
     * 删除本部门及子部门
     *
     * @param id
     * @return
     */
    public int delete(@NotNull @Min(1) Integer id) {
        List<Integer> childrenIds = this.findAllChildren(id).stream().map(SysDept::getId).collect(Collectors.toList());
        childrenIds.add(id);
        int cnt = super.delete(childrenIds.toArray(new Integer[]{}));
        logger.info("delete dept size:[{}]", cnt);
        return cnt;
    }

    /**
     * 查询树结构
     *
     * @param parentId
     * @param includeChild
     *            是否包含子节点的子节点
     * @return
     */
    @Transactional(readOnly = true)
    public List<Tree> findTree(@NotNull Integer parentId, boolean includeChild) {
        List<Tree> trees = new ArrayList<>();
        List<SysDept> depts = this.findByParentId(parentId);
        depts.forEach((dept) -> {
            Tree tree = Tree.builder().key(IdGen.uuid()).value(dept.getId()).title(dept.getName())
                .children(includeChild ? this.findTree(dept.getId(), includeChild) : null).selectable(true).build();
            if (includeChild) {
                tree.setLeaf(CollectionUtils.isEmpty(tree.getChildren()) ? true : false);
            }
            trees.add(tree);
        });
        return trees;
    }

}
