package com.sky.controller.notify;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.annotation.RateLimiter;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试类
 */
@RestController
@Slf4j
public class HelloController {
    
    @RateLimiter(value = 1, timeout = 100)
    @GetMapping("/hello")
    public String printHello() {
        log.info("执行限流");
        return "限流测试~";
    }
}
