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
import java.util.Map;

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
        Map<String,String> map = (Map<String, String>) args[1];
        String dataSource =  map.get("datasource");
        dynamicRoutingDataSource.setPrimary(dataSource);
        DataBaseContextHolder.DB_TYPE = DataBaseContextHolder.dbType.get(dynamicDataSourceProperties.getDatasource().get(dataSource).getDriverClassName());
        log.info("");
        return point.proceed();
    }
}
