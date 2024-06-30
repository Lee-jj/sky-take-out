package com.sky.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.springframework.core.annotation.AliasFor;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    
    int NOT_LIMITED = 0;

    /**
     * qps
     * @return
     */
    @AliasFor("qps")
    double value() default NOT_LIMITED;

    /**
     * qps别名
     * @return
     */
    @AliasFor("value")
    double qps() default NOT_LIMITED;

    /**
     * 超时时长
     * @return
     */
    int timeout() default 0;

    /**
     * 超时时长单位
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
