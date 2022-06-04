package com.weiwan.easyboot.component.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.weiwan.easyboot.annotation.DataAuthority;
import com.weiwan.easyboot.config.BootProperties;
import com.weiwan.easyboot.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class DataAuthorityHandler implements DataPermissionHandler {

    private BootProperties.DataAuthorityProperties dataAuthorityProperties;
    private BootProperties.DataAuthorityMode dataAuthorityMode;
    private final ThreadLocal<DataAuthority> threadLocal = new ThreadLocal<>();

    public DataAuthorityHandler(@Autowired BootProperties bootProperties) {
        this.dataAuthorityProperties = bootProperties.getDataAuthority();
        this.dataAuthorityMode = dataAuthorityProperties.getMode();
    }

    /**
     * 配置切入点, 有该注解则进入 {@link DataAuthority}
     */
    @Pointcut("@annotation(com.weiwan.easyboot.annotation.DataAuthority)")
//        @Pointcut("execution(public * com.weiwan.dsp.console..*Controller.*(..))&&!@annotation(com.weiwan.dsp.console.aspect.LogIgnore)"
    public void dataAuthorityPointCut() {
    }

    @Before("dataAuthorityPointCut()")
    public void doBefore(JoinPoint point) {
        if (dataAuthorityProperties.isEnable()) return;
        Signature signature = point.getSignature();
        DataAuthority dataAuthority = null;
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            if (method != null) {
                dataAuthority = method.getAnnotation(DataAuthority.class);
            }
        }
        if (dataAuthority == null) {
            Object target = point.getTarget();
            dataAuthority = target.getClass().getAnnotation(DataAuthority.class);
        }
        if (dataAuthority != null) {
            threadLocal.set(dataAuthority);
        }
    }

    /**
     * 配置中可以选择是否开启数据权限,如果开启,默认全局模式, 需要所有表都有create_by字段
     * 如果模式选择了 {@link BootProperties.DataAuthorityMode#ANNOTATION}
     * 则数据权限的开启由注解来控制
     */
    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        // 如果没有开启, 直接返回
        if (!dataAuthorityProperties.isEnable()) return where;
        // 如果是注解模式, 则获取注解进行判断
        if (dataAuthorityMode == BootProperties.DataAuthorityMode.ANNOTATION) {
            //如果是注解模式
            DataAuthority dataAuthority = threadLocal.get();
            // 如果没有发现注解或者注解是ignore 直接返回
            if (dataAuthority == null || dataAuthority.ignore()) {
                return where;
            }
        }
        Expression condition = null;
        try {
            String dsf = SecurityUtils.getDsf();
            condition = CCJSqlParserUtil.parseCondExpression(dsf);
        } catch (JSQLParserException e) {
            log.debug("Parse data permission sql error", e);
        }
        // 找不到用户对应的数据权限Dsf
        if (condition == null) {
            return where;
        }
        return new AndExpression(where, condition);
    }

    public void clearThreadLocal() {
        this.threadLocal.remove();
    }
}