package com.citms.annotation;

import java.lang.annotation.*;

/**
 * 用于切换mybatis plus默认数据源
 * @author pei.wang
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SetDSPrimary {
}
