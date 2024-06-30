package com.sky.exception;

/**
 * 限流异常
 */
public class RateLimiterException extends BaseException {
    public RateLimiterException() {

    }

    public RateLimiterException(String msg) {
        super(msg);
    }
}
