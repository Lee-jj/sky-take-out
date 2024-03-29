package com.sky.service;

import com.sky.dto.OrderSubmitDTOv1;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.TOrderRush;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);
    
    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询用户历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    PageResult page(Integer page, Integer pageSize, Integer status);

    /**
     * 查询订单详情
     * @param id
     * @return
     */
    OrderVO detail(Long id);

    /**
     * 取消订单
     * @param id
     * @return
     */
    void cancel(Long id);

    /**
     * 再来一单
     * @param id
     * @return
     */
    void repetition(Long id);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistic();

    /**
     * 确认订单
     * @param ordersConfirmDTO
     * @return
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     * @return
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 商家取消订单
     * @return
     */
    void adminCancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 派送订单
     * @param id
     * @return
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id
     * @return
     */
    void complete(Long id);

    /**
     * 用户催单
     * @param id
     * @return
     */
    void reminder(Long id);

    /**
     * 下单
     * @param orderSubmitDTO
     * @return
     */
    OrderSubmitVO submitv1(OrderSubmitDTOv1 orderSubmitDTO);

    /**
     * 分页查询当前可接订单
     * @param page
     * @param pageSize
     * @return
     */
    PageResult pageAvaliableOrder(Integer page, Integer pageSize);

    /**
     * 用户抢单
     * @param id
     */
    void doSeckill(Long id);

    /**
     * 获取抢单结果
     * @param userId
     * @param orderId
     * @return
     */
    Long getSeckillResult(Long orderId);
}
