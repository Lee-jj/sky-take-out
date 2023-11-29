package com.sky.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

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

    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO getOverviewSetmeals() {
        // 起售数量
        Integer sold = setmealMapper.getCountByStatus(StatusConstant.ENABLE);
        // 停售数量
        Integer discontinued = setmealMapper.getCountByStatus(StatusConstant.DISABLE);
        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO getOverviewDishes() {
        // 起售数量
        Integer sold = dishMapper.getCountByStatus(StatusConstant.ENABLE);
        // 停售数量
        Integer discontinued = dishMapper.getCountByStatus(StatusConstant.DISABLE);

        return DishOverViewVO.builder()
               .sold(sold)
               .discontinued(discontinued)
               .build();
    }

    /**
     * 查询订单总览
     * @return
     */
    @Override
    public OrderOverViewVO getOverviewOrders() {

        Map map = new HashMap();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
        map.put("end", LocalDateTime.now().with(LocalTime.MAX));

        // 全部订单数
        Integer allOrders = orderMapper.getOrderCountByTimeAndStatus(map);

        // 已取消订单数
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.getOrderCountByTimeAndStatus(map);

        // 已完成订单数
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.getOrderCountByTimeAndStatus(map);

        // 待派送(已接单)订单数
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.getOrderCountByTimeAndStatus(map);

        // 待接单订单数
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.getOrderCountByTimeAndStatus(map);

        return OrderOverViewVO.builder()
               .allOrders(allOrders)
               .cancelledOrders(cancelledOrders)
               .completedOrders(completedOrders)
               .waitingOrders(waitingOrders)
               .deliveredOrders(deliveredOrders)
               .build();
    }
    
}
