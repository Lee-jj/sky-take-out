package com.sky.rabbitmq;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.sky.config.RabbitMQTopicConfig;
import com.sky.entity.TOrder;
import com.sky.entity.TOrderRush;
import com.sky.mapper.OrderMapper;
import com.sky.pojo.SeckillMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class MQReceiver {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = RabbitMQTopicConfig.QUEUE)
    public void receive(String msg) {
        log.info("接收到消息：{}", msg);
        SeckillMessage seckillMessage = JSONObject.parseObject(msg, SeckillMessage.class);
        Long userId = seckillMessage.getUserId();
        Long orderId = seckillMessage.getOrderId();

        // 把原本数据库的操作移动到这里，同时删除Redis缓存中的订单数据
        // 修改订单表中的订单状态为已接单
        TOrder order = TOrder.builder()
                .id(orderId)
                .status(TOrder.CONFIRMED)
                .build();
        orderMapper.updatev1(order);

        // 向抢单表中添加记录
        TOrderRush orderRush = new TOrderRush();
        orderRush.setOrderId(orderId);
        orderRush.setUserId(userId);
        orderRush.setOrderTime(LocalDateTime.now());
        orderMapper.insertRush(orderRush);

    }
}