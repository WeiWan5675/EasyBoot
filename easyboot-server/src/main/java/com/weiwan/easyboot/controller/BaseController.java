package com.weiwan.easyboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;

/**
 *
 * @author xiaozhennan
 */
@Validated
public abstract class BaseController {
    /**
     * 提供日志对象
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());
}
