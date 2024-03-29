package com.sky.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.config.RabbitMQTopicConfig;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MQSender {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSeckillMessage(String msg) {
        log.info("发送消息：{}", msg);
        rabbitTemplate.convertAndSend(RabbitMQTopicConfig.EXCHANGE, "seckill.message", msg);
    }
}
