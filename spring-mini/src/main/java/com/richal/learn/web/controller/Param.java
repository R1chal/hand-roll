package com.richal.learn.web.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数别名注解。
 * 为什么需要：
 * - URL 上的查询参数名与方法参数名可能不一致；
 * - 编译未加 -parameters 时，方法参数名会变成 arg0/arg1，无法按名称绑定；
 * 故提供 @Param 显式声明要读取的查询参数名，保证绑定稳定可控。
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String value() default "";
}
