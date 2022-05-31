package com.weiwan.easyboot.common;

import lombok.Data;

/**
 * @Author: xiaozhennan
 * @Date: 2022/5/29 18:01
 * @Package: com.weiwan.easyboot.common
 * @ClassName: DatabaseType
 * @Description:
 **/
public enum DatabaseType {
    MYSQL("mysql");
    private String type;

    DatabaseType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
