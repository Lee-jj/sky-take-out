package com.sky.aspect;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.sky.annotation.RateLimiter;
import com.sky.constant.MessageConstant;
import com.sky.exception.RateLimiterException;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class RateLimiterAspect {

    private static final ConcurrentMap<String, com.google.common.util.concurrent.RateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 切入点
     */
    @Pointcut("@annotation(com.sky.annotation.RateLimiter)")
    public void rateLimiterPointCut(){}

    /**
     * 前置通知，限流
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Before("rateLimiterPointCut()")
    public void rateLimit(JoinPoint joinPoint) throws Throwable {

        // 获取annotation中的description值，即操作描述
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        // 通过 AnnotationUtils.findAnnotation 获取 RateLimiter 注解
        RateLimiter rateLimiter = AnnotationUtils.findAnnotation(method, RateLimiter.class);

        if (null != rateLimiter && rateLimiter.qps() > RateLimiter.NOT_LIMITED) {
            double qps = rateLimiter.qps();
            if (RATE_LIMITER_CACHE.get(methodName) == null) {
                RATE_LIMITER_CACHE.put(methodName, com.google.common.util.concurrent.RateLimiter.create(qps));
            }
        }

        log.info("[{}] 的QPS设置为: {}", methodName, RATE_LIMITER_CACHE.get(methodName).getRate());
        
        // 尝试获取令牌
        if (null != RATE_LIMITER_CACHE.get(methodName) && !RATE_LIMITER_CACHE.get(methodName).tryAcquire(rateLimiter.timeout(), rateLimiter.timeUnit())) {
            throw new RateLimiterException(MessageConstant.RATE_LIMITER_WARNING);
        }
    }
}
