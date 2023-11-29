package com.sky.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查找订单
     * @param outTradeNo
     * @return
     */
    @Select("select * from orders where number = #{outTradeNo}")
    Orders getByNumber(String outTradeNo);

    /**
     * 更新订单
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页查询
     * @param orderPageQueryDTO
     * @return
     */
    Page<Orders> page(OrdersPageQueryDTO orderPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 根据状态查询订单数量
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据状态和订单时间查询
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 根据时间段和状态查询营业总额度
     * @param beginTime
     * @param endTime
     * @param completed
     * @return
     */
    @Select("select sum(amount) from orders where order_time >= #{beginTime} and order_time <= #{endTime} and status = #{status}")
    Double getSumByTimeAndStatus(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 根据订单状态和起始时间查询订单数量
     */
    Integer getOrderCountByTimeAndStatus(Map map);
}
