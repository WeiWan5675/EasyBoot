package com.weiwan.easyboot.component.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.weiwan.easyboot.config.BootProperties;
import com.weiwan.easyboot.model.dto.UserInfo;
import com.weiwan.easyboot.security.SecurityUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.schema.Column;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MultiTenantLineHandler implements TenantLineHandler {

    private BootProperties.MultiTenantProperties multiTenantProperties;


    public MultiTenantLineHandler(@Autowired BootProperties bootProperties) {
        this.multiTenantProperties = bootProperties.getMultiTenant();
    }

    @Override
    public Expression getTenantId() {
        UserInfo user = SecurityUtils.getUser();
        if(user != null) {
            return new LongValue(user.getUserId());
        }
        return null;
    }

    @Override
    public String getTenantIdColumn() {
        String tenantColumn = multiTenantProperties.getTenantColumn();
        if (StringUtils.isNotBlank(tenantColumn)) {
            return tenantColumn;
        }
        return TenantLineHandler.super.getTenantIdColumn();
    }

    @Override
    public boolean ignoreTable(String tableName) {
        UserInfo user = SecurityUtils.getUser();
        if(user == null) {
            return TenantLineHandler.super.ignoreTable(tableName);
        }
        String includes = multiTenantProperties.getIncludes();
        String ignores = multiTenantProperties.getIgnores();
        if (StringUtils.isNotBlank(includes)) {
            String[] tables = includes.split(",");
            if (tables != null && tables.length > 0) {
                return !Arrays.stream(tables).anyMatch(table -> table.equalsIgnoreCase(tableName));
            }
        }
        if (StringUtils.isNotBlank(ignores)) {
            String[] tables = ignores.split(",");
            if (tables != null && tables.length > 0) {
                return Arrays.stream(tables).anyMatch(table -> table.equalsIgnoreCase(tableName));
            }
        }
        return TenantLineHandler.super.ignoreTable(tableName);
    }

    @Override
    public boolean ignoreInsert(List<Column> columns, String tenantIdColumn) {
        return TenantLineHandler.super.ignoreInsert(columns, tenantIdColumn);
    }
}

