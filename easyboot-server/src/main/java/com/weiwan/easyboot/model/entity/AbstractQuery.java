package com.weiwan.easyboot.model.entity;

/**
 * 查询基类
 *
 * @author xiaozhennan
 */
public abstract class AbstractQuery {

    /**
     * 重载的方法当传null 语法错误问题
     */
    public static final EmptyQuery EMPTY = null;

    private static final class EmptyQuery extends AbstractQuery {

    }
}
