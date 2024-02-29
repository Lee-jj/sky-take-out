package com.sky.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单明细数据
     * @param orderDetailsList
     */
    void insertBatch(List<OrderDetail> orderDetailsList);

    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
    
    List<GoodsSalesDTO> getTop10ByTime(LocalDateTime begin, LocalDateTime end, Integer status);

    /**
     * 批量插入订单明细数据t_order_detail
     * @param orderDetailsList
     */
    void insertBatchv1(List<OrderDetail> orderDetailsList);

    /**
     * 根据订单id查询菜品列表
     * @param orderId
     * @return
     */
    @Select("select * from t_order_detail where order_id = #{orderId}")
    List<OrderDetail> listByOrderId(Long orderId);

}
