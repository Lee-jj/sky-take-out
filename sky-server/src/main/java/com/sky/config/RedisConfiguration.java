package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RedisConfiguration {
    // 创建Redis配置类
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建Redis模板对象...");
        RedisTemplate redisTemplate = new RedisTemplate<>();
        // 设置Redis的连接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置 redis key 的序列化器，方便在图形化界面中查看
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
