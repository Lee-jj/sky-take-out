package com.sky.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData() {
        LocalDateTime beginTime = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.now().with(LocalTime.MAX);

        // 营业额
        Double turnover = orderMapper.getSumByTimeAndStatus(beginTime, endTime, Orders.COMPLETED);

        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);

        // 总订单数
        Integer orderCount = orderMapper.getOrderCountByTimeAndStatus(map);

        // 新增用户数
        Integer newUsers = userMapper.getSumByTime(map);

        map.put("status", Orders.COMPLETED);

        // 有效订单数
        Integer validOrderCount = orderMapper.getOrderCountByTimeAndStatus(map);

        return BusinessDataVO.builder()
                .turnover(turnover == null ? 0.0 : turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCount == 0 ? 0 : validOrderCount * 1.0 / orderCount)
                .unitPrice(validOrderCount == 0 ? 0 : turnover / validOrderCount)
                .newUsers(newUsers)
                .build();
    }
    
}
