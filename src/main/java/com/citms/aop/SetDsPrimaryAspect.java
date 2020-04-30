package com.citms.aop;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.mybatisplus.enums.DBType;
import com.citms.startup.DataBaseContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author pei.wang
 */
@Slf4j
@Component
@Aspect
public class SetDsPrimaryAspect {

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Pointcut("@annotation(com.citms.annotation.SetDSPrimary)")
    public void pointCut(){}

    @Around(value = "pointCut()")
    public Object setDs(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        final Object[] args = point.getArgs();//参数
        dynamicRoutingDataSource.setPrimary("mysql");
        DataBaseContextHolder.DB_TYPE = DataBaseContextHolder.dbType.get(dynamicDataSourceProperties.getDatasource().get("mysql").getDriverClassName());
        log.info("");
        return point.proceed();
    }
}
